package xintao.azt.datagen;

import net.minecraft.data.PackOutput;
import net.minecraft.world.item.ArmorItem;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredItem;
import xintao.azt.AzureTraveller;
import xintao.azt.item.AztMaterialItem;
import xintao.azt.registry.AztMaterial;

import java.util.Map;

/**
 * 物品模型生成。
 * <p>
 * 与 {@link AztBlockModelProvider} 的关键差别：{@code ItemModelProvider} <b>不校验</b>贴图是否存在，
 * 因此即便贴图尚未绘制也能正常生成模型 JSON。游戏内会显示紫黑格（缺贴图），但模型本身合法、不影响加载。
 * <p>
 * 将指向的贴图路径（相对于 {@code src/main/resources}）：
 * <pre>
 * assets/azt/textures/item/&lt;材料id&gt;_&lt;形态&gt;.png
 * assets/azt/textures/block/&lt;材料id&gt;_ore.png   （方块物品由 BlockStates provider 生成）
 * </pre>
 */
public class AztItemModelProvider extends ItemModelProvider
{
    public AztItemModelProvider(PackOutput output, ExistingFileHelper helper)
    {
        super(output, AzureTraveller.MOD_ID, helper);
    }

    @Override
    protected void registerModels()
    {
        for (AztMaterial material : AztMaterial.values())
        {
            // 碎片：普通平面物品
            this.basicItem(AztMaterialItem.SHARD.get(material).get());

            if (material.has(AztMaterial.Form.SWORD))
            {
                this.handheldItem(AztMaterialItem.SWORD.get(material).get());
            }
            if (material.has(AztMaterial.Form.TOOL))
            {
                // 工具用手持模型（倾斜显示，与原版一致）
                this.handheldItem(AztMaterialItem.PICKAXE.get(material).get());
                this.handheldItem(AztMaterialItem.AXE.get(material).get());
                this.handheldItem(AztMaterialItem.SHOVEL.get(material).get());
                this.handheldItem(AztMaterialItem.HOE.get(material).get());
            }
            if (material.has(AztMaterial.Form.ARMOR))
            {
                for (Map.Entry<ArmorItem.Type, DeferredItem<ArmorItem>> entry
                        : AztMaterialItem.ARMOR.get(material).entrySet())
                {
                    this.basicItem(entry.getValue().get());
                }
            }
        }
    }
}
