package com.invadermonky.survivaltools.crafting.recipes;

import com.invadermonky.survivaltools.SurvivalTools;
import com.invadermonky.survivaltools.api.fluid.IPurifiedFluidContainerItem;
import com.invadermonky.survivaltools.util.helpers.SurvivalHelper;
import com.invadermonky.survivaltools.util.helpers.SurvivalItemHelper;
import com.invadermonky.survivaltools.util.libs.LibNames;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.registries.IForgeRegistryEntry;

import java.util.HashMap;
import java.util.Map;

public class RecipeFillPurifiedContainer extends IForgeRegistryEntry.Impl<IRecipe> implements IRecipe {
    private Map<Integer, ItemStack> returnItems;

    public RecipeFillPurifiedContainer() {
        this.setRegistryName(new ResourceLocation(SurvivalTools.MOD_ID, LibNames.RECIPE_FILL_PURIFIED_CONTAINER));
    }

    @Override
    public boolean matches(InventoryCrafting inv, World worldIn) {
        boolean foundPurifiedFluidContainer = false;
        boolean foundFillItem = false;

        for(int i = 0; i < inv.getSizeInventory(); i++) {
            ItemStack stack = inv.getStackInSlot(i);
            if(!stack.isEmpty()) {
                if(stack.getItem() instanceof IPurifiedFluidContainerItem && !foundPurifiedFluidContainer) {
                    foundPurifiedFluidContainer = true;
                } else {
                    IFluidHandlerItem handler = FluidUtil.getFluidHandler(stack);
                    if(handler != null) {
                        for(IFluidTankProperties props : handler.getTankProperties()) {
                            FluidStack fluidStack = props.getContents();
                            if(fluidStack != null && fluidStack.getFluid() == SurvivalHelper.getPurifiedWater()) {
                                foundFillItem = true;
                                break;
                            }
                        }
                    } else if(SurvivalItemHelper.isPurifiedWaterBottle(stack)) {
                        foundFillItem = true;
                    } else  {
                        return false;
                    }
                }
            }
        }
        return foundPurifiedFluidContainer && foundFillItem;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv) {
        this.returnItems = new HashMap<>(inv.getSizeInventory());
        ItemStack purifiedFluidContainer = ItemStack.EMPTY;
        boolean foundPurifiedFluidContainer = false;
        /** slotId : [stack, isBottle] */
        Map<Integer, Tuple<ItemStack, Boolean>> fillItems = new HashMap<>(inv.getSizeInventory());

        for(int i = 0; i < inv.getSizeInventory(); i++) {
            ItemStack checkStack = inv.getStackInSlot(i);
            if(checkStack.getItem() instanceof IPurifiedFluidContainerItem && !foundPurifiedFluidContainer) {
                purifiedFluidContainer = checkStack.copy();
                foundPurifiedFluidContainer = true;
            } else if(SurvivalItemHelper.isPurifiedWaterBottle(checkStack)) {
                fillItems.put(i, new Tuple<>(checkStack.copy(), true));
            } else if(FluidUtil.getFluidHandler(checkStack) != null) {
                fillItems.put(i, new Tuple<>(checkStack.copy(), false));
            }
        }

        if(!purifiedFluidContainer.isEmpty()) {
            IPurifiedFluidContainerItem fluidContainer = (IPurifiedFluidContainerItem) purifiedFluidContainer.getItem();
            int packMissing = fluidContainer.getMaxFluidCapacity(purifiedFluidContainer) - fluidContainer.getFluidAmountStored(purifiedFluidContainer);
            int fillAmount = 0;
            for(Map.Entry<Integer,Tuple<ItemStack,Boolean>> entry : fillItems.entrySet()) {
                ItemStack containerStack = entry.getValue().getFirst();
                if(entry.getValue().getSecond()) {
                    fillAmount += 250;
                    this.returnItems.put(entry.getKey(), new ItemStack(Items.GLASS_BOTTLE));
                } else {
                    IFluidHandlerItem handler = FluidUtil.getFluidHandler(containerStack);
                    FluidStack fluidStack = handler.drain(new FluidStack(SurvivalHelper.getPurifiedWater(), packMissing - fillAmount), true);
                    if(fluidStack != null) {
                        this.returnItems.put(entry.getKey(), handler.getContainer());
                        fillAmount += fluidStack.amount;
                    } else {
                        this.returnItems.put(entry.getKey(), containerStack.copy());
                    }
                }
            }
            fluidContainer.fill(purifiedFluidContainer, new FluidStack(SurvivalHelper.getPurifiedWater(), fillAmount), true);
        }
        return purifiedFluidContainer;
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) {
        NonNullList<ItemStack> ret = NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);
        for(int i = 0; i < ret.size(); i++) {
            if(this.returnItems.containsKey(i)) {
                ret.set(i, this.returnItems.get(i).copy());
            }
        }
        return ret;
    }

    @Override
    public boolean canFit(int width, int height) {
        return width > 1 || height > 1;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean isDynamic() {
        return true;
    }
}
