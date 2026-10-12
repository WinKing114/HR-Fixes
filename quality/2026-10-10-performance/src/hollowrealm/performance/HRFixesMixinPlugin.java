package hollowrealm.performance;

import java.util.List;
import java.util.Set;
import net.minecraftforge.fml.loading.LoadingModList;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

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

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        String mixin = mixinClassName.substring(mixinClassName.lastIndexOf('.') + 1);
        switch (mixin) {
            case "AttachmentClientMixin":
                return tested("eternal_starlight", "0.9.0");
            case "SorcererWorldgenRandomMixin":
                return tested("goetyawaken", "1.3.9.4");
            case "ZealotWorldgenRandomMixin":
                return tested("goetyominous", "3.0.0.fix-1.20.1");
            case "QuestPanelLifecycleMixin":
                return tested("ftbquests", "2001.4.22")
                    && tested("quest_enhance", "1.20.1-forge-3.13");
            case "ApotheosisJeiMixin":
            case "SocketingJeiLayoutMixin":
                return tested("apotheosis", "7.4.8")
                    && tested("jei", "15.56.0.205");
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
