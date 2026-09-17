package xintao.azt.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import xintao.azt.AzureTraveller;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * 数据生成入口。
 * <p>
 * 挂在 mod 总线上——{@code GatherDataEvent} 只在 mod 总线触发。
 * 注意 NeoForge 已弃用 {@code @EventBusSubscriber} 的 {@code bus} 参数，改为依事件类型自动判定，
 * 因此此处不再显式指定。运行：{@code gradlew runData}，输出到 {@code src/generated/resources}。
 * <p>
 * <b>{@code src/generated} 不入库</b>，所以每次拉取代码后需先跑 {@code runData}，
 * 否则 {@code build} 产出的 jar 会缺少模型、战利品表、世界生成与语言文件。
 */
@EventBusSubscriber(modid = AzureTraveller.MOD_ID)
public class AztDataGenerator
{
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event)
    {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        ExistingFileHelper helper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookup = event.getLookupProvider();

        // ---- 客户端资源 ----
        // 中英文各一个 provider，覆盖全部物品名、方块名与配置界面文案
        generator.addProvider(event.includeClient(), new AztLangZhProvider(output));
        generator.addProvider(event.includeClient(), new AztLangEnProvider(output));
        // 模型 JSON。注意两类 provider 都要求贴图真实存在，缺图会抛异常导致 runData 失败；
        // 贴图占位图由 tools/GeneratePlaceholderTextures.ps1 生成。
        generator.addProvider(event.includeClient(), new AztItemModelProvider(output, helper));
        generator.addProvider(event.includeClient(), new AztBlockModelProvider(output, helper));

        // ---- 服务端数据 ----
        // 矿石掉落表
        generator.addProvider(event.includeServer(), new LootTableProvider(output, Set.of(),
                List.of(new LootTableProvider.SubProviderEntry(AztLootProvider::new, LootContextParamSets.BLOCK)),
                lookup));
        // 世界生成：configured_feature / placed_feature / biome_modifier 三件套
        generator.addProvider(event.includeServer(), new AztWorldGenProvider(output, lookup));
    }
}
