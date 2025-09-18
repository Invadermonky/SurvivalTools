package com.invadermonky.survivaltools.compat.survivaltools.tiles;

import com.invadermonky.magicultureintegrations.api.tile.IHeatableTile;
import com.invadermonky.survivaltools.api.tiles.AbstractTileWaterPurifier;
import com.invadermonky.survivaltools.inventory.handler.FilteredStackRangedWrapper;
import com.invadermonky.survivaltools.util.libs.ModIds;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.items.CapabilityItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Optional.Interface(modid = ModIds.ConstIds.magicluture_integrations, iface = "com.invadermonky.magicultureintegrations.api.tile.IHeatableTile", striprefs = true)
public class TileSolidFuelPurifier extends AbstractTileWaterPurifier implements IHeatableTile {
    protected FilteredStackRangedWrapper fuelItemHandler;

    protected int burnTime = 0;
    protected int burnTimeMax = 0;

    public TileSolidFuelPurifier() {
        super(1);
        this.fuelItemHandler = new FilteredStackRangedWrapper(this.itemHandler, 2, 3, TileEntityFurnace::isItemFuel);
    }

    @Override
    public void dropInventory() {
        this.burnTime = 0;
        this.burnTimeMax = 0;
        super.dropInventory();
    }

    @Override
    public boolean onTileUpdate() {
        boolean did = false;
        if (this.burnTime <= 0 && this.burnTimeMax > 0) {
            this.burnTime = 0;
            this.burnTimeMax = 0;
            did = true;
        }
        return did;
    }

    @Override
    public boolean isRunning() {
        return this.isBurning();
    }

    @Override
    public void readFromTileNBT(NBTTagCompound compound) {
        this.burnTime = compound.getInteger("burnTime");
        this.burnTimeMax = compound.getInteger("burnTimeMax");
    }

    @Override
    public @NotNull NBTTagCompound writeToTileNBT(NBTTagCompound compound) {
        compound.setInteger("burnTime", this.burnTime);
        compound.setInteger("burnTimeMax", this.burnTimeMax);
        return compound;
    }

    @Override
    public boolean hasTileCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        EnumFacing front = this.getFrontFacing();
        return front.getOpposite() == facing && capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY;
    }

    @Override
    public <T> @Nullable T getTileCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        return hasTileCapability(capability, facing) ? CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(this.fuelItemHandler) : null;
    }

    @Override
    public boolean hasFuel() {
        return this.burnTime > 0 || TileEntityFurnace.isItemFuel(this.getFuelStack());
    }

    @Override
    public boolean consumeFuelPassive() {
        if (this.burnTime > 0) {
            this.burnTime--;
            return true;
        }
        return false;
    }

    @Override
    public void consumeFuelActive() {
        if (this.burnTime <= 0) {
            this.burnTimeMax = this.consumeFuelStack();
            this.burnTime = this.burnTimeMax;
        }
        this.burnTime--;
    }

    public boolean isBurning() {
        return this.burnTime > 0;
    }

    public ItemStack getFuelStack() {
        return this.fuelItemHandler.getStackInSlot(0);
    }

    public int getBurnTime() {
        return burnTime;
    }

    public void setBurnTime(int burnTime) {
        this.burnTime = burnTime;
    }

    public int getBurnTimeMax() {
        return burnTimeMax;
    }

    public void setBurnTimeMax(int burnTimeMax) {
        this.burnTimeMax = burnTimeMax;
    }

    public int consumeFuelStack() {
        ItemStack stack = this.fuelItemHandler.extractItem(0, this.fuelItemHandler.getSlotLimit(0), false);
        int itemBurnDuration = TileEntityFurnace.getItemBurnTime(stack);
        if (stack.getItem().hasContainerItem(stack)) {
            stack = stack.getItem().getContainerItem(stack);
        } else {
            stack.shrink(1);
        }
        if (!stack.isEmpty()) {
            this.fuelItemHandler.insertInternal(0, stack, false);
        }
        return itemBurnDuration;
    }

     /* ####################
        IHeatableTile
     */

    @Optional.Method(modid = ModIds.ConstIds.magicluture_integrations)
    @Override
    public boolean canSmeltHeatable() {
        return this.recipe != null;
    }

    @Optional.Method(modid = ModIds.ConstIds.magicluture_integrations)
    @Override
    public int getBurnTimeHeatable() {
        return this.getBurnTime();
    }

    @Optional.Method(modid = ModIds.ConstIds.magicluture_integrations)
    @Override
    public int getBurnTimeMaxHeatable() {
        return this.getBurnTimeMax();
    }

    @Optional.Method(modid = ModIds.ConstIds.magicluture_integrations)
    @Override
    public void setBurnTimeMaxHeatable(int burnTimeMax) {
        this.setBurnTimeMax(burnTimeMax);
    }

    @Optional.Method(modid = ModIds.ConstIds.magicluture_integrations)
    @Override
    public int getCookTimeHeatable() {
        return 0;
    }

    @Optional.Method(modid = ModIds.ConstIds.magicluture_integrations)
    @Override
    public int getCookTimeMaxHeatable() {
        return 0;
    }

    @Optional.Method(modid = ModIds.ConstIds.magicluture_integrations)
    @Override
    public void boostBurnTimeHeatable(int boostAmount) {
        this.setBurnTime(this.burnTime + boostAmount);
    }

    @Optional.Method(modid = ModIds.ConstIds.magicluture_integrations)
    @Override
    public void boostCookTimeHeatable(int boostAmount) {

    }

    @Optional.Method(modid = ModIds.ConstIds.magicluture_integrations)
    @Override
    public void updateTileHeatable() {
        this.markDirty();
    }

}
