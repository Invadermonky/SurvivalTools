package com.invadermonky.survivaltools.inventory.handler;

import com.invadermonky.survivaltools.crafting.purifier.PurifierRecipeRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RangedWrapper;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

public class PurifierStackHandler extends ItemStackHandler {
    /*
        Slots:
        0 - Filter Ingredient slot
        1 - Input Tank insert slot
        2 - Output Tank insert slot
        3 - Input Tank extract slot
        4 - Output Tank extract slot
        5+ - Additional Slots
     */
    public static final int SLOT_FILTER_INGREDIENT = 0;
    public static final int SLOT_INPUT_TANK_IN = 1;
    public static final int SLOT_OUTPUT_TANK_IN = 2;
    public static final int SLOT_INPUT_TANK_OUT = 3;
    public static final int SLOT_OUTPUT_TANK_OUT = 4;

    private final TileEntity tile;
    private final int size;
    public final RangedWrapper HANDLER_FILTER_SLOT;
    public final RangedWrapper HANDLER_INPUT_TANK_SLOT_IN;
    public final RangedWrapper HANDLER_OUTPUT_TANK_SLOT_IN;
    public final RangedWrapper HANDLER_INPUT_TANK_SLOT_OUT;
    public final RangedWrapper HANDLER_OUTPUT_TANK_SLOT_OUT;
    public final RangedWrapper HANDLER_OUTPUT_SLOTS;
    private final Map<Integer, Predicate<ItemStack>> SLOT_PREDICATES;


    public PurifierStackHandler(TileEntity tile, int extraSlots) {
        super(5 + extraSlots);
        this.tile = tile;
        this.size = 5 + extraSlots;
        this.HANDLER_FILTER_SLOT = new RangedWrapper(this, SLOT_FILTER_INGREDIENT, SLOT_FILTER_INGREDIENT + 1);
        this.HANDLER_INPUT_TANK_SLOT_IN = new RangedWrapper(this, SLOT_INPUT_TANK_IN, SLOT_INPUT_TANK_IN + 1);
        this.HANDLER_INPUT_TANK_SLOT_OUT = new RangedWrapper(this, SLOT_INPUT_TANK_OUT, SLOT_INPUT_TANK_OUT + 1);
        this.HANDLER_OUTPUT_TANK_SLOT_IN = new RangedWrapper(this, SLOT_OUTPUT_TANK_IN, SLOT_OUTPUT_TANK_IN + 1);
        this.HANDLER_OUTPUT_TANK_SLOT_OUT = new RangedWrapper(this, SLOT_OUTPUT_TANK_OUT, SLOT_OUTPUT_TANK_OUT + 1);
        this.HANDLER_OUTPUT_SLOTS = new RangedWrapper(this, 3, 5);
        this.SLOT_PREDICATES = new HashMap<>(this.getSlots());
        this.setSlotPredicate(SLOT_FILTER_INGREDIENT, PurifierRecipeRegistry::isValidFilterItem);
        this.setSlotPredicate(SLOT_INPUT_TANK_IN, PurifierStackHandler::isValidInputTankItem);
        this.setSlotPredicate(SLOT_OUTPUT_TANK_IN, PurifierStackHandler::isValidOutputTankItem);
    }

    public PurifierStackHandler setSlotPredicate(int slot, Predicate<ItemStack> predicate) {
        this.validateSlotIndex(slot);
        this.SLOT_PREDICATES.put(slot, predicate);
        return this;
    }

    @Override
    public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        return this.isItemValid(slot, stack) ? super.insertItem(slot, stack, simulate) : stack;
    }

    @Override
    protected int getStackLimit(int slot, @NotNull ItemStack stack) {
        if(slot == SLOT_FILTER_INGREDIENT) {
            //Filter items can stack to 64 unless it is a container item.
            return stack.getItem().hasContainerItem(stack) ? 1 : super.getStackLimit(slot, stack);
        }
        return super.getStackLimit(slot, stack);
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        return this.SLOT_PREDICATES.containsKey(slot) ? this.SLOT_PREDICATES.get(slot).test(stack) : super.isItemValid(slot, stack);
    }

    @Override
    protected void onContentsChanged(int slot) {
        this.tile.markDirty();
    }

    @Override
    protected void onLoad() {
        //Because the normal ItemStackHandler doesn't account for possible changes to the number of slots in an
        // inventory, the length of the `stacks` list needs to be adjusted manually.
        //Translation: Why the hell did forge hardcode the inventory size?
        if(this.stacks.size() < this.size) {
            NonNullList<ItemStack> newStacks = NonNullList.withSize(this.size, ItemStack.EMPTY);
            for(int i = 0; i < this.stacks.size(); i++) {
                newStacks.set(i, this.stacks.get(i).copy());
            }
            this.stacks = newStacks;
        }
    }

    public static boolean isValidInputTankItem(ItemStack stack) {
        IFluidHandlerItem handler = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
        if(handler != null) {
            for(IFluidTankProperties props : handler.getTankProperties()) {
                if(props.canDrain() && PurifierRecipeRegistry.isValidInputFluid(props.getContents())) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isValidOutputTankItem(ItemStack stack) {
        IFluidHandlerItem handler = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
        if(handler != null) {
            for (IFluidTankProperties props : handler.getTankProperties()) {
                if(props.canFill() && props.getContents() == null || (PurifierRecipeRegistry.isValidOutputFluid(props.getContents()) && props.getContents().amount < props.getCapacity())) {
                    return true;
                }
            }
        }
        return false;
    }


    public boolean isFluidContainerFull(ItemStack stack) {
        if(stack.isEmpty())
            return false;

        IFluidHandlerItem handler = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
        if (handler != null) {
            for(IFluidTankProperties props : handler.getTankProperties()) {
                if(props.canFill() && props.getContents() != null && props.getContents().amount >= props.getCapacity()) {
                    return true;
                }
            }
        }
        return false;
    }
}
