package xintao.azt.datagen;

import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import xintao.azt.AzureTraveller;
import xintao.azt.block.AztOreBlock;
import xintao.azt.registry.AztMaterial;

/**
 * 方块状态与方块模型生成。
 * <p>
 * <b>前置条件：贴图必须已存在。</b> {@code BlockStateProvider} 会校验贴图是否在资源包中真实存在，
 * 缺失时直接抛 {@code IllegalArgumentException: Texture ... does not exist in any known resource pack}，
 * 导致整个 runData 失败。因此在画好方块贴图之前，<b>不要</b>在 {@code AztDataGenerator} 里注册本 provider。
 * <p>
 * 需要的贴图（16×16 PNG，路径相对于 {@code src/main/resources}）：
 * <pre>
 * assets/azt/textures/block/&lt;材料id&gt;_ore.png
 * </pre>
 * 若改用程序化占位贴图，只需把 PNG 放到上述路径即可启用本 provider。
 */
public class AztBlockModelProvider extends BlockStateProvider
{
    public AztBlockModelProvider(PackOutput output, ExistingFileHelper helper)
    {
        super(output, AzureTraveller.MOD_ID, helper);
    }

    @Override
    protected void registerStatesAndModels()
    {
        for (AztMaterial material : AztMaterial.values())
        {
            if (material.getOre() == null)
            {
                continue;
            }
            // 矿石是标准立方体：方块状态 + 方块模型 + 对应物品模型一次生成
            this.simpleBlockWithItem(AztOreBlock.get(material).get(),
                    this.cubeAll(AztOreBlock.get(material).get()));
        }
    }
}
