package com.invadermonky.survivaltools.tile.base;

import com.invadermonky.survivaltools.blocks.base.AbstractBlockWaterPurifier;
import com.invadermonky.survivaltools.crafting.purifier.PurifierRecipe;
import com.invadermonky.survivaltools.crafting.purifier.PurifierRecipeRegistry;
import com.invadermonky.survivaltools.inventory.handler.FilteredStackRangedWrapper;
import com.invadermonky.survivaltools.inventory.handler.FluidContainerRangedWrapper;
import com.invadermonky.survivaltools.network.PacketHandlerST;
import com.invadermonky.survivaltools.network.packets.PacketDrainPurifierTank;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractTileWaterPurifier extends AbstractTickableTileST implements IFluidHandler {
    public static final int TANK_CAPACITY = 4000;

    protected FluidTank inputTank;
    protected FluidTank outputTank;
    protected ItemStackHandler itemHandler;
    protected FilteredStackRangedWrapper filterItemHandler;
    protected FluidContainerRangedWrapper outputFillHandler;
    protected PurifierRecipe recipe;
    protected int progress;
    protected int maxProgress;

    public AbstractTileWaterPurifier(int additionalSlotCount) {
        this.inputTank = new FluidTank(TANK_CAPACITY);
        this.inputTank.setCanDrain(false);
        this.inputTank.setTileEntity(this);

        this.outputTank = new FluidTank(TANK_CAPACITY);
        this.outputTank.setCanFill(false);
        this.outputTank.setTileEntity(this);

        this.itemHandler = new ItemStackHandler(2 + additionalSlotCount) {
            @Override
            public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
                if (!this.isItemValid(slot, stack))
                    return stack;
                return super.insertItem(slot, stack, simulate);
            }

            @Override
            public int getSlotLimit(int slot) {
                return slot == 1 ? 1 : super.getSlotLimit(slot);
            }

            @Override
            protected void onContentsChanged(int slot) {
                AbstractTileWaterPurifier.this.markDirty();
            }
        };

        this.filterItemHandler = new FilteredStackRangedWrapper(this.itemHandler, 0, 1, PurifierRecipeRegistry::isValidFilterItem);
        this.outputFillHandler = new FluidContainerRangedWrapper(this.itemHandler, 1, 2);
    }

    public AbstractTileWaterPurifier() {
        this(0);
    }

    @Override
    public void update() {
        boolean did = false;
        boolean recipeProcessed = false;
        IBlockState state = this.world.getBlockState(this.pos);
        boolean isRunning = state.getPropertyKeys().contains(AbstractBlockWaterPurifier.ENABLED) ? state.getValue(AbstractBlockWaterPurifier.ENABLED) : false;

        if (!this.world.isRemote) {
            //Resetting recipe and progress on recipe completion
            if (this.progress >= this.maxProgress) {
                this.progress = 0;
                this.recipe = null;
                did = true;
            }

            //Getting purifier recipe
            if (this.recipe == null) {
                this.recipe = PurifierRecipeRegistry.getRecipe(this.inputTank.getFluid(), this.getFilterStack());
            }

            if (this.recipe != null) {
                if (this.maxProgress != this.recipe.getRecipeDuration()) {
                    this.maxProgress = this.recipe.getRecipeDuration();
                    did = true;
                }
                //Starting new recipe progress
                if (this.progress <= 0 && this.hasFuel() && this.canOutputFluid()) {
                    this.consumeFilterStack();
                    did = true;
                }

                if (this.progress < this.maxProgress && this.hasFuel()) {
                    int inputDrain = this.recipe.getInputFluid().amount / this.maxProgress;
                    int outputFill = this.recipe.getOutputFluid().amount / this.maxProgress;

                    if (this.inputTank.getFluidAmount() >= inputDrain && (this.outputTank.getCapacity() - this.outputTank.getFluidAmount()) >= outputFill) {
                        this.inputTank.drainInternal(inputDrain, true);
                        this.outputTank.fillInternal(new FluidStack(this.recipe.getOutputFluid().getFluid(), outputFill), true);
                        this.progress++;
                        recipeProcessed = true;
                        did = true;
                    }
                }
            }

            if (recipeProcessed) {
                this.consumeFuelActive();
            } else if (this.consumeFuelPassive()) {
                did = true;
            }

            if (this.transferFluidToContainer()) {
                did = true;
            }

            if (this.onTileUpdate()) {
                did = true;
            }

            if (isRunning != this.isRunning()) {
                this.setEnabled(this.isRunning());
            }
        }

        if (did) {
            this.markDirty();
        }
    }

    protected boolean transferFluidToContainer() {
        //Filling and transferring
        ItemStack fillStack = this.getOutputFillStack();
        if (!fillStack.isEmpty() && fillStack.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null)) {
            FluidActionResult result = FluidUtil.tryFillContainer(fillStack, this.outputTank, this.outputTank.getCapacity(), null, false);
            if (result.isSuccess()) {
                fillStack = this.outputFillHandler.extractInternal(0, fillStack.getCount(), false);
                result = FluidUtil.tryFillContainer(fillStack, this.outputTank, this.outputTank.getCapacity(), null, true);
                this.outputFillHandler.insertItem(0, result.getResult(), false);
                return true;
            }
        }
        return false;
    }

    @Override
    public void readFromNBT(@NotNull NBTTagCompound compound) {
        super.readFromNBT(compound);
        this.inputTank.readFromNBT(compound.getCompoundTag("inputTank"));
        this.outputTank.readFromNBT(compound.getCompoundTag("outputTank"));
        this.itemHandler.deserializeNBT(compound.getCompoundTag("inventory"));
        this.progress = compound.getInteger("progress");
        this.maxProgress = compound.getInteger("maxProgress");
        this.readFromTileNBT(compound);
    }

    @Override
    public @NotNull NBTTagCompound writeToNBT(@NotNull NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setTag("inputTank", this.inputTank.writeToNBT(new NBTTagCompound()));
        compound.setTag("outputTank", this.outputTank.writeToNBT(new NBTTagCompound()));
        compound.setTag("inventory", this.itemHandler.serializeNBT());
        compound.setInteger("progress", this.progress);
        compound.setInteger("maxProgress", this.maxProgress);
        return this.writeToTileNBT(compound);
    }

    @Override
    public boolean hasCapability(@NotNull Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY
                || capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY
                || this.hasTileCapability(capability, facing)
                || super.hasCapability(capability, facing);
    }

    @Override
    public @Nullable <T> T getCapability(@NotNull Capability<T> capability, @Nullable EnumFacing facing) {
        T tileCap = this.getTileCapability(capability, facing);
        if (tileCap != null) {
            return tileCap;
        } else if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            if (facing == EnumFacing.UP) {
                return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(this.filterItemHandler);
            } else {
                return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(this.outputFillHandler);
            }
        } else if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(this);
        } else {
            return super.getCapability(capability, facing);
        }
    }

    @Override
    public IFluidTankProperties[] getTankProperties() {
        return new IFluidTankProperties[]{
                this.inputTank.getTankProperties()[0],
                this.outputTank.getTankProperties()[0]
        };
    }

    @Override
    public int fill(FluidStack resource, boolean doFill) {
        if (validateFluid(resource) && this.inputTank.fill(resource, false) > 0 && PurifierRecipeRegistry.isValidInputFluid(resource)) {
            int fill = this.inputTank.fill(resource, false);
            if (doFill && fill > 0) {
                this.inputTank.fill(resource, true);
                this.markDirty();
            }
            return fill;
        }
        return 0;
    }

    @Override
    public @Nullable FluidStack drain(FluidStack resource, boolean doDrain) {
        if (this.outputTank.getFluid() != null && validateFluid(resource) && this.outputTank.getFluid().getFluid() == resource.getFluid()) {
            FluidStack drained = this.outputTank.drain(resource, false);
            if (doDrain && drained != null) {
                this.outputTank.drain(resource, true);
                this.markDirty();
            }
            return drained;
        }
        return null;
    }

    @Override
    public @Nullable FluidStack drain(int maxDrain, boolean doDrain) {
        if (this.outputTank.getFluid() != null && maxDrain > 0) {
            return this.drain(new FluidStack(this.outputTank.getFluid().getFluid(), maxDrain), doDrain);
        }
        return null;
    }

    public void dropInventory() {
        for (int i = 0; i < this.itemHandler.getSlots(); i++) {
            ItemStack stack = this.itemHandler.getStackInSlot(i);
            if (!stack.isEmpty())
                Block.spawnAsEntity(this.world, this.pos, this.itemHandler.extractItem(i, stack.getCount(), false));
        }
    }

    public FluidTank getInputFluidTank() {
        return this.inputTank;
    }

    public FluidTank getOutputFluidTank() {
        return this.outputTank;
    }

    public ItemStackHandler getItemHandler() {
        return this.itemHandler;
    }

    public ItemStack getFilterStack() {
        return this.filterItemHandler.getStackInSlot(0);
    }

    public ItemStack getOutputFillStack() {
        return this.outputFillHandler.getStackInSlot(0);
    }

    public int getProgress() {
        return this.progress;
    }

    public void setProgress(int progress) {
        this.progress = Math.min(progress, this.getMaxProgress());
    }

    public int getMaxProgress() {
        return this.maxProgress;
    }

    public void setMaxProgress(int maxProgress) {
        this.maxProgress = maxProgress;
    }

    public void setInputFluidAmount(int amount) {
        FluidStack fluidStack = this.inputTank.getFluid();
        if (fluidStack != null) {
            this.inputTank.setFluid(new FluidStack(fluidStack.getFluid(), amount));
        }
    }

    public void setOutputFluidAmount(int amount) {
        FluidStack fluidStack = this.outputTank.getFluid();
        if (fluidStack != null) {
            this.outputTank.setFluid(new FluidStack(fluidStack.getFluid(), amount));
        }
    }

    public EnumFacing getFrontFacing() {
        return this.world.getBlockState(this.pos).getValue(AbstractBlockWaterPurifier.FACING);
    }

    protected void consumeFilterStack() {
        if (this.recipe == null || this.recipe.getFilterIngredient() == Ingredient.EMPTY)
            return;

        ItemStack stack = this.filterItemHandler.extractItem(0, this.filterItemHandler.getSlotLimit(0), false);
        if (stack.getItem().hasContainerItem(stack)) {
            stack = stack.getItem().getContainerItem(stack);
        } else if (stack.isItemStackDamageable()) {
            stack.getItem().setDamage(stack, stack.getItemDamage() + 1);
        } else {
            stack.shrink(1);
        }
        if (!stack.isEmpty()) {
            this.filterItemHandler.insertItem(0, stack, false);
        }
    }

    public boolean isEnabled() {
        IBlockState state = this.world.getBlockState(this.pos);
        if (state.getBlock() instanceof AbstractBlockWaterPurifier) {
            return state.getValue(AbstractBlockWaterPurifier.ENABLED);
        }
        return false;
    }

    public void setEnabled(boolean enabled) {
        IBlockState state = this.world.getBlockState(this.pos);
        if (state.getBlock() instanceof AbstractBlockWaterPurifier) {
            this.world.setBlockState(this.pos, state.withProperty(AbstractBlockWaterPurifier.ENABLED, enabled));
            this.validate();
            this.world.setTileEntity(this.pos, this);
        }
    }

    protected boolean validateFluid(FluidStack fluidStack) {
        return fluidStack != null && fluidStack.amount > 0;
    }

    protected boolean canOutputFluid() {
        if (this.recipe == null) {
            return false;
        } else if (this.outputTank.getFluid() == null) {
            return true;
        } else {
            return this.outputTank.getFluid().amount < TANK_CAPACITY && recipe.getOutputFluid().getFluid() == this.outputTank.getFluid().getFluid();
        }
    }

    /**
     * Runs on {@link ITickable#update()}. This method should return true if the tile needs to be updated.
     */
    public abstract boolean onTileUpdate();

    public abstract boolean isRunning();

    public abstract void readFromTileNBT(NBTTagCompound compound);

    public abstract @NotNull NBTTagCompound writeToTileNBT(NBTTagCompound compound);

    public abstract boolean hasTileCapability(Capability<?> capability, @Nullable EnumFacing facing);

    public abstract @Nullable <T> T getTileCapability(Capability<T> capability, @Nullable EnumFacing facing);

    public abstract boolean hasFuel();

    public abstract boolean consumeFuelPassive();

    public abstract void consumeFuelActive();

    public void emptyInputFluidTank() {
        this.inputTank.drainInternal(this.inputTank.getCapacity(), true);
        this.recipe = null;
        if (!this.world.isRemote) {
            this.markDirty();
        } else {
            PacketHandlerST.INSTANCE.sendToServer(new PacketDrainPurifierTank(this.pos, (byte) 0));
        }
    }

    public void emptyOutputFluidTank() {
        if (!this.world.isRemote) {
            this.outputTank.drainInternal(this.outputTank.getCapacity(), true);
            this.markDirty();
        } else {
            PacketHandlerST.INSTANCE.sendToServer(new PacketDrainPurifierTank(this.pos, (byte) 1));
        }
    }

}
