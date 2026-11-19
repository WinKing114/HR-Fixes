package hollowrealm.performance;

import dev.shadowsoffire.apotheosis.adventure.loot.LootRarity;
import dev.shadowsoffire.apotheosis.adventure.loot.RarityRegistry;
import dev.shadowsoffire.apotheosis.adventure.socket.SocketHelper;
import dev.shadowsoffire.apotheosis.adventure.socket.SocketingRecipe;
import dev.shadowsoffire.apotheosis.adventure.socket.gem.Gem;
import dev.shadowsoffire.apotheosis.adventure.socket.gem.GemRegistry;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.RandomAccess;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IIngredientAcceptor;
import mezz.jei.api.gui.ingredient.IRecipeSlotDrawable;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.extensions.vanilla.smithing.ISmithingCategoryExtension;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;

public final class SocketingJeiExtension implements ISmithingCategoryExtension<SocketingRecipe> {
    private static final List<String> BASE_ITEM_IDS = List.of(
            "diamond_helmet", "diamond_chestplate", "diamond_leggings", "diamond_boots",
            "diamond_sword", "diamond_pickaxe", "diamond_axe", "diamond_shovel",
            "bow", "crossbow", "trident", "shield");

    private final SnapshotCache<SocketingRecipe, ClientLevel, Gem, LootRarity, ItemStack> cache = new SnapshotCache<>();

    private static List<ItemStack> createBaseStacks() {
        List<ItemStack> bases = new ArrayList<>(BASE_ITEM_IDS.size());
        for (String itemId : BASE_ITEM_IDS) {
            ItemStack stack = new ItemStack(BuiltInRegistries.f_257033_.m_7745_(new ResourceLocation("minecraft", itemId)));
            SocketHelper.setSockets(stack, 1);
            bases.add(stack);
        }
        return bases;
    }

    private static SnapshotCache.Snapshot<ItemStack> computeSnapshot(
            SocketingRecipe recipe,
            ClientLevel level,
            List<Gem> gems,
            List<LootRarity> rarities) {
        List<ItemStack> baseTemplates = createBaseStacks();
        List<ItemStack> bases = new ArrayList<>();
        List<ItemStack> gemStacks = new ArrayList<>();
        List<ItemStack> outputs = new ArrayList<>();
        for (Gem gem : gems) {
            for (LootRarity rarity : rarities) {
                if (gem.clamp(rarity) != rarity) {
                    continue;
                }
                ItemStack gemStack = GemRegistry.createGemStack(gem, rarity);
                for (ItemStack baseTemplate : baseTemplates) {
                    ItemStack base = baseTemplate.m_41777_();
                    ItemStack output = result(recipe, level, base, gemStack);
                    if (!output.m_41619_()) {
                        bases.add(base);
                        gemStacks.add(gemStack);
                        outputs.add(output);
                        break;
                    }
                }
            }
        }
        return new SnapshotCache.Snapshot<>(List.copyOf(bases), List.copyOf(gemStacks), List.copyOf(outputs));
    }

    private SnapshotCache.Snapshot<ItemStack> snapshot(SocketingRecipe recipe) {
        ClientLevel level = Minecraft.m_91087_().f_91073_;
        if (level == null) {
            return this.cache.getOrCompute(recipe, null, List.of(), List.of(), SocketingJeiExtension::computeSnapshot);
        }
        return this.cache.getOrCompute(
                recipe,
                level,
                GemRegistry.INSTANCE.getValues(),
                RarityRegistry.INSTANCE.getValues(),
                SocketingJeiExtension::computeSnapshot);
    }

    private static ItemStack result(SocketingRecipe recipe, ClientLevel level, ItemStack base, ItemStack gem) {
        if (level == null || base.m_41619_() || gem.m_41619_()) {
            return ItemStack.f_41583_;
        }
        SimpleContainer container = new SimpleContainer(ItemStack.f_41583_, base, gem);
        return recipe.m_5818_(container, level) ? recipe.m_5874_(container, level.m_9598_()) : ItemStack.f_41583_;
    }

    @Override
    public <T extends IIngredientAcceptor<T>> void setTemplate(SocketingRecipe recipe, T ingredientAcceptor) {
    }

