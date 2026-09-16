package xintao.azt.item;

import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import xintao.azt.block.AztBlock;
import xintao.azt.registry.AztRegistry;

public class AztItem
{
    public static final DeferredItem<Item> EXAMPLE_ITEM = AztRegistry.ITEMS
            .registerSimpleItem("example_item",
                    new Item.Properties().food(new FoodProperties.Builder()
                            .alwaysEdible().nutrition(1).saturationModifier(2f).build()));
    public static final DeferredItem<BlockItem> EXAMPLE_BLOCK_ITEM = AztRegistry.ITEMS
            .registerSimpleBlockItem("example_block", AztBlock.EXAMPLE_BLOCK);

    public static void initialize() {}
}
