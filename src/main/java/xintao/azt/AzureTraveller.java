package xintao.azt;

import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import org.slf4j.Logger;
import xintao.azt.block.AztBlock;
import xintao.azt.block.AztOreBlock;
import xintao.azt.item.AztArmorMaterial;
import xintao.azt.item.AztItem;
import xintao.azt.item.AztMaterialItem;
import xintao.azt.registry.AztMaterial;
import xintao.azt.registry.AztRegistry;

/**
 * 模组主类。只负责模组级别的三件事，不含任何内容定义：
 * <ol>
 *     <li>模组基本信息（{@link #MOD_ID}、{@link #LOGGER}）</li>
 *     <li>把注册器挂上事件总线、绑定生命周期与游戏事件</li>
 *     <li>注册配置文件</li>
 * </ol>
 * 物品/方块等内容定义一律不写在这里——见 {@code item/}、{@code block/} 与 {@link AztRegistry}。
 */
// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(AzureTraveller.MOD_ID)
public class AzureTraveller
{
    /** 模组 id。必须与 neoforge.mods.toml 中的 modId 一致。 */
    public static final String MOD_ID = "azt";

    /** 供全局引用的 slf4j 日志器。 */
    public static final Logger LOGGER = LogUtils.getLogger();

    /**
     * 构造器是模组加载时最先运行的代码。
     * FML 会自动识别 {@link IEventBus}、{@link ModContainer} 这类参数并注入，无需自行创建。
     */
    public AzureTraveller(IEventBus modEventBus, ModContainer modContainer)
    {
        // 强制初始化承载注册器的类。Java 的类初始化是惰性的，
        // 若不在此处显式引用，这些类会晚于 RegisterEvent 才被加载，导致注册失败并崩溃。
        AztBlock.initialize();
        AztItem.initialize();
        // 材料体系：先加载规格表、盔甲材质与矿石方块，再依规格表批量登记物品
        AztMaterial.initialize();
        AztArmorMaterial.initialize();
        AztOreBlock.initialize();
        AztMaterialItem.initialize();
        AztMaterialItem.register();

        // 绑定 mod 总线上的生命周期回调。
        modEventBus.addListener(this::commonSetup);

        // 注册器必须挂上 mod 总线才会真正注册，否则其中的条目会全部静默缺失。
        AztRegistry.BLOCKS.register(modEventBus);
        AztRegistry.ITEMS.register(modEventBus);
        AztRegistry.CREATIVE_MODE_TABS.register(modEventBus);
        AztArmorMaterial.register(modEventBus);

        // 把本实例注册到游戏事件总线，以接收下方 onServerStarting 这类运行期事件。
        // 注意区分：mod 总线承载生命周期与注册，游戏总线承载游戏运行期事件，挂错则事件静默不触发。
        NeoForge.EVENT_BUS.register(this);

        // 注册配置规格，由 FML 负责创建与读写 config/azt-common.toml。
        modContainer.registerConfig(ModConfig.Type.COMMON, AzureTravellerConfig.SPEC);
    }

    /** 通用初始化。在模组加载阶段触发一次，此处的日志是 MDK 演示内容，可直接删除。 */
    private void commonSetup(FMLCommonSetupEvent event)
    {
        LOGGER.info("HELLO FROM COMMON SETUP");
    }

    /** 服务端启动。演示游戏总线事件的写法：静态方法加 {@code @SubscribeEvent} 亦可，见 AzureTravellerClient。 */
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {
        LOGGER.info("HELLO from server starting");
    }
}
