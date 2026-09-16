package xintao.azt.datagen;

import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;
import xintao.azt.AzureTraveller;
import xintao.azt.item.AztMaterialItem;
import xintao.azt.registry.AztMaterial;

import java.util.Map;

/**
 * 中文语言文件生成（{@code zh_cn.json}）。
 * <p>
 * 命名采用"材料名 + 形态名"的组合：材料名取自 {@link AztMaterial}，形态名固定，
 * 因此新增材料时物品名会自动生成，不需要补语言键。
 * <p>
 * 英文版见 {@link AztLangEnProvider}，两者结构相同，仅名字与形态名不同。
 * <b>{@code zh_cn.json} 由本 provider 独占生成</b>，不要在 {@code src/main/resources} 下另放同名文件
 * （两源集同路径时 {@code processResources} 会让 {@code src/main} 覆盖 {@code src/generated}）。
 */
public class AztLangZhProvider extends LanguageProvider
{
    /** 形态名后缀。键与 {@link AztMaterialItem} 里注册的命名保持一致。 */
    private static final Map<String, String> ARMOR_SUFFIX = Map.of
        (
            "helmet", "头盔",
            "chestplate", "胸甲",
            "leggings", "护腿",
            "boots", "靴子"
        );

    public AztLangZhProvider(PackOutput output)
    {
        super(output, AzureTraveller.MOD_ID, "zh_cn");
    }

    @Override
    protected void addTranslations()
    {
        // 创造栏标题
        this.add("itemGroup." + AzureTraveller.MOD_ID, "蔚蓝旅行家");

        for (AztMaterial material : AztMaterial.values())
        {
            String prefix = material.getId();
            String name = material.getCnName();

            this.add("item." + AzureTraveller.MOD_ID + "." + prefix + "_shard", name + "碎片");

            // 矿石方块：显示用的是 block 键
            if (material.getOre() != null)
            {
                this.add("block." + AzureTraveller.MOD_ID + "." + prefix + "_ore", name + "矿石");
            }

            if (material.has(AztMaterial.Form.SWORD))
            {
                this.add("item." + AzureTraveller.MOD_ID + "." + prefix + "_sword", name + "剑");
            }
            if (material.has(AztMaterial.Form.TOOL))
            {
                this.add("item." + AzureTraveller.MOD_ID + "." + prefix + "_pickaxe", name + "镐");
                this.add("item." + AzureTraveller.MOD_ID + "." + prefix + "_axe", name + "斧");
                this.add("item." + AzureTraveller.MOD_ID + "." + prefix + "_shovel", name + "锹");
                this.add("item." + AzureTraveller.MOD_ID + "." + prefix + "_hoe", name + "锄");
            }
            if (material.has(AztMaterial.Form.ARMOR))
            {
                for (Map.Entry<String, String> entry : ARMOR_SUFFIX.entrySet())
                {
                    this.add("item." + AzureTraveller.MOD_ID + "." + prefix + "_" + entry.getKey(), name + entry.getValue());
                }
            }
        }
    }
}
