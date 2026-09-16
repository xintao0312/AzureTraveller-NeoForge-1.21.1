package xintao.azt.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import xintao.azt.AzureTraveller;
import xintao.azt.item.AztItem;
import xintao.azt.item.AztMaterialItem;

/**
 * 注册器（DeferredRegister）集中处。
 * <p>
 * 这里放的是"往哪本簿子上登记"的簿子本身，不是每一条登记内容：
 * <ul>
 *     <li>第一层（本类）：注册器，整个模组只需要几个 —— 每个注册表一个</li>
 *     <li>第二层（AztItem / AztBlock 等）：条目，每个物品/方块一条</li>
 * </ul>
 * 簿子必须挂上 mod 事件总线才会真正注册，见 {@link AzureTraveller} 构造函数。
 * <p>
 * 将来新增注册表（实体、附魔效果组件、粒子……）时，在此类追加对应的 DeferredRegister，
 * 并别忘了在 {@link AzureTraveller} 构造函数里 {@code XXX.register(modEventBus)}。
 */
public class AztRegistry
{
    // 方块注册器，所有注册项都在 "azt" 命名空间下
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(AzureTraveller.MOD_ID);
    // 物品注册器
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(AzureTraveller.MOD_ID);
    // 创造模式标签页注册器
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, AzureTraveller.MOD_ID);

    // 模组自己的创造栏，注册键为 "azt:azt_items"，排在原版"战斗"栏之前
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> AZT_ITEMS_TAB =
            CREATIVE_MODE_TABS.register("azt_items", () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.azt")) // 创造栏标题的语言键
                    .withTabsBefore(CreativeModeTabs.COMBAT)
                    .icon(() -> AztItem.EXAMPLE_ITEM.get().getDefaultInstance())
                    .displayItems((parameters, output) ->
                    {
                        // 自定义栏优先用本回调声明内容；给原版栏加东西才用 BuildCreativeModeTabContentsEvent
                        // 示例物品（MDK 演示，可删）
                        output.accept(AztItem.EXAMPLE_ITEM.get());
                        // 材料体系：条目由 AztMaterial 规格表批量生成，此处按同一张表遍历加入
                        for (AztMaterial material : AztMaterial.values())
                        {
                            // 矿石方块放在该材料物品之前，便于对照
                            if (AztMaterialItem.ORE_ITEM.containsKey(material))
                            {
                                output.accept(AztMaterialItem.ORE_ITEM.get(material).get());
                            }
                            output.accept(AztMaterialItem.SHARD.get(material).get());
                            if (AztMaterialItem.SWORD.containsKey(material))
                            {
                                output.accept(AztMaterialItem.SWORD.get(material).get());
                            }
                            if (AztMaterialItem.PICKAXE.containsKey(material))
                            {
                                output.accept(AztMaterialItem.PICKAXE.get(material).get());
                                output.accept(AztMaterialItem.AXE.get(material).get());
                                output.accept(AztMaterialItem.SHOVEL.get(material).get());
                                output.accept(AztMaterialItem.HOE.get(material).get());
                            }
                            if (AztMaterialItem.ARMOR.containsKey(material))
                            {
                                AztMaterialItem.ARMOR.get(material).values()
                                        .forEach(armor -> output.accept(armor.get()));
                            }
                        }
                    })
                    .build());
}
