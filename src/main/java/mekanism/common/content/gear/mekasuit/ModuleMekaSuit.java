package mekanism.common.content.gear.mekasuit;

import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;
import mekanism.api.Action;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.gas.IGasHandler;
import mekanism.api.energy.IEnergyContainer;
import mekanism.api.inventory.AutomationType;
import mekanism.api.math.FloatingLong;
import mekanism.api.text.IHasTextComponent;
import mekanism.common.MekanismLang;
import mekanism.common.capabilities.Capabilities;
import mekanism.common.config.MekanismConfig;
import mekanism.common.content.gear.HUDElement;
import mekanism.common.content.gear.HUDElement.HUDColor;
import mekanism.common.content.gear.Module;
import mekanism.common.content.gear.ModuleConfigItem;
import mekanism.common.content.gear.ModuleConfigItem.BooleanData;
import mekanism.common.content.gear.ModuleConfigItem.EnumData;
import mekanism.common.content.gear.Modules;
import mekanism.common.content.gear.mekasuit.ModuleLocomotiveBoostingUnit.SprintBoost;
import mekanism.common.item.gear.ItemMekaSuitArmor;
import mekanism.common.lib.radiation.RadiationManager;
import mekanism.common.lib.radiation.capability.IRadiationEntity;
import mekanism.common.registries.MekanismGases;
import mekanism.common.registries.MekanismItems;
import mekanism.common.util.CapabilityUtils;
import mekanism.common.util.MekanismUtils;
import mekanism.common.util.MekanismUtils.ResourceType;
import mekanism.common.util.StorageUtils;
import mekanism.common.util.UnitDisplayUtils;
import mekanism.common.util.UnitDisplayUtils.RadiationUnit;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public abstract class ModuleMekaSuit extends Module {

    public static class ModuleElectrolyticBreathingUnit extends ModuleMekaSuit {

        @Override
        public void tickServer(PlayerEntity player) {
            if (player.func_233571_b_(FluidTags.WATER) > 1.6) {
                FloatingLong usage = MekanismConfig.general.FROM_H2.get().multiply(2);
                long maxRate = Math.min(getMaxRate(), getContainerEnergy().divide(usage).intValue());
                long hydrogenUsed = 0;
                GasStack hydrogenStack = MekanismGases.HYDROGEN.getStack(maxRate * 2);
                ItemStack chestStack = player.getItemStackFromSlot(EquipmentSlotType.CHEST);
                Optional<IGasHandler> chestCapability = MekanismUtils.toOptional(chestStack.getCapability(Capabilities.GAS_HANDLER_CAPABILITY));
                if (checkChestPlate(chestStack) && chestCapability.isPresent()) {
                    hydrogenUsed = maxRate * 2 - chestCapability.get().insertChemical(hydrogenStack, Action.EXECUTE).getAmount();
                    hydrogenStack.shrink(hydrogenUsed);
                }
                ItemStack handStack = player.getItemStackFromSlot(EquipmentSlotType.MAINHAND);
                Optional<IGasHandler> handCapability = MekanismUtils.toOptional(handStack.getCapability(Capabilities.GAS_HANDLER_CAPABILITY));
                if (handCapability.isPresent()) {
                    hydrogenUsed = maxRate * 2 - handCapability.get().insertChemical(hydrogenStack, Action.EXECUTE).getAmount();
                }
                long oxygenUsed = Math.min(maxRate, player.getMaxAir() - player.getAir());
                long used = Math.max((int) Math.ceil(hydrogenUsed / 2D), oxygenUsed);
                useEnergy(player, usage.multiply(used));
                player.setAir(player.getAir() + (int) oxygenUsed);
            }
        }

        /**
         * Checks whether the given chestplate should be filled with hydrogen, if it can store hydrogen. Does not check whether the chestplate can store hydrogen.
         *
         * @param chestPlate the chestplate to check
         *
         * @return whether the given chestplate should be filled with hydrogen.
         */
        private boolean checkChestPlate(ItemStack chestPlate) {
            if (chestPlate.getItem() == MekanismItems.MEKASUIT_BODYARMOR.get()) {
                return Modules.load(chestPlate, Modules.JETPACK_UNIT) != null;
            } else {
                return true;
            }
        }

        private int getMaxRate() {
            return (int) Math.pow(2, getInstalledCount());
        }
    }

    public static class ModuleInhalationPurificationUnit extends ModuleMekaSuit {

        @Override
        public void tickServer(PlayerEntity player) {
            for (EffectInstance effect : player.getActivePotionEffects()) {
                if (getContainerEnergy().smallerThan(MekanismConfig.gear.mekaSuitEnergyUsagePotionTick.get())) {
                    break;
                }
                useEnergy(player, MekanismConfig.gear.mekaSuitEnergyUsagePotionTick.get());
                for (int i = 0; i < 9; i++) {
                    effect.tick(player, () -> MekanismUtils.onChangedPotionEffect(player, effect, true));
                }
            }
        }
    }

    public static class ModuleVisionEnhancementUnit extends ModuleMekaSuit {

        private static final ResourceLocation icon = MekanismUtils.getResource(ResourceType.GUI_HUD, "vision_enhancement_unit.png");

        @Override
        public void tickServer(PlayerEntity player) {
            super.tickServer(player);
            useEnergy(player, MekanismConfig.gear.mekaSuitEnergyUsageVisionEnhancement.get());
        }

        @Override
        public void addHUDElements(List<HUDElement> list) {
            list.add(HUDElement.enabled(icon, isEnabled()));
        }

        @Override
        public void changeMode(@Nonnull PlayerEntity player, @Nonnull ItemStack stack, int shift, boolean displayChangeMessage) {
            toggleEnabled(player, MekanismLang.MODULE_VISION_ENHANCEMENT.translate());
        }
    }

    public static class ModuleRadiationShieldingUnit extends ModuleMekaSuit {}

    public static class ModuleGravitationalModulatingUnit extends ModuleMekaSuit {

        private static final ResourceLocation icon = MekanismUtils.getResource(ResourceType.GUI_HUD, "gravitational_modulation_unit.png");

        // we share with locomotive boosting unit
        private ModuleConfigItem<SprintBoost> speedBoost;

        @Override
        public void init() {
            super.init();
            addConfigItem(speedBoost = new ModuleConfigItem<>(this, "speed_boost", MekanismLang.MODULE_SPEED_BOOST, new EnumData<>(SprintBoost.class), SprintBoost.LOW));
        }

        @Override
        public void addHUDElements(List<HUDElement> list) {
            list.add(HUDElement.enabled(icon, isEnabled()));
        }

        @Override
        public void changeMode(@Nonnull PlayerEntity player, @Nonnull ItemStack stack, int shift, boolean displayChangeMessage) {
            toggleEnabled(player, MekanismLang.MODULE_GRAVITATIONAL_MODULATION.translate());
        }

        public float getBoost() {
            return speedBoost.get().getBoost();
        }
    }

    public static class ModuleHydraulicAbsorptionUnit extends ModuleMekaSuit {}

    public static class ModuleHydraulicPropulsionUnit extends ModuleMekaSuit {

        private ModuleConfigItem<JumpBoost> jumpBoost;
        private ModuleConfigItem<Boolean> stepAssist;

        @Override
        public void init() {
            super.init();
            addConfigItem(jumpBoost = new ModuleConfigItem<>(this, "jump_boost", MekanismLang.MODULE_JUMP_BOOST, new EnumData<>(JumpBoost.class, getInstalledCount() + 1), JumpBoost.LOW));
            stepAssist = addConfigItem(new ModuleConfigItem<>(this, "step_assist", MekanismLang.MODULE_STEP_ASSIST, new BooleanData(), true));
        }

        public float getBoost() {
            return jumpBoost.get().getBoost();
        }

        public boolean isStepAssistEnabled() {
            return stepAssist.get();
        }

        public enum JumpBoost implements IHasTextComponent {
            OFF(0),
            LOW(0.5F),
            MED(1F),
            HIGH(3),
            ULTRA(5);

            private final float boost;
            private final ITextComponent label;

            JumpBoost(float boost) {
                this.boost = boost;
                this.label = new StringTextComponent(Float.toString(boost));
            }

            @Override
            public ITextComponent getTextComponent() {
                return label;
            }

            public float getBoost() {
                return boost;
            }
        }
    }

    public static class ModuleSolarRechargingUnit extends ModuleMekaSuit {

        @Override
        public void tickServer(PlayerEntity player) {
            super.tickServer(player);
            IEnergyContainer energyContainer = StorageUtils.getEnergyContainer(getContainer(), 0);
            if (energyContainer != null && !energyContainer.getNeeded().isZero() && player.world.isDaytime() &&
                player.world.canSeeSky(new BlockPos(player.getPositionVec())) && !player.world.isRaining() && player.world.func_230315_m_().hasSkyLight()) {
                FloatingLong rate = MekanismConfig.gear.mekaSuitSolarRechargingRate.get().multiply(getInstalledCount());
                energyContainer.insert(rate, Action.EXECUTE, AutomationType.MANUAL);
            }
        }
    }

    public static class ModuleNutritionalInjectionUnit extends ModuleMekaSuit {

        private static final ResourceLocation icon = MekanismUtils.getResource(ResourceType.GUI_HUD, "nutritional_injection_unit.png");

        @Override
        public void tickServer(PlayerEntity player) {
            super.tickServer(player);
            FloatingLong usage = MekanismConfig.gear.mekaSuitEnergyUsageNutritionalInjection.get();
            if (MekanismUtils.isPlayingMode(player) && player.canEat(false) && getContainerEnergy().greaterOrEqual(usage)) {
                ItemMekaSuitArmor item = (ItemMekaSuitArmor) getContainer().getItem();
                long toFeed = Math.min(1, item.getContainedGas(getContainer(), MekanismGases.NUTRITIONAL_PASTE.get()).getAmount() / MekanismConfig.general.nutritionalPasteMBPerFood.get());
                if (toFeed > 0) {
                    useEnergy(player, usage.multiply(toFeed));
                    item.useGas(getContainer(), MekanismGases.NUTRITIONAL_PASTE.get(), toFeed * MekanismConfig.general.nutritionalPasteMBPerFood.get());
                    player.getFoodStats().addStats(1, MekanismConfig.general.nutritionalPasteSaturation.get());
                }
            }
        }

        @Override
        public void addHUDElements(List<HUDElement> list) {
            if (!isEnabled()) {
                return;
            }
            GasStack stored = ((ItemMekaSuitArmor) getContainer().getItem()).getContainedGas(getContainer(), MekanismGases.NUTRITIONAL_PASTE.get());
            double ratio = StorageUtils.getRatio(stored.getAmount(), ItemMekaSuitArmor.MAX_NUTRITIONAL_PASTE);
            list.add(HUDElement.percent(icon, ratio));
        }
    }

    public static class ModuleDosimeterUnit extends ModuleMekaSuit {

        private static final ResourceLocation icon = MekanismUtils.getResource(ResourceType.GUI_HUD, "dosimeter.png");

        @Override
        public void addHUDElements(List<HUDElement> list) {
            if (!isEnabled()) {
                return;
            }
            Optional<IRadiationEntity> capability = MekanismUtils.toOptional(CapabilityUtils.getCapability(Minecraft.getInstance().player,
                  Capabilities.RADIATION_ENTITY_CAPABILITY, null));
            if (capability.isPresent()) {
                double radiation = capability.get().getRadiation();
                HUDElement e = HUDElement.of(icon, UnitDisplayUtils.getDisplayShort(radiation, RadiationUnit.SV, 2));
                HUDColor color = radiation < RadiationManager.MIN_MAGNITUDE ? HUDColor.REGULAR : (radiation < 0.1 ? HUDColor.WARNING : HUDColor.DANGER);
                list.add(e.color(color));
            }
        }
    }
}
