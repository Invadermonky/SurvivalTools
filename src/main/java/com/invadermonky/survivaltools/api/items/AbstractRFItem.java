package com.invadermonky.survivaltools.api.items;

import com.invadermonky.survivaltools.api.energy.EnergyContainerItemWrapper;
import com.invadermonky.survivaltools.api.energy.IEnergyContainerItem;
import com.invadermonky.survivaltools.config.ConfigHandlerST;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

import static com.invadermonky.survivaltools.util.libs.LibTags.TAG_ENERGY;

public abstract class AbstractRFItem extends Item implements IEnergyContainerItem {
    protected int energyCost;
    protected int energyCapacity;
    protected int energyMaxReceive;
    protected int energyMaxExtract;

    @Override
    public void getSubItems(@NotNull CreativeTabs tab, @NotNull NonNullList<ItemStack> items) {
        if (this.isInCreativeTab(tab)) {
            items.add(new ItemStack(this));
            if (ConfigHandlerST.general.enableFullVariants) {
                ItemStack stack = new ItemStack(this);
                this.setFull(stack);
                items.add(stack);
            }
        }
    }

    @Override
    public boolean showDurabilityBar(@NotNull ItemStack stack) {
        return true;
    }

    @Override
    public double getDurabilityForDisplay(@NotNull ItemStack stack) {
        double max = this.getMaxEnergyStored(stack);
        return (max - getEnergyStored(stack)) / max;
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(@NotNull ItemStack stack, @Nullable NBTTagCompound nbt) {
        return new EnergyContainerItemWrapper(stack, this, false, true);
    }

    public AbstractRFItem setEnergyCapacity(int energyCapacity) {
        this.energyCapacity = energyCapacity;
        return this;
    }

    public AbstractRFItem setMaxTransfer(int maxTransfer) {
        this.setEnergyMaxReceive(maxTransfer);
        this.setEnergyMaxExtract(maxTransfer);
        return this;
    }

    public AbstractRFItem setEnergyMaxReceive(int energyMaxReceive) {
        this.energyMaxReceive = energyMaxReceive;
        return this;
    }

    public AbstractRFItem setEnergyMaxExtract(int energyMaxExtract) {
        this.energyMaxExtract = energyMaxExtract;
        return this;
    }

    public NBTTagCompound getTag(ItemStack stack) {
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }
        return stack.getTagCompound();
    }

    public int getEnergyCost() {
        return this.energyCost;
    }

    public AbstractRFItem setEnergyCost(int energyCost) {
        this.energyCost = energyCost;
        return this;
    }

    public void setEnergyStored(ItemStack container, int energy) {
        this.getTag(container).setInteger(TAG_ENERGY, energy);
    }

    /*
     *  IEnergyContainerItem
     */

    public ItemStack setFull(ItemStack container) {
        this.setEnergyStored(container, this.energyCapacity);
        return container;
    }

    @Override
    public int receiveEnergy(ItemStack container, int maxReceive, boolean simulate) {
        int energy = this.getTag(container).getInteger(TAG_ENERGY);
        int energyReceived = Math.min(this.energyCapacity - energy, Math.min(this.energyMaxReceive, maxReceive));
        if (!simulate) {
            energy += energyReceived;
            container.getTagCompound().setInteger(TAG_ENERGY, energy);
        }
        return energyReceived;
    }

    @Override
    public int extractEnergy(ItemStack container, int maxExtract, boolean simulate) {
        if ((container.getTagCompound() == null) || (!container.getTagCompound().hasKey(TAG_ENERGY))) {
            return 0;
        }
        int energy = container.getTagCompound().getInteger(TAG_ENERGY);
        int energyExtracted = Math.min(energy, Math.min(this.energyMaxExtract, maxExtract));
        if (!simulate) {
            energy -= energyExtracted;
            container.getTagCompound().setInteger(TAG_ENERGY, energy);
        }
        return energyExtracted;
    }

    @Override
    public int getEnergyStored(ItemStack container) {
        if ((container.getTagCompound() == null) || (!container.getTagCompound().hasKey(TAG_ENERGY))) {
            return 0;
        }
        return container.getTagCompound().getInteger(TAG_ENERGY);
    }

    @Override
    public int getMaxEnergyStored(ItemStack container) {
        return this.energyCapacity;
    }


}
