package hollowrealm.performance.mixin;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.extensions.vanilla.smithing.ISmithingCategoryExtension;
import mezz.jei.library.plugins.vanilla.anvil.SmithingRecipeCategory;
import net.minecraft.world.item.crafting.SmithingRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Pseudo
@Mixin(value = SmithingRecipeCategory.class, remap = false)
public abstract class SocketingJeiLayoutMixin {
    @Inject(method = "setRecipe(Lmezz/jei/api/gui/builder/IRecipeLayoutBuilder;Lnet/minecraft/world/item/crafting/SmithingRecipe;Lmezz/jei/api/recipe/IFocusGroup;)V",
            at = @At("TAIL"), locals = LocalCapture.CAPTURE_FAILHARD, require = 1, remap = false)
    private void hrFixes$linkSocketExamples(IRecipeLayoutBuilder builder, SmithingRecipe recipe,
            IFocusGroup focuses, CallbackInfo ci, ISmithingCategoryExtension<?> extension,
            IRecipeSlotBuilder template, IRecipeSlotBuilder base, IRecipeSlotBuilder addition, IRecipeSlotBuilder output) {
        if (recipe.getClass().getName().equals("dev.shadowsoffire.apotheosis.adventure.socket.SocketingRecipe")) {
            builder.createFocusLink(base, addition, output);
        }
    }
}
