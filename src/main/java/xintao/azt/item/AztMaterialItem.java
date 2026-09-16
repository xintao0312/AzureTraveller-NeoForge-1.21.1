package xintao.azt.item;

import net.minecraft.world.item.*;
import net.neoforged.neoforge.registries.DeferredItem;
import xintao.azt.block.AztOreBlock;
import xintao.azt.registry.AztMaterial;
import xintao.azt.registry.AztRegistry;

import java.util.EnumMap;
import java.util.Map;

/**
 * 以 {@link AztMaterial} 规格表驱动的批量登记。
 * <p>
 * 手写 6 种材料 × 9 个形态需要 54 份几乎相同的代码；这里改为遍历材料表，
 * 依据每种材料声明的 {@link AztMaterial.Form} 形态集决定登记哪些条目。
 * <b>新增材料只需改 {@code AztMaterial} 一行，本类无需改动。</b>
 * <p>
 * 工具与武器的数值均取自材料本身，因此这里看不到任何硬编码数字。
 */
public final class AztMaterialItem
{
    /** 各材料的碎片。同时也是工具与盔甲的修复材料。 */
    public static final Map<AztMaterial, DeferredItem<Item>> SHARD = new EnumMap<>(AztMaterial.class);
    /** 各材料的剑。 */
    public static final Map<AztMaterial, DeferredItem<SwordItem>> SWORD = new EnumMap<>(AztMaterial.class);
    public static final Map<AztMaterial, DeferredItem<PickaxeItem>> PICKAXE = new EnumMap<>(AztMaterial.class);
    public static final Map<AztMaterial, DeferredItem<AxeItem>> AXE = new EnumMap<>(AztMaterial.class);
    public static final Map<AztMaterial, DeferredItem<ShovelItem>> SHOVEL = new EnumMap<>(AztMaterial.class);
    public static final Map<AztMaterial, DeferredItem<HoeItem>> HOE = new EnumMap<>(AztMaterial.class);
    /** 各材料的盔甲，键为部位。 */
    public static final Map<AztMaterial, Map<ArmorItem.Type, DeferredItem<ArmorItem>>> ARMOR =
            new EnumMap<>(AztMaterial.class);
    /** 各材料矿石方块的方块物品。 */
    public static final Map<AztMaterial, DeferredItem<BlockItem>> ORE_ITEM = new EnumMap<>(AztMaterial.class);

    // 工具的攻击数值。与原版一致：剑为 3/-2.4，其余挖具为 1/-2.8 一类。
    private static final int SWORD_ATTACK_DAMAGE = 3;
    private static final float SWORD_ATTACK_SPEED = -2.4F;
    private static final float DIGGER_ATTACK_DAMAGE = 1.0F;
    private static final float DIGGER_ATTACK_SPEED = -2.8F;
    // 盔甲耐久系数：实际耐久 = 部位基础值 × 此系数
    private static final int ARMOR_DURABILITY_FACTOR = 15;

    private AztMaterialItem()
    {
    }

    /** 按材料表批量登记。必须在 mod 总线触发 RegisterEvent 之前调用。 */
    public static void register()
    {
        for (AztMaterial material : AztMaterial.values())
        {
            // 矿石方块的方块物品。矿石方块本身由 AztOreBlock 登记，此处补上可拾取/可放置的物品形态
            if (material.getOre() != null)
            {
                ORE_ITEM.put(material, AztRegistry.ITEMS.registerSimpleBlockItem(
                        material.getId() + "_ore", AztOreBlock.get(material)));
            }
            if (material.has(AztMaterial.Form.SHARD))
            {
                SHARD.put(material, AztRegistry.ITEMS.registerSimpleItem(material.getId() + "_shard",
                        new Item.Properties()));
            }
            if (material.has(AztMaterial.Form.SWORD))
            {
                SWORD.put(material, registerSword(material));
            }
            if (material.has(AztMaterial.Form.TOOL))
            {
                PICKAXE.put(material, registerPickaxe(material));
                AXE.put(material, registerAxe(material));
                SHOVEL.put(material, registerShovel(material));
                HOE.put(material, registerHoe(material));
            }
            if (material.has(AztMaterial.Form.ARMOR))
            {
                ARMOR.put(material, registerArmor(material));
            }
        }
    }

    private static DeferredItem<SwordItem> registerSword(AztMaterial material)
    {
        Tier tier = AztToolTier.of(material);
        return AztRegistry.ITEMS.registerItem(material.getId() + "_sword",
                properties -> new SwordItem(tier,
                        properties.attributes(SwordItem.createAttributes(tier, SWORD_ATTACK_DAMAGE, SWORD_ATTACK_SPEED))),
                new Item.Properties());
    }

    private static DeferredItem<PickaxeItem> registerPickaxe(AztMaterial material)
    {
        Tier tier = AztToolTier.of(material);
        return AztRegistry.ITEMS.registerItem(material.getId() + "_pickaxe",
                properties -> new PickaxeItem(tier,
                        properties.attributes(DiggerItem.createAttributes(tier, DIGGER_ATTACK_DAMAGE, DIGGER_ATTACK_SPEED))),
                new Item.Properties());
    }

    private static DeferredItem<AxeItem> registerAxe(AztMaterial material)
    {
        Tier tier = AztToolTier.of(material);
        return AztRegistry.ITEMS.registerItem(material.getId() + "_axe",
                properties -> new AxeItem(tier,
                        properties.attributes(DiggerItem.createAttributes(tier, DIGGER_ATTACK_DAMAGE, DIGGER_ATTACK_SPEED))),
                new Item.Properties());
    }

    private static DeferredItem<ShovelItem> registerShovel(AztMaterial material)
    {
        Tier tier = AztToolTier.of(material);
        return AztRegistry.ITEMS.registerItem(material.getId() + "_shovel",
                properties -> new ShovelItem(tier,
                        properties.attributes(DiggerItem.createAttributes(tier, DIGGER_ATTACK_DAMAGE, DIGGER_ATTACK_SPEED))),
                new Item.Properties());
    }

    private static DeferredItem<HoeItem> registerHoe(AztMaterial material)
    {
        Tier tier = AztToolTier.of(material);
        return AztRegistry.ITEMS.registerItem(material.getId() + "_hoe",
                properties -> new HoeItem(tier,
                        properties.attributes(DiggerItem.createAttributes(tier, DIGGER_ATTACK_DAMAGE, DIGGER_ATTACK_SPEED))),
                new Item.Properties());
    }

    private static Map<ArmorItem.Type, DeferredItem<ArmorItem>> registerArmor(AztMaterial material)
    {
        Map<ArmorItem.Type, DeferredItem<ArmorItem>> armor = new EnumMap<>(ArmorItem.Type.class);
        for (ArmorItem.Type type : ArmorItem.Type.values())
        {
            armor.put(type, AztRegistry.ITEMS.registerItem(material.getId() + "_" + type.getName(),
                    properties -> new ArmorItem(AztArmorMaterial.get(material), type,
                            properties.durability(type.getDurability(ARMOR_DURABILITY_FACTOR))),
                    new Item.Properties()));
        }
        return armor;
    }

    /** 强制类初始化。由主类构造器调用，见 PLAN.md 10.5。 */
    public static void initialize()
    {
    }
}
