package hollowrealm.performance;

import java.util.List;
import java.util.Set;
import net.minecraftforge.fml.loading.LoadingModList;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.FieldInsnNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import org.spongepowered.asm.service.MixinService;

/**
 * A patch is only used on the specific target implementation it was audited
 * against. Missing or updated third-party mods are never a loading error.
 */
public final class HRFixesMixinPlugin implements IMixinConfigPlugin {
    @Override
    public void onLoad(String mixinPackage) {}

    @Override
    public String getRefMapperConfig() { return null; }

    private static boolean tested(String modId, String version) {
        LoadingModList list = LoadingModList.get();
        if (list == null) return false;
        for (ModInfo mod : list.getMods()) {
            if (modId.equals(mod.getModId())) {
                return version.equals(mod.getVersion().toString());
            }
        }
        return false;
    }

    public static boolean shouldDisableStructurifyOverlap() {
        return (tested("collections_of_optimizations", "4.6") || tested("collections_of_optimizations", "6.5"))
            && (tested("structurify", "2.0.42") || tested("structurify", "2.0.43"));
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        boolean enabled = compatible(targetClassName, mixinClassName);
        System.getLogger("HR Fixes").log(System.Logger.Level.INFO,
                (enabled ? "APPLY " : "SKIP ") + mixinClassName);
        return enabled;
    }

    private static boolean method(String target, String name, String descriptor) {
        // Read original bytes: the bytecode provider would run other transformers
        // while Mixin is still registering targets, causing premature class loading.
        try (java.io.InputStream stream = MixinService.getService().getResourceAsStream(target.replace('.', '/') + ".class")) {
            if (stream == null) return false;
            ClassNode node = new ClassNode();
            new ClassReader(stream).accept(node, ClassReader.SKIP_CODE | ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
            return node.methods.stream().anyMatch(m -> name.equals(m.name) && descriptor.equals(m.desc));
        } catch (java.io.IOException missing) {
            return false;
        }
    }

    private static boolean randomFieldRedirect(String target, String name, String descriptor) {
        // Match the raw FIELD selector and its require=1/allow=1 contract.
        // Do not ask the bytecode provider to transform an optional target here.
        try (java.io.InputStream stream = MixinService.getService().getResourceAsStream(target.replace('.', '/') + ".class")) {
            if (stream == null) return false;
            ClassNode node = new ClassNode();
            new ClassReader(stream).accept(node, ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
            int methods = 0;
            int sites = 0;
            for (var method : node.methods) {
                if (!name.equals(method.name) || !descriptor.equals(method.desc)) continue;
                methods++;
                for (var instruction : method.instructions) {
                    if (instruction instanceof FieldInsnNode field
                            && field.getOpcode() == Opcodes.GETFIELD
                            && "net/minecraft/world/level/Level".equals(field.owner)
                            && "f_46441_".equals(field.name)
                            && "Lnet/minecraft/util/RandomSource;".equals(field.desc)) {
                        sites++;
                    }
                }
            }
            return methods == 1 && sites == 1;
        } catch (java.io.IOException | RuntimeException unreadable) {
            // Missing, malformed or unsupported optional class bytes must fail closed.
            return false;
        }
    }

    private static boolean compatible(String targetClassName, String mixinClassName) {
        String mixin = mixinClassName.substring(mixinClassName.lastIndexOf('.') + 1);
        switch (mixin) {
            case "AttachmentClientMixin":
                return tested("eternal_starlight", "0.9.0")
                    && method(targetClassName, "handle", "(Lnet/minecraft/world/entity/player/Player;)V");
            case "SorcererWorldgenRandomMixin":
                return tested("goetyawaken", "1.3.9.4") && randomFieldRedirect(targetClassName, "generateOffers", "()V");
            case "ZealotWorldgenRandomMixin":
                return tested("goetyominous", "3.0.0.fix-1.20.1")
                    && randomFieldRedirect(targetClassName, "populateDefaultEquipmentSlots", "(Lnet/minecraft/world/DifficultyInstance;)V");
            case "QuestPanelLifecycleMixin":
                return tested("ftbquests", "2001.4.22")
                    && tested("quest_enhance", "1.20.1-forge-3.13") && method(targetClassName, "addWidgets", "()V");
            case "ApotheosisJeiMixin":
                return tested("apotheosis", "7.4.8")
                    && tested("jei", "15.56.0.205");
            case "SocketingJeiLayoutMixin":
                return tested("apotheosis", "7.4.8") && tested("jei", "15.56.0.205")
                    && method(targetClassName, "setRecipe", "(Lmezz/jei/api/gui/builder/IRecipeLayoutBuilder;Lnet/minecraft/world/item/crafting/SmithingRecipe;Lmezz/jei/api/recipe/IFocusGroup;)V");
            default:
                return false;
        }
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {}

    @Override
    public List<String> getMixins() { return null; }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass,
                         String mixinClassName, IMixinInfo mixinInfo) {}

    @Override
    public void postApply(String targetClassName, ClassNode targetClass,
                          String mixinClassName, IMixinInfo mixinInfo) {}
}
