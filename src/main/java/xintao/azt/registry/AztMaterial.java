package xintao.azt.registry;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.MapColor;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

/**
 * 材料规格表（纯数据，不含登记逻辑）。
 * <p>
 * 每种材料的数值、颜色、可用形态都在这里声明；批量登记由 {@code AztMaterialItem} 以此表驱动。
 * <b>新增一种材料只需在此枚举追加一行</b>，登记代码无需改动。
 * <p>
 * 设计依据见 PLAN.md 第 9 节：材料之间是"功能分叉"而非"线性更强"，
 * 因此每种材料用 {@link Form} 声明自己有哪些形态——晴空晶不做装备，即是此机制的直接体现。
 */
public enum AztMaterial
{
    //                    id             中文       英文          颜色                数量  速度  攻击  挖掘标签                          附魔  盔甲防御(头/胸/腿/靴)  韧性  击退抗性  形态
    AZURE_SHARD("azure_shard", "蔚蓝晶簇", "Azure Shard", MapColor.COLOR_LIGHT_BLUE, 190, 5.5F, 1.5F, BlockTags.INCORRECT_FOR_STONE_TOOL, 12,
            Map.of(ArmorItem.Type.HELMET, 2, ArmorItem.Type.CHESTPLATE, 5, ArmorItem.Type.LEGGINGS, 4, ArmorItem.Type.BOOTS, 2),
            0.0F, 0.0F, Form.ALL,
            // 对标铁：中上层密集，矿脉小
            new Ore(-24, 56, 6, 10, false, 3.0F, 3.0F)),

    // 晴空晶只服务"新机制"（待 Q4 定义），不参与装备，因此只有碎片形态
    SKYCRYSTAL("skycrystal", "晴空晶", "Skycrystal", MapColor.COLOR_CYAN, 300, 6.0F, 2.0F, BlockTags.INCORRECT_FOR_IRON_TOOL, 14,
            Map.of(), 0.0F, 0.0F, Form.of(Form.SHARD),
            // 设计为洞穴产出：airExposed=true 使其倾向洞穴内壁，比蔚蓝晶簇稀有
            new Ore(-16, 48, 4, 5, true, 3.0F, 3.0F)),

    VOIDIRON("voidiron", "虚空铁", "Voidiron", MapColor.COLOR_BLACK, 1100, 8.0F, 3.0F, BlockTags.INCORRECT_FOR_DIAMOND_TOOL, 11,
            Map.of(ArmorItem.Type.HELMET, 3, ArmorItem.Type.CHESTPLATE, 7, ArmorItem.Type.LEGGINGS, 6, ArmorItem.Type.BOOTS, 3),
            1.5F, 0.0F, Form.ALL,
            // 对标钻石：深层、稀有、矿脉大、更硬
            new Ore(-64, 16, 8, 4, false, 4.5F, 3.0F));

    /**
     * 矿石在世界里的生成参数与方块物理属性。{@code null} 表示该材料不生成矿石
     * （例如将来靠生物掉落获得的材料）。
     * <p>
     * 生成数值参照原版：蔚蓝晶簇对标铁（Y -24~56，密集），虚空铁对标钻石（Y -64~16，稀有）。
     * 晴空晶按设计只在洞穴出现，故用 airExposed=true（暴露于空气降低生成率，因而偏洞穴）。
     *
     * @param minY                生成最低高度（含）
     * @param maxY                生成最高高度（含）
     * @param veinSize            单条矿脉的最大方块数
     * @param veinsPerChunk       每区块尝试生成次数
     * @param airExposed          暴露于空气时是否降低生成率。true 会使其偏向洞穴内壁
     * @param destroyTime         挖掘耗时基数。原版石头 1.5、铁矿石 3.0、深层钻石矿 4.5
     * @param explosionResistance 爆炸抗性。原版石头 6.0、钻石矿 3.0、黑曜石 1200
     */
    public record Ore(int minY, int maxY, int veinSize, int veinsPerChunk, boolean airExposed,
                      float destroyTime, float explosionResistance)
    {
    }

    /** 用于修复工具的"挖了不掉落"方块标签。属工具属性，与材料固有属性无关。 */
    private final TagKey<Block> incorrectBlocksForDrops;
    /** 工具耐久。 */
    private final int uses;
    /** 挖掘速度。 */
    private final float speed;
    /** 攻击伤害加成。 */
    private final float attackDamageBonus;
    /** 附魔能力。 */
    private final int enchantmentValue;
    /** 盔甲各部位的防御点数。 */
    private final Map<ArmorItem.Type, Integer> armorDefense;
    /** 盔甲韧性。 */
    private final float armorToughness;
    /** 盔甲击退抗性。 */
    private final float armorKnockbackResistance;
    /** 该材料要做成哪些形态。 */
    private final Set<Form> forms;
    /** 该材料的矿石生成参数；{@code null} 表示不生成矿石。 */
    private final Ore ore;

