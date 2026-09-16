package xintao.azt.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import xintao.azt.registry.AztMaterial;
import xintao.azt.registry.AztRegistry;

import java.util.EnumMap;
import java.util.Map;

/**
 * 各材料的矿石方块。由 {@link AztMaterial} 的 {@link AztMaterial.Ore} 参数驱动批量登记，
 * 新增材料同样只需改材料表一行。
 * <p>
 * <b>掉落规则不在这里。</b> 1.21 把"需要什么工具才掉落"交给战利品表：
 * <ul>
 *     <li>{@code requiresCorrectToolForDrops()}（本类设置）：声明"挖错工具不掉落"</li>
 *     <li>战利品表（datagen 生成）：声明具体掉落物与所需工具标签</li>
 * </ul>
 * 因此本类只负责物理属性，掉落物由 {@code AztLootProvider} 负责。
 */
public class AztOreBlock
{
    /** 各材料的矿石方块。 */
    public static final Map<AztMaterial, DeferredBlock<Block>> ORE = new EnumMap<>(AztMaterial.class);

    static
    {
        for (AztMaterial material : AztMaterial.values())
        {
            AztMaterial.Ore ore = material.getOre();
            if (ore == null)
            {
                continue;
            }
            ORE.put(material, AztRegistry.BLOCKS.register(material.getId() + "_ore",
                    () -> new Block(createProperties(material, ore))));
        }
    }

    private static BlockBehaviour.Properties createProperties(AztMaterial material, AztMaterial.Ore ore)
    {
        return BlockBehaviour.Properties.of()
                .mapColor(material.getColor())
                .sound(SoundType.STONE)
                .strength(ore.destroyTime(), ore.explosionResistance())
                .requiresCorrectToolForDrops();
    }

    /** 取指定材料的矿石方块。仅对声明了 {@link AztMaterial.Ore} 的材料有效。 */
    public static DeferredBlock<Block> get(AztMaterial material)
    {
        DeferredBlock<Block> block = ORE.get(material);
        if (block == null)
        {
            throw new IllegalArgumentException("材料 " + material.getId() + " 未声明矿石参数，没有对应的矿石方块");
        }
        return block;
    }

    /** 强制类初始化。由主类构造器调用，见 PLAN.md 10.5。 */
    public static void initialize()
    {
    }
}
