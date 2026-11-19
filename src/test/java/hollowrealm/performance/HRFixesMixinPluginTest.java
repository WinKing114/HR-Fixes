package hollowrealm.performance;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipFile;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

class HRFixesMixinPluginTest {
    private static final String MODS_PROP = System.getProperty(
            "hrfixes.mods",
            "C:/Users/WinKing/Documents/.minecraft/versions/HollowRealm/mods");
    private static final String CLIENT_SRG_PROP = System.getProperty(
            "hrfixes.srg",
            "C:/Users/WinKing/Documents/.minecraft/libraries/net/minecraft/client/1.20.1-20230612.114412/client-1.20.1-20230612.114412-srg.jar");

    private static final Map<String, byte[]> REAL_CLASSES = new HashMap<>();

    private static final Map<String, String> AUDITED_VERSIONS = Map.of(
            "lodestone", "1.20.1-1.6.4.1",
            "attributeslib", "1.3.7",
            "apotheosis", "7.4.8",
            "jei", "15.56.0.205",
            "l2hostility", "2.5.19",
            "l2damagetracker", "0.4.4",
            "collections_of_optimizations", "5.5",
            "structurify", "2.0.43");

    @BeforeAll
    static void loadRealTargetBytes() throws IOException {
        Path modsDir = Path.of(MODS_PROP);
        loadFromJar(modsDir.resolve("ApothicAttributes-1.20.1-1.3.7.jar"),
                "dev/shadowsoffire/attributeslib/client/AttributesLibClient.class");
        loadFromJar(modsDir.resolve("lodestone-1.20.1-1.6.4.1.jar"),
                "team/lodestar/lodestone/mixin/common/attributeslib/AttributeLibClientMixin.class");
        loadFromJar(modsDir.resolve("[神化] Apotheosis-1.20.1-7.4.8.jar"),
                "dev/shadowsoffire/apotheosis/adventure/compat/AdventureJEIPlugin.class");
        loadFromJar(modsDir.resolve("[JEI物品管理器] jei-1.20.1-forge-15.56.0.205.jar"),
                "mezz/jei/library/plugins/vanilla/anvil/SmithingRecipeCategory.class");
        loadFromJar(modsDir.resolve("[莱特兰-恶意] l2hostility-2.5.19.jar"),
                "dev/xkmc/l2hostility/compat/kubejs/LHKJSPlugin.class");
        loadFromNestedJar(modsDir.resolve("[莱特兰-扩充] l2complements-2.6.1.jar"),
                "META-INF/jarjar/l2damagetracker-0.4.4.jar",
                "dev/xkmc/l2damagetracker/contents/attack/AttackEventHandler.class");
        loadFromJar(Path.of(CLIENT_SRG_PROP),
                "net/minecraft/client/gui/components/ChatComponent.class");
    }

    private static void loadFromJar(Path jar, String entryName) throws IOException {
        try (ZipFile zip = new ZipFile(jar.toFile())) {
            ZipEntry entry = zip.getEntry(entryName);
            if (entry == null) {
                throw new IOException("Missing " + entryName + " in " + jar);
            }
            REAL_CLASSES.put(entryName, zip.getInputStream(entry).readAllBytes());
        }
    }

