package xintao.azt.item;

import com.google.common.base.Suppliers;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import xintao.azt.AzureTraveller;
import xintao.azt.registry.AztMaterial;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * 盔甲材质登记。数值全部来自 {@link AztMaterial}，本类只负责组装与登记。
 * <p>
 * 与原版 {@code ArmorMaterials} 的差异：原版把贴图与装备音效都写在此处，
 * 本项目改为按材料 id 推导贴图路径（{@code textures/models/armor/<id>_layer_1.png}），
 * 装备音效统一使用原版铁质音效——将来要独立音效时只需改 {@link #EQUIP_SOUND}。
 */
public class AztArmorMaterial
{
    /** 盔甲材质注册器。 */
    public static final DeferredRegister<ArmorMaterial> ARMOR_MATERIAL =
            DeferredRegister.create(Registries.ARMOR_MATERIAL, AzureTraveller.MOD_ID);

    /** 装备音效。改用原版铁质音效，后续可替换为自定义 SoundEvent。 */
    private static final Holder<SoundEvent> EQUIP_SOUND = SoundEvents.ARMOR_EQUIP_IRON;

    /** 各材料的盔甲材质句柄。 */
    private static final Map<AztMaterial, DeferredHolder<ArmorMaterial, ArmorMaterial>> MATERIAL = new EnumMap<>(AztMaterial.class);

    static
    {
        for (AztMaterial material : AztMaterial.values())
        {
            if (!material.has(AztMaterial.Form.ARMOR))
            {
                continue;
            }
            MATERIAL.put(material, ARMOR_MATERIAL.register(material.getId(), () -> create(material)));
        }
    }

    private static ArmorMaterial create(AztMaterial material)
    {
        return new ArmorMaterial
            (
                new EnumMap<>(material.getArmorDefense()),
                material.getEnchantmentValue(),
                EQUIP_SOUND,
                Suppliers.memoize(() -> Ingredient.of(AztMaterialItem.SHARD.get(material))),
                List.of(new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath(AzureTraveller.MOD_ID, material.getId()))),
                material.getArmorToughness(),
                material.getArmorKnockbackResistance()
            );
    }

    /** 取指定材料的盔甲材质。仅对声明了 {@code Form.ARMOR} 的材料有效。 */
    public static Holder<ArmorMaterial> get(AztMaterial material)
    {
        DeferredHolder<ArmorMaterial, ArmorMaterial> holder = MATERIAL.get(material);
        if (holder == null)
        {
            throw new IllegalArgumentException("材料 " + material.getId() + " 未声明 Form.ARMOR，没有对应的盔甲材质");
        }
        return holder;
    }

    /** 把盔甲材质注册器挂上 mod 总线。由主类构造器调用。 */
    public static void register(IEventBus modEventBus)
    {
        ARMOR_MATERIAL.register(modEventBus);
    }

    /** 强制类初始化。由主类构造器调用，见 PLAN.md 10.5。 */
    public static void initialize()
    {
    }
}
