package mekanism.tools.common.item;

import java.util.List;
import java.util.Locale;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import mekanism.client.render.ModelCustomArmor;
import mekanism.common.Mekanism;
import mekanism.common.util.text.TextComponentUtil;
import mekanism.common.util.text.Translation;
import mekanism.tools.common.IHasRepairType;
import mekanism.tools.common.MekanismTools;
import mekanism.tools.common.ToolsItem;
import mekanism.tools.common.material.IMekanismMaterial;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ItemMekanismArmor extends ArmorItem implements IHasRepairType {

    public ItemMekanismArmor(IMekanismMaterial material, EquipmentSlotType slot) {
        super(material, slot, new Item.Properties().group(Mekanism.tabMekanism));
        String name = null;
        if (slot == EquipmentSlotType.HEAD) {
            name = material.getRegistryPrefix() + "_helmet";
        } else if (slot == EquipmentSlotType.CHEST) {
            name = material.getRegistryPrefix() + "_chestplate";
        } else if (slot == EquipmentSlotType.LEGS) {
            name = material.getRegistryPrefix() + "_leggings";
        } else if (slot == EquipmentSlotType.FEET) {
            name = material.getRegistryPrefix() + "_boots";
        }
        if (name != null) {
            setRegistryName(new ResourceLocation(MekanismTools.MODID, name.toLowerCase(Locale.ROOT)));
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
        tooltip.add(TextComponentUtil.build(Translation.of("mekanism.tooltip.hp"), ": " + (stack.getMaxDamage() - stack.getDamage())));
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlotType slot, String type) {
        //TODO: What is the default value because maybe we don't even need this method
        int layer = slot == EquipmentSlotType.LEGS ? 2 : 1;
        return MekanismTools.MODID + ":textures/armor/" + getArmorMaterial().getName() + "_" + layer + ".png";
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public BipedModel getArmorModel(LivingEntity entityLiving, ItemStack itemStack, EquipmentSlotType armorSlot, BipedModel _default) {
        if (itemStack.getItem() == ToolsItem.REFINED_GLOWSTONE_HELMET.getItem() || itemStack.getItem() == ToolsItem.REFINED_GLOWSTONE_CHESTPLATE.getItem()
            || itemStack.getItem() == ToolsItem.REFINED_GLOWSTONE_LEGGINGS.getItem() || itemStack.getItem() == ToolsItem.REFINED_GLOWSTONE_BOOTS.getItem()) {
            return ModelCustomArmor.getGlow(armorSlot);
        }
        return super.getArmorModel(entityLiving, itemStack, armorSlot, _default);
    }

    @Nonnull
    @Override
    public Ingredient getRepairMaterial() {
        return getArmorMaterial().getRepairMaterial();
    }
}