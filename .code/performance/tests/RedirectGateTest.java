import hollowrealm.performance.HRFixesMixinPlugin;
import java.nio.file.*;
import java.util.*;
import java.util.jar.*;
import net.minecraftforge.fml.loading.LoadingModList;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;
import org.spongepowered.asm.service.MixinService;

/** Executes the production gate against original target bytes and in-memory mutations.
 * Forge discovery/resource access are test doubles; this is not a Forge launch. */
public final class RedirectGateTest {
    static int failures;
    static int checks;
    static final String OWNER = "net/minecraft/world/level/Level";
    static final String FIELD = "f_46441_";
    static final String DESC = "Lnet/minecraft/util/RandomSource;";

    static void check(boolean result, String label) {
        checks++;
        if (!result) failures++;
        System.out.println((result ? "PASS " : "FAIL ") + label);
    }
    static boolean apply(String target, String mixin) {
        return new HRFixesMixinPlugin().shouldApplyMixin(target, "hollowrealm.performance.mixin." + mixin);
    }
    static ClassNode node(byte[] bytes) {
        ClassNode n = new ClassNode();
        new ClassReader(bytes).accept(n, 0);
        return n;
    }
    static byte[] bytes(ClassNode n) {
        ClassWriter w = new ClassWriter(0);
        n.accept(w);
        return w.toByteArray();
    }
    static MethodNode method(ClassNode n, String name, String desc) {
        return n.methods.stream().filter(m -> m.name.equals(name) && m.desc.equals(desc)).findFirst().orElseThrow();
    }
    static boolean site(AbstractInsnNode i) {
        return i instanceof FieldInsnNode f && f.getOpcode() == Opcodes.GETFIELD
            && f.owner.equals(OWNER) && f.name.equals(FIELD) && f.desc.equals(DESC);
    }
    static void runTarget(Path jar, String target, String mixin, String mod, String version, String name, String desc) throws Exception {
        byte[] original;
        try (JarFile z = new JarFile(jar.toFile())) {
            original = z.getInputStream(z.getJarEntry(target.replace('.', '/') + ".class")).readAllBytes();
        }
        LoadingModList.versions.clear();
        LoadingModList.versions.put(mod, version);
        MixinService.bytes = original;
        MixinService.resource = target.replace('.', '/') + ".class";
        ClassNode n = node(original);
        int count = 0;
        for (var i : method(n, name, desc).instructions) if (site(i)) count++;
        check(count == 1, mixin + " original binary has exactly one selector match");
        check(apply(target, mixin), mixin + " original target APPLY");
        for (String mutation : List.of("missing-site", "duplicate-site", "owner", "name", "descriptor", "opcode", "method-name", "method-descriptor")) {
            n = node(original);
            MethodNode m = method(n, name, desc);
            FieldInsnNode f = null;
            for (var i : m.instructions) if (site(i)) { f = (FieldInsnNode)i; break; }
            switch (mutation) {
                case "missing-site": m.instructions.remove(f); break;
                case "duplicate-site": m.instructions.insert(f, new FieldInsnNode(Opcodes.GETFIELD, OWNER, FIELD, DESC)); break;
                case "owner": f.owner = "example/OtherLevel"; break;
                case "name": f.name = "otherRandom"; break;
                case "descriptor": f.desc = "Ljava/util/Random;"; break;
                case "opcode": m.instructions.set(f, new FieldInsnNode(Opcodes.GETSTATIC, OWNER, FIELD, DESC)); break;
                case "method-name": m.name = "changedMethod"; break;
                case "method-descriptor": m.desc = "(I)V"; break;
            }
            MixinService.bytes = bytes(n);
            check(!apply(target, mixin), mixin + " " + mutation + " SKIP");
        }
        // The selector belongs to the named method, not the whole class.
        n = node(original);
        MethodNode other = new MethodNode(Opcodes.ACC_PRIVATE, "unrelatedFixture", "()V", null, null);
        other.instructions.add(new FieldInsnNode(Opcodes.GETFIELD, OWNER, FIELD, DESC));
        other.instructions.add(new InsnNode(Opcodes.RETURN));
        n.methods.add(other);
        MixinService.bytes = bytes(n);
        check(apply(target, mixin), mixin + " extra site in unrelated method APPLY");
        MixinService.bytes = null;
        check(!apply(target, mixin), mixin + " absent resource SKIP");
        for (byte[] malformed : List.of(new byte[0], new byte[]{1,2,3,4}, Arrays.copyOf(original, 24))) {
            MixinService.bytes = malformed;
            boolean skipped;
            try { skipped = !apply(target, mixin); } catch (RuntimeException e) { skipped = false; }
            check(skipped, mixin + " malformed resource length=" + malformed.length + " SKIP without throwing");
        }
        MixinService.bytes = original;
        MixinService.ioFailure = true;
        check(!apply(target, mixin), mixin + " I/O failure SKIP");
        MixinService.ioFailure = false;
        LoadingModList.versions.clear();
        int before = MixinService.reads;
        check(!apply(target, mixin) && MixinService.reads == before, mixin + " missing mod SKIP without reading target");
        LoadingModList.versions.put(mod, "999.0");
        check(!apply(target, mixin) && MixinService.reads == before, mixin + " unknown version SKIP without reading target");
    }
    static void signature(String target, String mixin, Map<String,String> mods, String name, String desc) {
        LoadingModList.versions.clear(); LoadingModList.versions.putAll(mods);
        ClassNode n = new ClassNode(); n.version = Opcodes.V17; n.access = Opcodes.ACC_PUBLIC;
        n.name = target.replace('.', '/'); n.superName = "java/lang/Object";
        n.methods.add(new MethodNode(Opcodes.ACC_PUBLIC, name, desc, null, null));
        MixinService.resource = n.name + ".class"; MixinService.bytes = bytes(n);
        check(apply(target, mixin), mixin + " audited signature still APPLY (gate test only)");
        n.methods.clear(); MixinService.bytes = bytes(n);
        check(!apply(target, mixin), mixin + " missing signature still SKIP");
        LoadingModList.versions.clear();
        check(!apply(target, mixin), mixin + " missing dependencies SKIP");
    }
    public static void main(String[] args) throws Exception {
        runTarget(Path.of(args[0]), "com.k1sak1.goetyawaken.common.entities.ally.illager.SorcererServant", "SorcererWorldgenRandomMixin",
            "goetyawaken", "1.3.9.4", "generateOffers", "()V");
        runTarget(Path.of(args[1]), "com.qiuyue.goetyominous.common.entities.hostile.cultists.Zealot", "ZealotWorldgenRandomMixin",
            "goetyominous", "3.0.0.fix-1.20.1", "populateDefaultEquipmentSlots", "(Lnet/minecraft/world/DifficultyInstance;)V");
        signature("cn.leolezury.eternalstarlight.common.network.SyncAttachmentsPacket", "AttachmentClientMixin",
            Map.of("eternal_starlight", "0.9.0"), "handle", "(Lnet/minecraft/world/entity/player/Player;)V");
        signature("dev.ftb.mods.ftbquests.client.gui.quests.ViewQuestPanel", "QuestPanelLifecycleMixin",
            Map.of("ftbquests", "2001.4.22", "quest_enhance", "1.20.1-forge-3.13"), "addWidgets", "()V");
        signature("mezz.jei.library.plugins.vanilla.anvil.SmithingRecipeCategory", "SocketingJeiLayoutMixin",
            Map.of("apotheosis", "7.4.8", "jei", "15.56.0.205"), "setRecipe", "(Lmezz/jei/api/gui/builder/IRecipeLayoutBuilder;Lnet/minecraft/world/item/crafting/SmithingRecipe;Lmezz/jei/api/recipe/IFocusGroup;)V");
        LoadingModList.versions.clear();
        check(!apply("example.Target", "ApotheosisJeiMixin"), "Apotheosis missing dependencies SKIP");
        LoadingModList.versions.putAll(Map.of("apotheosis", "7.4.8", "jei", "15.56.0.205"));
        check(apply("example.Target", "ApotheosisJeiMixin"), "Apotheosis audited gate APPLY");
        LoadingModList.versions.put("jei", "999");
        check(!apply("example.Target", "ApotheosisJeiMixin"), "Apotheosis unknown JEI SKIP");
        for (String coo : List.of("absent", "4.6", "6.5", "4.8", "999")) {
            for (String struct : List.of("absent", "2.0.42", "2.0.43", "999")) {
                LoadingModList.versions.clear();
                if (!coo.equals("absent")) LoadingModList.versions.put("collections_of_optimizations", coo);
                if (!struct.equals("absent")) LoadingModList.versions.put("structurify", struct);
                boolean expected = (coo.equals("4.6") || coo.equals("6.5")) && (struct.equals("2.0.42") || struct.equals("2.0.43"));
                check(HRFixesMixinPlugin.shouldDisableStructurifyOverlap() == expected, "CoO gate " + coo + " / " + struct);
            }
        }
        check(!apply("example.Target", "UnknownMixin"), "unknown mixin SKIP");
        LoadingModList.absent = true;
        check(!apply("example.Target", "SorcererWorldgenRandomMixin"), "absent discovery list SKIP");
        System.out.println("RESULT checks=" + checks + " failures=" + failures);
        if (failures != 0) System.exit(1);
    }
}