    private final String id;
    private final String cnName;
    private final String enName;
    private final MapColor color;

    AztMaterial(String id, String cnName, String enName, MapColor color,
                int uses, float speed, float attackDamageBonus, TagKey<Block> incorrectBlocksForDrops, int enchantmentValue,
                Map<ArmorItem.Type, Integer> armorDefense, float armorToughness, float armorKnockbackResistance,
                Set<Form> forms, Ore ore)
    {
        this.id = id;
        this.cnName = cnName;
        this.enName = enName;
        this.color = color;
        this.uses = uses;
        this.speed = speed;
        this.attackDamageBonus = attackDamageBonus;
        this.incorrectBlocksForDrops = incorrectBlocksForDrops;
        this.enchantmentValue = enchantmentValue;
        this.armorDefense = armorDefense;
        this.armorToughness = armorToughness;
        this.armorKnockbackResistance = armorKnockbackResistance;
        this.forms = forms;
        this.ore = ore;
    }

    /** 注册 id 前缀，同时是命名空间下的路径、贴图名与语言键的基础。 */
    public String getId()
    {
        return this.id;
    }

    /** 中文显示名。仅用于语言文件生成与文档。 */
    public String getCnName()
    {
        return this.cnName;
    }

    /** 英文显示名。仅用于语言文件生成。 */
    public String getEnName()
    {
        return this.enName;
    }

    public MapColor getColor()
    {
        return this.color;
    }

    public int getUses()
    {
        return this.uses;
    }

    public float getSpeed()
    {
        return this.speed;
    }

    public float getAttackDamageBonus()
    {
        return this.attackDamageBonus;
    }

    public TagKey<Block> getIncorrectBlocksForDrops()
    {
        return this.incorrectBlocksForDrops;
    }

    public int getEnchantmentValue()
    {
        return this.enchantmentValue;
    }

    public Map<ArmorItem.Type, Integer> getArmorDefense()
    {
        return this.armorDefense;
    }

    public float getArmorToughness()
    {
        return this.armorToughness;
    }

    public float getArmorKnockbackResistance()
    {
        return this.armorKnockbackResistance;
    }

    /** 该材料是否要做成指定形态。 */
    public boolean has(Form form)
    {
        return this.forms.contains(form);
    }

    /** 该材料的矿石生成参数；{@code null} 表示不生成矿石。 */
    public Ore getOre()
    {
        return this.ore;
    }

    /** 强制类初始化。由主类构造器调用，见 PLAN.md 10.5。 */
    public static void initialize()
    {
    }

    /**
     * 材料形态。
     * <p>
     * 组合集既可用 {@link #ALL} 这类预定义常量，也可用 {@link #of} 现场声明。
     * 预定义常量属于"预留的命名"：当前可能只有 {@link #ALL} 在用，
     * 但 {@link #SHARD_ONLY}、{@link #SWORD_SET} 描述了明确的组合语义，
     * 材料表里用它们比写 {@code Form.of(...)} 更清楚，故保留。
     */
    public enum Form
    {
        /** 碎片物品：处理链的中间产物，也是修复材料。 */
        SHARD,
        /** 剑。 */
        SWORD,
        /** 镐斧锹锄四件套。 */
        TOOL,
        /** 盔甲四件套。 */
        ARMOR;

        /** 全套形态：碎片 + 剑 + 工具 + 盔甲。 */
        public static final Set<Form> ALL = Set.of(SHARD, SWORD, TOOL, ARMOR);
        /** 仅碎片，不做装备（如晴空晶这类只服务机制的辅助材料）。 */
        public static final Set<Form> SHARD_ONLY = Set.of(SHARD);
        /** 碎片 + 剑 + 工具，不做盔甲。 */
        public static final Set<Form> SWORD_SET = Set.of(SHARD, SWORD, TOOL);

        /** 构造形态集。例如"只有碎片"也可写 {@code Form.of(Form.SHARD)}。 */
        public static Set<Form> of(Form first, Form... rest)
        {
            Set<Form> set = EnumSet.of(first, rest);
            return Set.copyOf(set);
        }
    }
}
