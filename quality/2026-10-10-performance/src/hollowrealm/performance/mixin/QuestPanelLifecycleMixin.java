package hollowrealm.performance.mixin;

import dev.ftb.mods.ftblibrary.ui.Widget;
import dev.ftb.mods.ftbquests.quest.Quest;
import dev.ftb.mods.ftbquests.client.gui.quests.ViewQuestPanel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Preserve FTB's empty-panel lifecycle before description extensions run. */
@Pseudo
@Mixin(value = ViewQuestPanel.class, priority = 500, remap = false)
public abstract class QuestPanelLifecycleMixin {
    @Shadow private Quest quest;

    @Inject(method = "addWidgets()V", at = @At("HEAD"), cancellable = true, require = 1, remap = false)
    private void hrFixes$emptyQuestPanel(CallbackInfo ci) {
        if (quest == null) {
            // The original method does exactly this and returns for a null quest.
            ((Widget) (Object) this).setPosAndSize(-1, -1, 1, 1);
            ci.cancel();
        }
    }
}
