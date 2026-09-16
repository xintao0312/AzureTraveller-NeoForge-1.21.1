package xintao.azt.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import xintao.azt.registry.AztRegistry;

public class AztBlock
{
    public static final DeferredBlock<Block> EXAMPLE_BLOCK = AztRegistry.BLOCKS
            .registerSimpleBlock("example_block",
                    BlockBehaviour.Properties.of().mapColor(MapColor.STONE));

    public static void initialize() {}
}
