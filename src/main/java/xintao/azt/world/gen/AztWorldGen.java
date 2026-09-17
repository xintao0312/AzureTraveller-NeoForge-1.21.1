package xintao.azt.world.gen;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
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
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.BiomeModifiers;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import xintao.azt.AzureTraveller;
import xintao.azt.block.AztOreBlock;
import xintao.azt.registry.AztMaterial;

import java.util.List;

/**
 * 世界生成的**纯定义**：矿石如何生成、生成在哪。不含任何 datagen 机制。
 * <p>
 * 三块数据必须齐备，缺一则矿石不会生成：
 * <ol>
 *     <li>{@code configured_feature} — 生成什么（矿脉大小、可替换的方块）</li>
 *     <li>{@code placed_feature} — 怎么放置（次数、高度、散布）</li>
 *     <li>{@code biome_modifier}（NeoForge 自有数据包注册表）— 注入到哪些生物群系</li>
 * </ol>
 * 数值全部取自 {@link AztMaterial.Ore}，因此调整生成率只需改材料表。
 * <p>
 * 输出 JSON 的职责在 {@code datagen/AztWorldGenProvider}，本类只负责"是什么"。
 */
public final class AztWorldGen
{
    /** 可挖掘方块的可替换标签：石与深层岩。用标签而非具体方块 id，跟随原版定义。 */
    private static final TagMatchTest STONE_REPLACEABLE = new TagMatchTest(BlockTags.STONE_ORE_REPLACEABLES);
    private static final TagMatchTest DEEPSLATE_REPLACEABLE = new TagMatchTest(BlockTags.DEEPSLATE_ORE_REPLACEABLES);

    private AztWorldGen()
    {
    }

    /** 构建本模组的全部数据包注册表条目。供 datagen 的 provider 调用。 */
    public static RegistrySetBuilder builder()
    {
        return new RegistrySetBuilder()
                .add(Registries.CONFIGURED_FEATURE, AztWorldGen::configuredFeatures)
                .add(Registries.PLACED_FEATURE, AztWorldGen::placedFeatures)
                .add(NeoForgeRegistries.Keys.BIOME_MODIFIERS, AztWorldGen::biomeModifiers);
    }

    // ---- 注册表键 ----

    private static ResourceKey<ConfiguredFeature<?, ?>> configuredKey(AztMaterial material)
    {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, id(material));
    }

    private static ResourceKey<PlacedFeature> placedKey(AztMaterial material)
    {
        return ResourceKey.create(Registries.PLACED_FEATURE, id(material));
    }

    private static ResourceKey<BiomeModifier> biomeModifierKey(AztMaterial material)
    {
        return ResourceKey.create(NeoForgeRegistries.Keys.BIOME_MODIFIERS, id(material));
    }

    private static ResourceLocation id(AztMaterial material)
    {
        return ResourceLocation.fromNamespaceAndPath(AzureTraveller.MOD_ID, material.getId() + "_ore");
    }

    // ---- 三件套 ----

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
                    OreConfiguration.target(STONE_REPLACEABLE, AztOreBlock.get(material).get().defaultBlockState()),
                    OreConfiguration.target(DEEPSLATE_REPLACEABLE, AztOreBlock.get(material).get().defaultBlockState()));

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
            context.register(biomeModifierKey(material),
                    new BiomeModifiers.AddFeaturesBiomeModifier(
                            biomes.getOrThrow(BiomeTags.IS_OVERWORLD),
                            HolderSet.direct(placed.getOrThrow(placedKey(material))),
                            GenerationStep.Decoration.UNDERGROUND_ORES));
        }
    }
}
