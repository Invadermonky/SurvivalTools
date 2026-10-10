package com.invadermonky.survivaltools.api.tiles;

import com.invadermonky.survivaltools.api.blocks.AbstractBlockWaterPurifier;
import com.invadermonky.survivaltools.crafting.purifier.PurifierRecipe;
import com.invadermonky.survivaltools.crafting.purifier.PurifierRecipeRegistry;
import com.invadermonky.survivaltools.inventory.handler.PurifierStackHandler;
import com.invadermonky.survivaltools.network.PacketHandlerST;
import com.invadermonky.survivaltools.network.packets.PacketDrainPurifierTank;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractTileWaterPurifier extends AbstractTickableTileST implements IFluidHandler {
    public static final int TANK_CAPACITY = 4000;

    protected PurifierStackHandler stackHandler;
    protected FluidTank inputTank;
    protected FluidTank outputTank;
    protected PurifierRecipe recipe;
    protected int progress;
    protected int maxProgress;

    public AbstractTileWaterPurifier(int additionalSlots) {
        this.stackHandler = new PurifierStackHandler(this, additionalSlots);
        this.inputTank = new FluidTank(TANK_CAPACITY) {
            @Override
            public boolean canFillFluidType(FluidStack fluid) {
                return PurifierRecipeRegistry.isValidInputFluid(fluid);
            }

            @Override
            protected void onContentsChanged() {
                this.tile.markDirty();
            }
        };
        this.inputTank.setCanDrain(false);
        this.inputTank.setTileEntity(this);

        this.outputTank = new FluidTank(TANK_CAPACITY) {
            @Override
            protected void onContentsChanged() {
                this.tile.markDirty();
            }
        };
        this.outputTank.setCanFill(false);
        this.outputTank.setTileEntity(this);
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

            did |= this.transferInputSlotFluids();
            did |= this.transferOutputSlotFluids();
            did |= this.onTileUpdate();

            if (isRunning != this.isRunning()) {
                this.setEnabled(this.isRunning());
            }
        }

        if (did) {
            this.markDirty();
        }
    }

    protected boolean transferInputSlotFluids() {
        ItemStack toEmpty = this.stackHandler.HANDLER_INPUT_TANK_SLOT_IN.extractItem(0, 1, true);
        if(!toEmpty.isEmpty() && toEmpty.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null)) {
            FluidActionResult result = FluidUtil.tryEmptyContainer(toEmpty, this.inputTank, this.inputTank.getCapacity(), null, false);
            ItemStack emptied = result.getResult();
            if(result.isSuccess() && !result.getResult().isEmpty() && !ItemStack.areItemStacksEqual(toEmpty, result.getResult())) {
                ItemStack remaining = ItemHandlerHelper.insertItem(this.stackHandler.HANDLER_INPUT_TANK_SLOT_OUT, emptied, true);
                if(remaining.isEmpty()) {
                    toEmpty = this.stackHandler.HANDLER_INPUT_TANK_SLOT_IN.extractItem(0, 1, false);
                    result = FluidUtil.tryEmptyContainer(toEmpty, this.inputTank, this.inputTank.getCapacity(), null, true);
                    emptied = result.getResult();
                    ItemHandlerHelper.insertItem(this.stackHandler.HANDLER_INPUT_TANK_SLOT_OUT, emptied, false);
                    return true;
                }
            }
        }
        return false;
    }

    protected boolean transferOutputSlotFluids() {
        ItemStack toFill = this.stackHandler.HANDLER_OUTPUT_TANK_SLOT_IN.extractItem(0, 1, true);
        if(!toFill.isEmpty() && toFill.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null)) {
            FluidActionResult result = FluidUtil.tryFillContainer(toFill, this.outputTank, this.outputTank.getCapacity(), null, false);
            ItemStack filled = result.getResult();
            if(result.isSuccess() && !filled.isEmpty() && !ItemStack.areItemStacksEqual(toFill, filled)) {
                if(this.stackHandler.isFluidContainerFull(filled)) {
                    //If the container is filled, move to output slot
                    ItemStack remaining = ItemHandlerHelper.insertItem(this.stackHandler.HANDLER_OUTPUT_TANK_SLOT_OUT, filled, true);
                    if(remaining.isEmpty()) {
                        toFill = this.stackHandler.HANDLER_OUTPUT_TANK_SLOT_IN.extractItem(0, 1, false);
                        result = FluidUtil.tryFillContainer(toFill, this.outputTank, this.outputTank.getCapacity(), null, true);
                        filled = result.getResult();
                        ItemHandlerHelper.insertItem(this.stackHandler.HANDLER_OUTPUT_TANK_SLOT_OUT, filled, false);
                        return true;
                    }
                } else if(this.stackHandler.HANDLER_OUTPUT_TANK_SLOT_IN.getStackInSlot(0).getCount() == 1) {
                    //If the container is not filled and there is only one item, fill it and update the input slot
                    toFill = this.stackHandler.HANDLER_OUTPUT_TANK_SLOT_IN.extractItem(0, 1, false);
                    result = FluidUtil.tryFillContainer(toFill, this.outputTank, this.outputTank.getCapacity(), null, true);
                    filled = result.getResult();
                    ItemHandlerHelper.insertItem(this.stackHandler.HANDLER_OUTPUT_TANK_SLOT_IN, filled, false);
                    return true;
                }



            }
        }
        return false;
    }

    @Override
    public void readFromNBT(@NotNull NBTTagCompound compound) {
        super.readFromNBT(compound);
        this.inputTank.readFromNBT(compound.getCompoundTag("inputTank"));
        this.outputTank.readFromNBT(compound.getCompoundTag("outputTank"));
        this.stackHandler.deserializeNBT(compound.getCompoundTag("inventory"));
        this.progress = compound.getInteger("progress");
        this.maxProgress = compound.getInteger("maxProgress");
        this.readFromTileNBT(compound);
    }

    @Override
    public @NotNull NBTTagCompound writeToNBT(@NotNull NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setTag("inputTank", this.inputTank.writeToNBT(new NBTTagCompound()));
        compound.setTag("outputTank", this.outputTank.writeToNBT(new NBTTagCompound()));
        compound.setTag("inventory", this.stackHandler.serializeNBT());
        compound.setInteger("progress", this.progress);
        compound.setInteger("maxProgress", this.maxProgress);
        return this.writeToTileNBT(compound);
    }

    @Override
    public @Nullable ITextComponent getDisplayName() {
        return new TextComponentTranslation(this.getBlockType().getTranslationKey() + ".name");
    }

    @Override
    public boolean hasCapability(@NotNull Capability<?> capability, @Nullable EnumFacing facing) {
        if(this.hasTileCapability(capability, facing)) {
            return true;
        } else if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            if(facing == EnumFacing.UP || facing == EnumFacing.DOWN) {
                return true;
            } else {
                EnumFacing direction = this.world.getBlockState(this.pos).getValue(AbstractBlockWaterPurifier.FACING);
                return facing == direction.rotateY() || facing == direction.rotateYCCW();
            }
        } else {
            return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
        }
    }

    @Override
    public @Nullable <T> T getCapability(@NotNull Capability<T> capability, @Nullable EnumFacing facing) {
        T tileCap = this.getTileCapability(capability, facing);
        if (tileCap != null) {
            return tileCap;
        } else if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            if(facing == EnumFacing.UP) {
                return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(this.stackHandler.HANDLER_FILTER_SLOT);
            } else if(facing == EnumFacing.DOWN) {
                return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(this.stackHandler.HANDLER_OUTPUT_SLOTS);
            }
            EnumFacing direction = this.world.getBlockState(this.pos).getValue(AbstractBlockWaterPurifier.FACING);
            if(facing == direction.rotateY()) {
                return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(this.stackHandler.HANDLER_INPUT_TANK_SLOT_IN);
            } else if(facing == direction.rotateYCCW()) {
                return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(this.stackHandler.HANDLER_OUTPUT_TANK_SLOT_IN);
            }
        } else if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(this);
        }
        return super.getCapability(capability, facing);
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
        for (int i = 0; i < this.stackHandler.getSlots(); i++) {
            ItemStack stack = this.stackHandler.getStackInSlot(i);
            if (!stack.isEmpty())
                Block.spawnAsEntity(this.world, this.pos, this.stackHandler.extractItem(i, stack.getCount(), false));
        }
    }

    public FluidTank getInputFluidTank() {
        return this.inputTank;
    }

    public FluidTank getOutputFluidTank() {
        return this.outputTank;
    }

    public ItemStackHandler getItemHandler() {
        return this.stackHandler;
    }

    public ItemStack getFilterStack() {
        return this.stackHandler.HANDLER_FILTER_SLOT.getStackInSlot(0);
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

        ItemStack stack = this.stackHandler.HANDLER_FILTER_SLOT.extractItem(0, this.stackHandler.HANDLER_FILTER_SLOT.getSlotLimit(0), false);
        if (stack.getItem().hasContainerItem(stack) && stack.getCount() == 1) {
            stack = stack.getItem().getContainerItem(stack);
        } else {
            stack.shrink(1);
        }
        if (!stack.isEmpty()) {
            this.stackHandler.HANDLER_FILTER_SLOT.setStackInSlot(0, stack);
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
        if (state.getBlock() instanceof AbstractBlockWaterPurifier && state.getValue(AbstractBlockWaterPurifier.ENABLED) != enabled) {
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
