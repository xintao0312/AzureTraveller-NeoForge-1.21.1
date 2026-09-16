package xintao.azt.world.gen;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.placement.*;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.BiomeModifiers;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import xintao.azt.AzureTraveller;
import xintao.azt.registry.AztMaterial;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * 世界生成数据：把各材料的矿石注入主世界。
 * <p>
 * 三块数据必须齐备，缺一则矿石不会生成：
 * <ol>
 *     <li>{@code configured_feature} — 生成什么（矿脉大小、可替换的方块）</li>
 *     <li>{@code placed_feature} — 怎么放置（次数、高度、散布）</li>
 *     <li>{@code biome_modifier}（NeoForge 自有注册表）— 注入到哪些生物群系</li>
 * </ol>
 * 数值全部取自 {@link AztMaterial.Ore}，因此调整生成率只需改材料表。
 * <p>
 * 可替换的方块用标签而非具体方块 id：{@code stone_ore_replaceables} 与
 * {@code deepslate_ore_replaceables} 是原版对石/深层岩的可替换定义，跟随原版即可。
 */
public class AztWorldGenProvider extends DatapackBuiltinEntriesProvider
{
    private static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
            .add(Registries.CONFIGURED_FEATURE, AztWorldGenProvider::configuredFeatures)
            .add(Registries.PLACED_FEATURE, AztWorldGenProvider::placedFeatures)
            .add(NeoForgeRegistries.Keys.BIOME_MODIFIERS, AztWorldGenProvider::biomeModifiers);

    public AztWorldGenProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup)
    {
        super(output, lookup, BUILDER, Set.of(AzureTraveller.MOD_ID));
    }

    private static ResourceKey<ConfiguredFeature<?, ?>> configuredKey(AztMaterial material)
    {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE,
                ResourceLocation.fromNamespaceAndPath(AzureTraveller.MOD_ID, material.getId() + "_ore"));
    }

    private static ResourceKey<PlacedFeature> placedKey(AztMaterial material)
    {
        return ResourceKey.create(Registries.PLACED_FEATURE,
                ResourceLocation.fromNamespaceAndPath(AzureTraveller.MOD_ID, material.getId() + "_ore"));
    }

    private static void configuredFeatures(BootstrapContext<ConfiguredFeature<?, ?>> context)
    {
        for (AztMaterial material : AztMaterial.values())
        {
            AztMaterial.Ore ore = material.getOre();
            if (ore == null)
            {
                continue;
            }
            List<OreConfiguration.TargetBlockState> targets = List.of(
                    OreConfiguration.target(new TagMatchTest(BlockTags.STONE_ORE_REPLACEABLES),
                            xintao.azt.block.AztOreBlock.get(material).get().defaultBlockState()),
                    OreConfiguration.target(new TagMatchTest(BlockTags.DEEPSLATE_ORE_REPLACEABLES),
                            xintao.azt.block.AztOreBlock.get(material).get().defaultBlockState()));

            context.register(configuredKey(material),
                    new ConfiguredFeature<>(Feature.ORE,
                            new OreConfiguration(targets, ore.veinSize(), ore.airExposed() ? 0.5F : 0.0F)));
        }
    }

    private static void placedFeatures(BootstrapContext<PlacedFeature> context)
    {
        HolderGetter<ConfiguredFeature<?, ?>> configured = context.lookup(Registries.CONFIGURED_FEATURE);
        for (AztMaterial material : AztMaterial.values())
        {
            AztMaterial.Ore ore = material.getOre();
            if (ore == null)
            {
                continue;
            }
            // 放置链：每区块尝试次数 → 方块内随机散布 → 高度范围 → 生物群系过滤
            // 原版 OrePlacements 的 commonOrePlacement 是私有方法，故此处自行组合
            List<PlacementModifier> placement = List.of(
                    CountPlacement.of(ore.veinsPerChunk()),
                    InSquarePlacement.spread(),
                    HeightRangePlacement.uniform(VerticalAnchor.absolute(ore.minY()), VerticalAnchor.absolute(ore.maxY())),
                    BiomeFilter.biome());

            context.register(placedKey(material),
                    new PlacedFeature(configured.getOrThrow(configuredKey(material)), placement));
        }
    }

    private static void biomeModifiers(BootstrapContext<BiomeModifier> context)
    {
        HolderGetter<Biome> biomes = context.lookup(Registries.BIOME);
        HolderGetter<PlacedFeature> placed = context.lookup(Registries.PLACED_FEATURE);

        for (AztMaterial material : AztMaterial.values())
        {
            if (material.getOre() == null)
            {
                continue;
            }
            context.register(
                    ResourceKey.create(NeoForgeRegistries.Keys.BIOME_MODIFIERS,
                            ResourceLocation.fromNamespaceAndPath(AzureTraveller.MOD_ID, material.getId() + "_ore")),
                    new BiomeModifiers.AddFeaturesBiomeModifier(
                            biomes.getOrThrow(BiomeTags.IS_OVERWORLD),
                            HolderSet.direct(placed.getOrThrow(placedKey(material))),
                            GenerationStep.Decoration.UNDERGROUND_ORES));
        }
    }
}
