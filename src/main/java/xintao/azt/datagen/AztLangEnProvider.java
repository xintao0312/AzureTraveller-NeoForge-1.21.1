package xintao.azt.datagen;

import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;
import xintao.azt.AzureTraveller;
import xintao.azt.item.AztMaterialItem;
import xintao.azt.registry.AztMaterial;

import java.util.Map;

/**
 * 英文语言文件生成。
 * <p>
 * 与 {@link AztLangZhProvider} 结构相同，只是名字用英文。
 * <p>
 * <b>重要：本 provider 是 {@code en_us.json} 的唯一来源。</b>
 * 不要在 {@code src/main/resources} 下再放一份同名文件——两个源集产出同一路径时，
 * Gradle 的 {@code processResources} 会让 {@code src/main} 覆盖 {@code src/generated}，
 * 届时本 provider 的产物会被静默丢弃。
 */
public class AztLangEnProvider extends LanguageProvider
{
    /** 形态名后缀。键必须与 {@link AztMaterialItem} 注册时使用的名称一致。 */
    private static final Map<String, String> ARMOR_SUFFIX = Map.of
        (
            "helmet", "Helmet",
            "chestplate", "Chestplate",
            "leggings", "Leggings",
            "boots", "Boots"
        );

    public AztLangEnProvider(PackOutput output)
    {
        super(output, AzureTraveller.MOD_ID, "en_us");
    }

    @Override
    protected void addTranslations()
    {
        this.add("itemGroup." + AzureTraveller.MOD_ID, "Azure Traveller");

        // 配置界面文案（键由 ModConfigSpec 依 modid 自动推导）
        this.add(AzureTraveller.MOD_ID + ".configuration.title", "Azure Traveller Configs");
        this.add(AzureTraveller.MOD_ID + ".configuration.section." + AzureTraveller.MOD_ID + ".common.toml", "Azure Traveller Configs");
        this.add(AzureTraveller.MOD_ID + ".configuration.section." + AzureTraveller.MOD_ID + ".common.toml.title", "Azure Traveller Configs");
        this.add(AzureTraveller.MOD_ID + ".configuration.items", "Item List");
        this.add(AzureTraveller.MOD_ID + ".configuration.logDirtBlock", "Log Dirt Block");
        this.add(AzureTraveller.MOD_ID + ".configuration.magicNumberIntroduction", "Magic Number Text");
        this.add(AzureTraveller.MOD_ID + ".configuration.magicNumber", "Magic Number");

        for (AztMaterial material : AztMaterial.values())
        {
            String prefix = material.getId();
            String name = material.getEnName();
            String id = AzureTraveller.MOD_ID + "." + prefix;

            this.add("item." + id + "_shard", name + " Shard");

            // 矿石方块：显示用的是 block 键
            if (material.getOre() != null)
            {
                this.add("block." + id + "_ore", name + " Ore");
            }

            if (material.has(AztMaterial.Form.SWORD))
            {
                this.add("item." + id + "_sword", name + " Sword");
            }
            if (material.has(AztMaterial.Form.TOOL))
            {
                this.add("item." + id + "_pickaxe", name + " Pickaxe");
                this.add("item." + id + "_axe", name + " Axe");
                this.add("item." + id + "_shovel", name + " Shovel");
                this.add("item." + id + "_hoe", name + " Hoe");
            }
            if (material.has(AztMaterial.Form.ARMOR))
            {
                for (Map.Entry<String, String> entry : ARMOR_SUFFIX.entrySet())
                {
                    this.add("item." + id + "_" + entry.getKey(), name + " " + entry.getValue());
                }
            }
        }
    }
}
