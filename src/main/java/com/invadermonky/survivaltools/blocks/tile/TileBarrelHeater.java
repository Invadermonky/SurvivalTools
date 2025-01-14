package com.invadermonky.survivaltools.blocks.tile;

import com.invadermonky.magicultureintegrations.api.tile.IHeatableTile;
import com.invadermonky.survivaltools.api.blocks.IBarrelHeater;
import com.invadermonky.survivaltools.blocks.BlockBarrelHeater;
import com.invadermonky.survivaltools.config.ConfigHandlerST;
import com.invadermonky.survivaltools.config.modules.ConfigOpenBarrel;
import com.invadermonky.survivaltools.util.helpers.StringHelper;
import com.invadermonky.survivaltools.util.libs.LibNames;
import com.invadermonky.survivaltools.util.libs.LibTags;
import com.invadermonky.survivaltools.util.libs.ModIds;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.SlotFurnaceFuel;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nullable;

@Optional.Interface(iface = "com.invadermonky.magicultureintegrations.api.tile.IHeatableTile", modid = ModIds.ConstIds.magicluture_integrations, striprefs = true)
public class TileBarrelHeater extends TileEntity implements ITickable, IInventory, IBarrelHeater, IHeatableTile {
    public static ConfigOpenBarrel.WaterBoilerConfig.FurnaceHeaterConfig config = ConfigHandlerST.open_barrel.water_boiler.barrel_heater;

    public ItemStackHandler fuelHandler = new ItemStackHandler(1);
    private int burnTime;
    private int itemBurnTime;

    @Override
    public boolean isHeaterActive() {
        return this.isBurning();
    }

    @Override
    public void update() {
        boolean isBurning = this.isBurning();
        boolean did = false;

        if(this.isBurning()) {
            --this.burnTime;
        }

        if(!this.world.isRemote) {
            ItemStack fuelStack = this.fuelHandler.getStackInSlot(0);
            if(this.isBurning() || !fuelStack.isEmpty()) {
                if(!this.isBurning() && this.canHeatTank()) {
                    this.burnTime = (int) (TileEntityFurnace.getItemBurnTime(fuelStack) * config.fuelEfficiency);
                    this.itemBurnTime = this.burnTime;

                    if(this.isBurning()) {
                        did = true;
                        if(!fuelStack.isEmpty()) {
                            Item item = fuelStack.getItem();
                            fuelStack.shrink(1);

                            if(fuelStack.isEmpty()) {
                                ItemStack stack = item.getContainerItem(fuelStack);
                                this.fuelHandler.setStackInSlot(0, stack);
                            }
                        }
                    }
                }
            }

            if(isBurning != this.isBurning()) {
                BlockBarrelHeater.setState(this.isBurning(), this.world, this.pos);
                did = true;
            }
        }

        if(did) {
            this.markDirty();
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        this.burnTime = compound.getInteger(LibTags.TAG_BURN_TIME);
        this.itemBurnTime = TileEntityFurnace.getItemBurnTime(this.fuelHandler.getStackInSlot(0));
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setInteger(LibTags.TAG_BURN_TIME, this.getField(0));
        return compound;
    }

    @Nullable
    @Override
    public ITextComponent getDisplayName() {
        return new TextComponentTranslation(StringHelper.getTranslationKey(LibNames.OPEN_BARREL_HEATER, "container"));
    }

    public boolean isBurning() {
        return this.getField(0) > 0;
    }

    public boolean canHeatTank() {
        TileEntity tile = this.world.getTileEntity(this.pos.up());
        return tile instanceof TileOpenBarrel && ((TileOpenBarrel) tile).checkWaterBoiler();
    }

    @SuppressWarnings("unchecked")
    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if(facing != null && capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            if(facing != EnumFacing.DOWN && facing != EnumFacing.UP) {
                return (T) fuelHandler;
            }
        }
        return super.getCapability(capability, facing);
    }

    /*
     *  IInventory
     */

    @Override
    public int getSizeInventory() {
        return this.fuelHandler.getSlots();
    }

    @Override
    public boolean isEmpty() {
        for(int i = 0; i < this.getSizeInventory(); i++) {
            if(!this.fuelHandler.getStackInSlot(i).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        return this.fuelHandler.getStackInSlot(index);
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        return this.fuelHandler.extractItem(index, count, false);
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        ItemStack slotStack = this.fuelHandler.getStackInSlot(index).copy();
        this.fuelHandler.setStackInSlot(index, ItemStack.EMPTY);
        return slotStack;
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        this.fuelHandler.setStackInSlot(index, stack);
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public boolean isUsableByPlayer(EntityPlayer player) {
        if(this.world.getTileEntity(this.pos) != this) {
            return false;
        } else {
            return player.getDistanceSq((double)this.pos.getX() + 0.5D, (double)this.pos.getY() + 0.5D, (double)this.pos.getZ() + 0.5D) <= 64.0D;
        }
    }

    @Override
    public void openInventory(EntityPlayer player) {}

    @Override
    public void closeInventory(EntityPlayer player) {}

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        ItemStack slotStack = this.fuelHandler.getStackInSlot(0);
        return TileEntityFurnace.isItemFuel(stack) || SlotFurnaceFuel.isBucket(stack) && slotStack.getItem() != Items.BUCKET;
    }

    @Override
    public int getField(int id) {
        switch (id) {
            case 0:
                return this.burnTime;
            case 1:
                return this.itemBurnTime;
            default:
                return 0;
        }
    }

    @Override
    public void setField(int id, int value) {
        switch (id) {
            case 0:
                this.burnTime = value;
                break;
            case 1:
                this.itemBurnTime = value;
                break;
        }
    }

    @Override
    public int getFieldCount() {
        return 2;
    }

    @Override
    public void clear() {
        for(int i = 0; i < this.fuelHandler.getSlots(); i++) {
            this.fuelHandler.setStackInSlot(i, ItemStack.EMPTY);
        }
    }

    @Override
    public String getName() {
        return StringHelper.getTranslationKey("tank_heater", "container");
    }

    @Override
    public boolean hasCustomName() {
        return false;
    }

    /*
     *  IHeatableTile
     */

    @Optional.Method(modid = ModIds.ConstIds.magicluture_integrations)
    @Override
    public boolean canSmeltHeatable() {
        return this.canHeatTank();
    }

    @Optional.Method(modid = ModIds.ConstIds.magicluture_integrations)
    @Override
    public int getBurnTimeHeatable() {
        return this.getField(0);
    }

    @Optional.Method(modid = ModIds.ConstIds.magicluture_integrations)
    @Override
    public int getBurnTimeMaxHeatable() {
        return this.getField(1);
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
    public void setBurnTimeMaxHeatable(int i) {
        this.setField(1, i);
    }

    @Optional.Method(modid = ModIds.ConstIds.magicluture_integrations)
    @Override
    public void boostBurnTimeHeatable(int i) {
        this.setField(0, this.getBurnTimeHeatable() + i);
        this.setBurnTimeMaxHeatable(this.getBurnTimeHeatable());
    }

    @Optional.Method(modid = ModIds.ConstIds.magicluture_integrations)
    @Override
    public void boostCookTimeHeatable(int i) {}

    @Optional.Method(modid = ModIds.ConstIds.magicluture_integrations)
    @Override
    public void updateTileHeatable() {
        BlockBarrelHeater.setState(this.isBurning(), this.world, this.pos);
        this.markDirty();
    }
}
