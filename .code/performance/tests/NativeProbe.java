package hollowrealm.qa;

import hollowrealm.performance.WorldgenRandom;
import java.nio.file.*;
import java.lang.reflect.Method;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;

/** Test-only mod; never included in the HR Fixes release JAR. */
@Mod("hr_native_qa")
public final class NativeProbe {
    static volatile boolean finished;
    static void check(boolean result, String label) {
        if (!result) throw new AssertionError(label);
        System.out.println("[HR112] PASS " + label);
    }
    public NativeProbe() {
        MinecraftForge.EVENT_BUS.addListener(this::started);
        if (FMLEnvironment.dist == Dist.CLIENT) new Client();
    }
    static LivingEntity create(String id, String name, ServerLevel level) throws Exception {
        var key = new ResourceLocation(id);
        var type = BuiltInRegistries.f_256780_.m_7745_(key);
        check(key.equals(BuiltInRegistries.f_256780_.m_7981_(type)), "registered " + id);
        return (LivingEntity)Class.forName(name).getConstructor(EntityType.class, Level.class).newInstance(type, level);
    }
    static MerchantOffers offers(LivingEntity entity) throws Exception {
        var field = entity.getClass().getDeclaredField("offers"); field.setAccessible(true);
        return (MerchantOffers)field.get(entity);
    }
    static void merged(LivingEntity entity, String name) {
        check(java.util.Arrays.stream(entity.getClass().getDeclaredMethods()).anyMatch(m -> m.getName().contains(name)), "merged Redirect " + name);
    }
    private void started(ServerStartedEvent event) {
        System.out.println("[HR112] SERVER_STARTED " + FMLEnvironment.dist);
        event.getServer().execute(() -> {
            try {
                if (java.util.List.of("goety", "awaken", "all").contains(System.getProperty("hrqa.case", "none"))) {
                    runGoety(event.getServer().m_129783_(), () -> complete(event));
                } else {
                    if (System.getProperty("hrqa.case").equals("coo")) runCoo();
                    if (java.util.List.of("drift-missing", "drift-duplicate", "unknown").contains(System.getProperty("hrqa.case"))) {
                        var fixture = Class.forName("com.k1sak1.goetyawaken.common.entities.ally.illager.SorcererServant");
                        check(java.util.Arrays.stream(fixture.getDeclaredMethods()).noneMatch(m -> m.getName().contains("hollowrealm$")), "self-authored drift/unknown fixture safely skipped");
                    }
                    complete(event);
                }
            } catch (Throwable failure) { failed(event, failure); }
        });
    }
    static void complete(ServerStartedEvent event) {
        System.out.println("[HR112] COMPLETE " + System.getProperty("hrqa.phase", "write"));
        finished = true;
        if (FMLEnvironment.dist == Dist.DEDICATED_SERVER) event.getServer().execute(() -> event.getServer().m_7570_(false));
    }
    static void failed(ServerStartedEvent event, Throwable failure) {
        System.out.println("[HR112] FAIL"); failure.printStackTrace();
        finished = true;
        if (FMLEnvironment.dist == Dist.DEDICATED_SERVER) event.getServer().execute(() -> event.getServer().m_7570_(false));
    }
    static void runCoo() throws Exception {
        String target = "com.faboslav.structurify.common.world.level.structure.checks.StructureOverlapCheck";
        Class.forName(target);
        var plugin = Class.forName("com.misanthropy.collections_of_optimizations.core.CoOMixinPlugin").getConstructor().newInstance();
        var apply = plugin.getClass().getMethod("shouldApplyMixin", String.class, String.class);
        check(Boolean.FALSE.equals(apply.invoke(plugin, target, "com.misanthropy.collections_of_optimizations.mixin.structurify.MixinStructureOverlapCheck")), "CoO overlap disabled");
        check(Boolean.TRUE.equals(apply.invoke(plugin, "example.Target", "example.UnrelatedMixin")), "CoO unrelated gate retained");
    }
    static void runGoety(ServerLevel level, Runnable done) throws Exception {
        String sorc = "com.k1sak1.goetyawaken.common.entities.ally.illager.SorcererServant";
        String zeal = "com.qiuyue.goetyominous.common.entities.hostile.cultists.Zealot";
        var sorcerer = create("goetyawaken:sorcerer_servant", sorc, level);
        merged(sorcerer, "hollowrealm$tradeRandom");
        check(WorldgenRandom.forEntity(sorcerer, level) == level.f_46441_, "main thread original random");
        Method set = sorcerer.getClass().getMethod("setSorcererLevel", int.class, boolean.class);
        set.invoke(sorcerer, 3, false);
        check(!offers(sorcerer).isEmpty(), "main thread trade generation");
        CompoundTag saved = new CompoundTag(); sorcerer.m_20240_(saved);
        CompoundTag trades = offers(sorcerer).m_45388_().m_6426_();
        var file = Path.of("hrqa-sorcerer.nbt").toFile();
        if (System.getProperty("hrqa.phase", "write").equals("read")) {
            check(file.isFile(), "previous process NBT exists");
            var old = NbtIo.m_128937_(file);
            var restored = create("goetyawaken:sorcerer_servant", sorc, level);
            restored.m_20258_(old);
            var oldTrades = old.m_128469_("Offers");
            check(oldTrades.equals(offers(restored).m_45388_()), "restart trade NBT restored semantically");
        } else {
            NbtIo.m_128944_(saved, file);
            check(file.isFile(), "native entity NBT saved");
        }
        DifficultyInstance difficulty = level.m_6436_(new BlockPos(0,-60,0));
        final Method equip;
        if (!System.getProperty("hrqa.case").equals("awaken")) {
            var zealot = create("goetyominous:zealot", zeal, level);
            merged(zealot, "hollowrealm$equipmentRandom");
            equip = zealot.getClass().getDeclaredMethod("populateDefaultEquipmentSlots", DifficultyInstance.class);
            equip.setAccessible(true);
            equip.invoke(zealot, difficulty);
            check(!zealot.m_6844_(EquipmentSlot.MAINHAND).m_41619_(), "main thread equipment");
        } else { equip = null; }
        new Thread(() -> {
            try {
                var workerSorc = create("goetyawaken:sorcerer_servant", sorc, level);
                check(WorldgenRandom.forEntity(workerSorc, level) == workerSorc.m_217043_(), "worker entity random");
                workerSorc.m_20258_(saved.m_6426_());
                check(trades.equals(offers(workerSorc).m_45388_()), "worker trade NBT retained");
                var fresh = create("goetyawaken:sorcerer_servant", sorc, level);
                set.invoke(fresh, 3, false);
                check(!offers(fresh).isEmpty(), "worker fresh trades");
                if (equip != null) {
                    var workerZeal = create("goetyominous:zealot", zeal, level);
                    equip.invoke(workerZeal, difficulty);
                    check(!workerZeal.m_6844_(EquipmentSlot.MAINHAND).m_41619_(), "worker equipment");
                }
                level.m_7654_().execute(done);
            } catch (Throwable failure) {
                System.out.println("[HR112] FAIL WORKER"); failure.printStackTrace();
                // Terminate through the normal save path even when the test failed.
                level.m_7654_().execute(done);
            }
        }, "HR112-worldgen-test").start();
    }
    static final class Client {
        boolean opened;
        String lastScreen = "";
        int readyTicks;
        boolean aborted;
        Client() { MinecraftForge.EVENT_BUS.addListener(this::tick); }
        void tick(TickEvent.ClientTickEvent e) {
            if (e.phase != TickEvent.Phase.END) return;
            if (aborted) return;
            var mc = net.minecraft.client.Minecraft.m_91087_();
            if (mc.f_91080_ != null && !mc.f_91080_.getClass().getName().equals(lastScreen)) {
                lastScreen = mc.f_91080_.getClass().getName();
                System.out.println("[HR112] SCREEN " + lastScreen);
            }
            if (mc.f_91080_ instanceof net.minecraft.client.gui.screens.ConfirmScreen confirm) {
                try {
                    String title = confirm.m_96636_().toString();
                    System.out.println("[HR112] CONFIRM_TITLE " + title);
                    check(title.contains("selectWorld.warning.experimental.title") || title.contains("selectWorld.backupQuestion.experimental"), "only fresh fixture experimental-world prompt accepted");
                    var field = net.minecraft.client.gui.screens.ConfirmScreen.class.getDeclaredField("f_95649_");
                    field.setAccessible(true);
                    Object callback = field.get(confirm);
                    Class.forName("it.unimi.dsi.fastutil.booleans.BooleanConsumer").getMethod("accept", boolean.class).invoke(callback, true);
                } catch (Throwable failure) {
                    aborted = true;
                    System.out.println("[HR112] FAIL unexpected confirmation"); failure.printStackTrace(); mc.m_91395_();
                }
                return;
            }
            if (!opened && lastScreen.equals("net.minecraftforge.client.gui.LoadingErrorScreen")) {
                opened = true;
                try {
                    var field = mc.f_91080_.getClass().getDeclaredField("modLoadErrors");
                    field.setAccessible(true);
                    check(((java.util.List<?>)field.get(mc.f_91080_)).isEmpty(), "Forge loading screen contains warnings only");
                    mc.m_231466_().m_233133_(mc.f_91080_, "hr112_qa");
                } catch (Throwable failure) {
                    System.out.println("[HR112] FAIL loading screen"); failure.printStackTrace(); mc.m_91395_();
                }
            }
            if (!opened && mc.f_91080_ instanceof net.minecraft.client.gui.screens.TitleScreen) {
                opened = true;
                mc.m_231466_().m_233133_(mc.f_91080_, "hr112_qa");
            }
            if (finished && mc.f_91074_ != null) {
                if (System.getProperty("hrqa.case").equals("all")) {
                    if (++readyTicks != 100) return;
                    try { NativeClientChecks.run(); }
                    catch (Throwable failure) { System.out.println("[HR112] FAIL client regression"); failure.printStackTrace(); }
                }
                mc.m_91395_();
            }
        }
    }
}
