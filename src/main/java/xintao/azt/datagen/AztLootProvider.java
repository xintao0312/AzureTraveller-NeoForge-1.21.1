package xintao.azt.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import xintao.azt.block.AztOreBlock;
import xintao.azt.registry.AztMaterial;

import java.util.Set;

/**
 * 矿石方块掉落表生成。
 * <p>
 * 用原版的 {@code createOreDrop}，它已内建"精准采集掉方块、否则掉材料"的行为，
 * 无需自己写掉落条件。
 * <p>
 * 注意 {@code requiresCorrectToolForDrops()} 只声明"挖错工具不掉落"，
 * 具体需要什么工具由这里（战利品表）决定——所以新加矿石时两处都要顾及。
 */
public class AztLootProvider extends BlockLootSubProvider
{
    protected AztLootProvider(HolderLookup.Provider registries)
    {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), registries);
    }

    @Override
    protected void generate()
    {
        for (AztMaterial material : AztMaterial.values())
        {
            if (material.getOre() == null)
            {
                continue;
            }
            Block ore = AztOreBlock.get(material).get();
            // 掉落物就是该材料的碎片
            this.add(ore, this.createOreDrop(ore, xintao.azt.item.AztMaterialItem.SHARD.get(material).get()));
        }
    }

    @Override
    protected Iterable<Block> getKnownBlocks()
    {
        // 只接管本模组的矿石方块，原版方块不在此处理
        return AztOreBlock.ORE.values().stream().map(holder -> (Block) holder.get())::iterator;
    }
}
