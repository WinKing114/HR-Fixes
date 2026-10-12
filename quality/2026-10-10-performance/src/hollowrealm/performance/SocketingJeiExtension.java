package hollowrealm.performance;

import java.util.ArrayList;
import java.util.List;
import java.lang.ref.WeakReference;
import dev.shadowsoffire.apotheosis.adventure.socket.SocketHelper;
import dev.shadowsoffire.apotheosis.adventure.socket.SocketingRecipe;
import dev.shadowsoffire.apotheosis.adventure.socket.gem.GemRegistry;
import dev.shadowsoffire.apotheosis.adventure.socket.gem.Gem;
import dev.shadowsoffire.apotheosis.adventure.loot.LootRarity;
import dev.shadowsoffire.apotheosis.adventure.loot.RarityRegistry;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IIngredientAcceptor;
import mezz.jei.api.gui.ingredient.IRecipeSlotDrawable;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.extensions.vanilla.smithing.ISmithingCategoryExtension;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;

/** JEI examples only. Actual socketing still uses Apotheosis' untouched recipe. */
public final class SocketingJeiExtension implements ISmithingCategoryExtension<SocketingRecipe> {
    private record Example(ItemStack base, ItemStack gem, ItemStack output) {}
    private List<Gem> previousGems = List.of();
    private List<LootRarity> previousRarities = List.of();
    private WeakReference<Object> previousLevel = new WeakReference<>(null);
    private List<Example> examples = List.of();
    private static List<ItemStack> bases() {
        var result = new ArrayList<ItemStack>();
        for (String name : List.of("diamond_helmet", "diamond_chestplate", "diamond_leggings",
                "diamond_boots", "diamond_sword", "diamond_pickaxe", "diamond_axe",
                "diamond_shovel", "bow", "crossbow", "trident", "shield")) {
            var stack = new ItemStack(BuiltInRegistries.f_257033_.m_7745_(new ResourceLocation("minecraft", name)));
            SocketHelper.setSockets(stack, 1);
            result.add(stack);
        }
        return result;
    }

    private List<Example> examples(SocketingRecipe recipe) {
        var level = Minecraft.m_91087_().f_91073_;
        var gems = List.copyOf(GemRegistry.INSTANCE.getValues());
        var rarities = List.copyOf(RarityRegistry.INSTANCE.getValues());
        if (previousLevel.get() != level || !previousGems.equals(gems) || !previousRarities.equals(rarities)) {
            var next = new ArrayList<Example>();
            for (var gem : gems) for (var rarity : rarities) {
                if (gem.clamp(rarity) != rarity) continue;
                var stack = GemRegistry.createGemStack(gem, rarity);
                for (var base : bases()) {
                    var output = result(recipe, base, stack);
                    if (!output.m_41619_()) { next.add(new Example(base, stack, output)); break; }
                }
            }
            examples = List.copyOf(next);
            previousGems = gems;
            previousRarities = rarities;
            previousLevel = new WeakReference<>(level);
        }
        return examples;
    }

    private static ItemStack result(SocketingRecipe recipe, ItemStack base, ItemStack gem) {
        var level = Minecraft.m_91087_().f_91073_;
        if (level == null || base.m_41619_() || gem.m_41619_()) return ItemStack.f_41583_;
        var inputs = new SimpleContainer(ItemStack.f_41583_, base, gem);
        return recipe.m_5818_(inputs, level) ? recipe.m_5874_(inputs, level.m_9598_()) : ItemStack.f_41583_;
    }

    public <T extends IIngredientAcceptor<T>> void setTemplate(SocketingRecipe recipe, T slot) {}
    public <T extends IIngredientAcceptor<T>> void setBase(SocketingRecipe recipe, T slot) { slot.addItemStacks(examples(recipe).stream().map(Example::base).toList()); }
    public <T extends IIngredientAcceptor<T>> void setAddition(SocketingRecipe recipe, T slot) { slot.addItemStacks(examples(recipe).stream().map(Example::gem).toList()); }
    public <T extends IIngredientAcceptor<T>> void setOutput(SocketingRecipe recipe, T slot) {
        slot.addItemStacks(examples(recipe).stream().map(Example::output).toList());
    }

    public void onDisplayedIngredientsUpdate(SocketingRecipe recipe, IRecipeSlotDrawable template,
            IRecipeSlotDrawable base, IRecipeSlotDrawable addition, IRecipeSlotDrawable output, IFocusGroup focuses) {
        var result = result(recipe, base.getDisplayedIngredient(VanillaTypes.ITEM_STACK).orElse(ItemStack.f_41583_),
                addition.getDisplayedIngredient(VanillaTypes.ITEM_STACK).orElse(ItemStack.f_41583_));
        output.createDisplayOverrides().addItemStacks(result.m_41619_() ? List.of() : List.of(result));
    }
}
