package hollowrealm.qa;

import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Stream;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.Recipe;

/** Real client API smoke checks; no quest sources, balance, or player save copied. */
public final class NativeClientChecks {
    static Object call(Object object, String name, Object... args) throws Exception {
        Class<?> type = object instanceof Class<?> c ? c : object.getClass();
        for (Method m : type.getMethods()) {
            if (!m.getName().equals(name) || m.getParameterCount() != args.length) continue;
            boolean match = true;
            for (int i=0; i<args.length; i++) if (args[i] != null && !m.getParameterTypes()[i].isInstance(args[i])) match = false;
            if (match) { m.setAccessible(true); return m.invoke(object instanceof Class<?> ? null : object, args); }
        }
        throw new NoSuchMethodException(type.getName() + "." + name);
    }
    static Class<?> type(String name) throws Exception { return Class.forName(name); }
    public static void run() throws Exception {
        var mc = Minecraft.m_91087_();
        var player = mc.f_91074_;
        Class<?> attachmentType = type("cn.leolezury.eternalstarlight.common.platform.EntityDataAttachment");
        Object attachment = type("cn.leolezury.eternalstarlight.common.registry.ESDataAttachments").getField("IMPORTANT_ITEM").get(null);
        Method set = attachmentType.getMethod("setDataUnsynced", Entity.class, Object.class);
        Method get = attachmentType.getMethod("getData", Entity.class);
        Method existing = attachmentType.getMethod("getExistingData", Entity.class);
        var old = (Optional<?>)existing.invoke(attachment, player);
        Class<?> packet = type("cn.leolezury.eternalstarlight.common.network.SyncAttachmentsPacket");
        var ctor = packet.getConstructor(int.class, attachmentType, Object.class);
        var handle = packet.getMethod("handle", Player.class);
        try {
            set.invoke(attachment, player, Boolean.FALSE);
            handle.invoke(ctor.newInstance(player.m_19879_(), attachment, Boolean.TRUE), new Object[]{null});
            NativeProbe.check(Boolean.TRUE.equals(get.invoke(attachment, player)), "attachment null sender updates client data");
            handle.invoke(ctor.newInstance(player.m_19879_(), attachment, Boolean.FALSE), player);
            NativeProbe.check(Boolean.FALSE.equals(get.invoke(attachment, player)), "attachment explicit player preserved");
            try {
                mc.f_91074_ = null;
                handle.invoke(ctor.newInstance(player.m_19879_(), attachment, Boolean.TRUE), new Object[]{null});
            } finally { mc.f_91074_ = player; }
            NativeProbe.check(Boolean.FALSE.equals(get.invoke(attachment, player)), "attachment absent local player safe");
        } finally {
            if (old.isPresent()) set.invoke(attachment,player,old.get());
            else attachmentType.getMethod("removeDataUnsynced",Entity.class).invoke(attachment,player);
        }
        Object screen = call(type("dev.ftb.mods.ftbquests.client.ClientQuestFile"), "openGui");
        call(screen, "closeQuest");
        Object panel = screen.getClass().getField("viewQuestPanel").get(screen);
        call(panel, "addWidgets");
        NativeProbe.check(call(panel,"getViewedQuest") == null, "quest empty panel refresh with Quest Enhance");
        NativeProbe.check(panel.getClass().getField("width").getInt(panel) == 1 && panel.getClass().getField("height").getInt(panel) == 1,
            "quest empty panel native dimensions retained");
        NativeProbe.check(Arrays.stream(panel.getClass().getDeclaredMethods()).anyMatch(m -> m.getName().contains("hrFixes$emptyQuestPanel")), "quest guard merged");
        mc.m_91152_(null);
        Object runtime = call(type("mezz.jei.common.Internal"),"getJeiRuntime");
        NativeProbe.check(runtime != null, "JEI runtime available");
        Object recipeType = type("mezz.jei.api.constants.RecipeTypes").getField("SMITHING").get(null);
        Object manager = call(runtime,"getRecipeManager");
        Object category = call(manager,"getRecipeCategory",recipeType);
        Object lookup = call(manager,"createRecipeLookup",recipeType);
        Object socket = null;
        try (Stream<?> stream = (Stream<?>)call(lookup,"get")) {
            socket = stream.filter(r -> ((Recipe<?>)r).m_6423_().toString().equals("apotheosis:socketing")).findFirst().orElseThrow();
        }
        Object factory = call(call(runtime,"getJeiHelpers"),"getFocusFactory");
        Object focus = call(factory,"createFocusGroup",List.of());
        Optional<?> layout = (Optional<?>)call(manager,"createRecipeLayoutDrawable",category,socket,focus);
        NativeProbe.check(layout.isPresent(), "Apotheosis socketing JEI layout built");
        Object slotsView = call(layout.orElseThrow(),"getRecipeSlotsView");
        var slots = (List<?>)call(slotsView,"getSlotViews");
        int populated = 0;
        for (Object slot : slots) {
            try (Stream<?> items = (Stream<?>)call(slot,"getItemStacks")) { if (items.count() > 0) populated++; }
        }
        NativeProbe.check(populated >= 3, "JEI base gem and output populated");
        NativeProbe.check(Arrays.stream(category.getClass().getDeclaredMethods()).anyMatch(m -> m.getName().contains("hrFixes$linkSocketExamples")), "socketing focus-link injection merged");
        System.out.println("[HR112] CLIENT_REGRESSION_COMPLETE");
    }
}
