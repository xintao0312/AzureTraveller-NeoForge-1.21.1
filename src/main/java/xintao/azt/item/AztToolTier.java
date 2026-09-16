package xintao.azt.item;

import com.google.common.base.Suppliers;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import xintao.azt.registry.AztMaterial;

import java.util.function.Supplier;

/**
 * 把 {@link AztMaterial} 适配成原版的 {@link Tier} 接口，供工具构造器使用。
 * <p>
 * 1.21.1 的 {@code Tier} 是普通接口（不是注册表条目），直接实现即可，无需注册。
 * 结构与原版 {@code Tiers} 枚举保持一致，仅在"修复材料"上不同——
 * 原版修复材料已随版本改为 {@code Supplier<Ingredient>}，见 {@code Tiers} 源码。
 * <p>
 * 可挖掘的方块由 {@code PickaxeItem} 等子类内部写死的 {@code BlockTags.MINEABLE_WITH_*} 决定，
 * 此处只需提供"挖了不掉落"的标签。
 */
public class AztToolTier implements Tier
{
    private final AztMaterial material;
    private final Supplier<Ingredient> repairIngredient;

    public AztToolTier(AztMaterial material)
    {
        this.material = material;
        // memoize 与原版 Tiers 的做法一致：Ingredient 构造有一定开销，避免每次调用都重建
        this.repairIngredient = Suppliers.memoize(() -> Ingredient.of(AztMaterialItem.SHARD.get(material)));
    }

    public AztMaterial getMaterial()
    {
        return this.material;
    }

    @Override
    public int getUses()
    {
        return this.material.getUses();
    }

    @Override
    public float getSpeed()
    {
        return this.material.getSpeed();
    }

    @Override
    public float getAttackDamageBonus()
    {
        return this.material.getAttackDamageBonus();
    }

    @Override
    public TagKey<Block> getIncorrectBlocksForDrops()
    {
        return this.material.getIncorrectBlocksForDrops();
    }

    @Override
    public int getEnchantmentValue()
    {
        return this.material.getEnchantmentValue();
    }

    @Override
    public Ingredient getRepairIngredient()
    {
        return this.repairIngredient.get();
    }

    /** 强制类初始化。由主类构造器调用，见 PLAN.md 10.5。 */
    public static void initialize()
    {
    }

    /** 便于在批量登记中直接取得该物品类型所需的 Tier。 */
    public static Tier of(AztMaterial material)
    {
        return new AztToolTier(material);
    }
}