    private static void loadFromNestedJar(Path outerJar, String innerJarEntry, String entryName) throws IOException {
        try (ZipFile outer = new ZipFile(outerJar.toFile())) {
            ZipEntry inner = outer.getEntry(innerJarEntry);
            if (inner == null) {
                throw new IOException("Missing inner jar " + innerJarEntry + " in " + outerJar);
            }
            try (ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(outer.getInputStream(inner).readAllBytes()))) {
                ZipEntry e;
                while ((e = zis.getNextEntry()) != null) {
                    if (entryName.equals(e.getName())) {
                        REAL_CLASSES.put(entryName, zis.readAllBytes());
                        return;
                    }
                }
            }
        }
        throw new IOException("Missing nested " + entryName);
    }

    @AfterEach
    void cleanup() {
        HRFixesMixinPlugin.resetVersionCacheForTests();
    }

    @Test
    void enablesAllFiveLiveMixinsAgainstRealInstalledModBytes() {
        HRFixesMixinPlugin.setInstalledVersionsForTests(AUDITED_VERSIONS);
        HRFixesMixinPlugin.setResourceLookupForTests(REAL_CLASSES::get);
        HRFixesMixinPlugin plugin = new HRFixesMixinPlugin();

        assertTrue(plugin.shouldApplyMixin("dev.xkmc.l2hostility.compat.kubejs.LHKJSPlugin",
                "hollowrealm.performance.mixin.KubeJsAttackCacheMixin"));
        assertTrue(plugin.shouldApplyMixin("dev.shadowsoffire.apotheosis.adventure.compat.AdventureJEIPlugin",
                "hollowrealm.performance.mixin.ApotheosisJeiMixin"));
        assertTrue(plugin.shouldApplyMixin("mezz.jei.library.plugins.vanilla.anvil.SmithingRecipeCategory",
                "hollowrealm.performance.mixin.SocketingJeiLayoutMixin"));
        assertTrue(plugin.shouldApplyMixin("dev.shadowsoffire.attributeslib.client.AttributesLibClient",
                "hollowrealm.performance.mixin.LodestoneTooltipMixin"));
        assertTrue(plugin.shouldApplyMixin("net.minecraft.client.gui.components.ChatComponent",
                "hollowrealm.performance.mixin.ChatLineHeightGuardMixin"));
    }

    @Test
    void failsClosedWhenVersionsDriftOrRemovedMixinsAreQueriedOrBytesMutate() {
        Map<String, String> drifted = new HashMap<>(AUDITED_VERSIONS);
        drifted.put("jei", "15.99.0.0");
        drifted.put("l2damagetracker", "0.4.99");
        HRFixesMixinPlugin.setInstalledVersionsForTests(drifted);
        HRFixesMixinPlugin.setResourceLookupForTests(path -> {
            byte[] raw = REAL_CLASSES.get(path);
            if (raw != null && path.endsWith("AttributesLibClient.class")) {
                byte[] mutated = raw.clone();
                mutated[mutated.length - 1] ^= 0x01;
                return mutated;
            }
            return raw;
        });

        HRFixesMixinPlugin plugin = new HRFixesMixinPlugin();
        assertFalse(plugin.shouldApplyMixin("dev.xkmc.l2hostility.compat.kubejs.LHKJSPlugin",
                "hollowrealm.performance.mixin.KubeJsAttackCacheMixin"));
        assertFalse(plugin.shouldApplyMixin("dev.shadowsoffire.apotheosis.adventure.compat.AdventureJEIPlugin",
                "hollowrealm.performance.mixin.ApotheosisJeiMixin"));
        assertFalse(plugin.shouldApplyMixin("mezz.jei.library.plugins.vanilla.anvil.SmithingRecipeCategory",
                "hollowrealm.performance.mixin.SocketingJeiLayoutMixin"));
        assertFalse(plugin.shouldApplyMixin("dev.shadowsoffire.attributeslib.client.AttributesLibClient",
                "hollowrealm.performance.mixin.LodestoneTooltipMixin"));

        // Removed 1.x mixins must always fail closed
        assertFalse(plugin.shouldApplyMixin("cn.leolezury.eternalstarlight.common.network.SyncAttachmentsPacket",
                "hollowrealm.performance.mixin.AttachmentClientMixin"));
        assertFalse(plugin.shouldApplyMixin("com.k1sak1.goetyawaken.common.entities.ally.illager.SorcererServant",
                "hollowrealm.performance.mixin.SorcererWorldgenRandomMixin"));
        assertFalse(plugin.shouldApplyMixin("com.qiuyue.goetyominous.common.entities.hostile.cultists.Zealot",
                "hollowrealm.performance.mixin.ZealotWorldgenRandomMixin"));
        assertFalse(plugin.shouldApplyMixin("dev.ftb.mods.ftbquests.client.gui.quests.ViewQuestPanel",
                "hollowrealm.performance.mixin.QuestPanelLifecycleMixin"));
    }

    @Test
    void synchronizesLodestoneLambdaIdempotentlyAndChecksStructurifyGate() {
        ClassNode node = new ClassNode();
        new ClassReader(REAL_CLASSES.get("dev/shadowsoffire/attributeslib/client/AttributesLibClient.class")).accept(node, 0);
        MethodNode target = node.methods.stream()
                .filter(m -> "lambda$applyTextFor$6".equals(m.name))
                .findFirst()
                .orElseThrow();
        assertEquals(0, target.access & Opcodes.ACC_SYNCHRONIZED);

        assertTrue(HRFixesMixinPlugin.applyLodestoneSynchronization(node));
        assertTrue((target.access & Opcodes.ACC_SYNCHRONIZED) != 0);
        // Second pass must remain idempotent
        assertTrue(HRFixesMixinPlugin.applyLodestoneSynchronization(node));
        assertTrue((target.access & Opcodes.ACC_SYNCHRONIZED) != 0);

        // Verify valid class bytes still write out cleanly
        ClassWriter cw = new ClassWriter(0);
        node.accept(cw);
        assertTrue(cw.toByteArray().length > 1000);

        HRFixesMixinPlugin.setInstalledVersionsForTests(AUDITED_VERSIONS);
        assertTrue(HRFixesMixinPlugin.shouldDisableStructurifyOverlap());
        HRFixesMixinPlugin.resetVersionCacheForTests();
        HRFixesMixinPlugin.setInstalledVersionsForTests(Map.of("structurify", "2.0.43"));
        assertFalse(HRFixesMixinPlugin.shouldDisableStructurifyOverlap());
    }
}
