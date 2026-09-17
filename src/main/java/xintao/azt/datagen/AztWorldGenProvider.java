package xintao.azt.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import xintao.azt.AzureTraveller;
import xintao.azt.world.gen.AztWorldGen;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * 世界生成的 datagen provider——只是一个薄壳。
 * <p>
 * 真正的定义（矿石参数、注册表键、放置链）在 {@link AztWorldGen}，位于 {@code world.gen} 包。
 * 这样拆分是为了让两条规则都不破例：
 * <ul>
 *     <li>{@code world.*} 放世界生成逻辑（与 datagen 机制无关的纯定义）</li>
 *     <li>{@code datagen/} 放所有 DataProvider（与领域无关的技术层次）</li>
 * </ul>
 */
public class AztWorldGenProvider extends DatapackBuiltinEntriesProvider
{
    public AztWorldGenProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup)
    {
        super(output, lookup, AztWorldGen.builder(), Set.of(AzureTraveller.MOD_ID));
    }
}