    @Override
    public <T extends IIngredientAcceptor<T>> void setBase(SocketingRecipe recipe, T ingredientAcceptor) {
        ingredientAcceptor.addItemStacks(snapshot(recipe).bases());
    }

    @Override
    public <T extends IIngredientAcceptor<T>> void setAddition(SocketingRecipe recipe, T ingredientAcceptor) {
        ingredientAcceptor.addItemStacks(snapshot(recipe).gems());
    }

    @Override
    public <T extends IIngredientAcceptor<T>> void setOutput(SocketingRecipe recipe, T ingredientAcceptor) {
        ingredientAcceptor.addItemStacks(snapshot(recipe).outputs());
    }

    @Override
    public void onDisplayedIngredientsUpdate(
            SocketingRecipe recipe,
            IRecipeSlotDrawable templateSlot,
            IRecipeSlotDrawable baseSlot,
            IRecipeSlotDrawable additionSlot,
            IRecipeSlotDrawable outputSlot,
            IFocusGroup focuses) {
        ItemStack base = baseSlot.getDisplayedIngredient(VanillaTypes.ITEM_STACK).orElse(ItemStack.f_41583_);
        ItemStack addition = additionSlot.getDisplayedIngredient(VanillaTypes.ITEM_STACK).orElse(ItemStack.f_41583_);
        ItemStack output = result(recipe, Minecraft.m_91087_().f_91073_, base, addition);
        outputSlot.createDisplayOverrides().addItemStacks(output.m_41619_() ? List.of() : List.of(output));
    }

    static final class SnapshotCache<R, L, G, T, S> {
        @FunctionalInterface
        interface ExampleComputer<R, L, G, T, S> {
            Snapshot<S> compute(R recipe, L level, List<G> gems, List<T> rarities);
        }

        record Snapshot<S>(List<S> bases, List<S> gems, List<S> outputs) {
            private static final Snapshot<?> EMPTY = new Snapshot<>(List.of(), List.of(), List.of());

            @SuppressWarnings("unchecked")
            static <S> Snapshot<S> empty() {
                return (Snapshot<S>) EMPTY;
            }
        }

        private record CachedState<R, L, G, T, S>(
                R recipe,
                WeakReference<L> level,
                List<G> gems,
                List<T> rarities,
                Snapshot<S> snapshot) {
            boolean matches(R targetRecipe, L targetLevel, Collection<? extends G> currentGems, Collection<? extends T> currentRarities) {
                return this.recipe == targetRecipe
                        && this.level.get() == targetLevel
                        && sameElements(this.gems, currentGems)
                        && sameElements(this.rarities, currentRarities);
            }
        }

        private volatile CachedState<R, L, G, T, S> cached;

        Snapshot<S> getOrCompute(
                R recipe,
                L level,
                Collection<? extends G> currentGems,
                Collection<? extends T> currentRarities,
                ExampleComputer<R, L, G, T, S> computer) {
            if (level == null || recipe == null) {
                if (this.cached != null) {
                    this.cached = null;
                }
                return Snapshot.empty();
            }
            CachedState<R, L, G, T, S> local = this.cached;
            if (local != null && local.matches(recipe, level, currentGems, currentRarities)) {
                return local.snapshot();
            }
            synchronized (this) {
                local = this.cached;
                if (local != null && local.matches(recipe, level, currentGems, currentRarities)) {
                    return local.snapshot();
                }
                List<G> gemsSnapshot = List.copyOf(currentGems);
                List<T> raritiesSnapshot = List.copyOf(currentRarities);
                Snapshot<S> computed = computer.compute(recipe, level, gemsSnapshot, raritiesSnapshot);
                this.cached = new CachedState<>(recipe, new WeakReference<>(level), gemsSnapshot, raritiesSnapshot, computed);
                return computed;
            }
        }

        static <E> boolean sameElements(List<E> expected, Collection<? extends E> actual) {
            if (expected.size() != actual.size()) {
                return false;
            }
            if (actual instanceof List<?> actualList && actualList instanceof RandomAccess) {
                for (int i = 0, len = expected.size(); i < len; i++) {
                    if (!Objects.equals(expected.get(i), actualList.get(i))) {
                        return false;
                    }
                }
                return true;
            }
            int index = 0;
            for (E item : actual) {
                if (!Objects.equals(expected.get(index++), item)) {
                    return false;
                }
            }
            return true;
        }
    }
}
