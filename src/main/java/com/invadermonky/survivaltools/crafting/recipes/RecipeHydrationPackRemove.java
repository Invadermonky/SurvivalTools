package com.invadermonky.survivaltools.crafting.recipes;

import com.invadermonky.survivaltools.SurvivalTools;
import com.invadermonky.survivaltools.items.ItemHydrationPack;
import com.invadermonky.survivaltools.util.libs.LibNames;
import net.minecraft.entity.EntityLiving;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class RecipeHydrationPackRemove extends IForgeRegistryEntry.Impl<IRecipe> implements IRecipe {
    public RecipeHydrationPackRemove() {
        this.setRegistryName(new ResourceLocation(SurvivalTools.MOD_ID, LibNames.RECIPE_HYDRATION_PACK_REMOVE));
    }

    @Override
    public boolean matches(InventoryCrafting inv, World worldIn) {
        boolean found = false;

        for(int i = 0; i < inv.getSizeInventory(); i++) {
            ItemStack stack = inv.getStackInSlot(i);
            if(!stack.isEmpty()) {
                if(EntityLiving.getSlotForItemStack(stack) == EntityEquipmentSlot.CHEST && ItemHydrationPack.hasHydrationPackAttached(stack) && !found) {
                    found = true;
                } else {
                    return false;
                }
            }
        }

        return found;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv) {
        ItemStack chestStack = ItemStack.EMPTY;

        for(int i = 0; i < inv.getSizeInventory(); i++) {
            ItemStack checkStack = inv.getStackInSlot(i);
            if(!checkStack.isEmpty()) {
                if(EntityLiving.getSlotForItemStack(checkStack) == EntityEquipmentSlot.CHEST && ItemHydrationPack.hasHydrationPackAttached(checkStack)) {
                    chestStack = checkStack.copy();
                }
            }
        }
        if(!chestStack.isEmpty()) {
            ItemHydrationPack.removeHydrationPack(chestStack);
        }
        return chestStack;
    }

    @Override
    public boolean canFit(int width, int height) {
        return width * height >= 1;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return ItemStack.EMPTY;
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) {
        NonNullList<ItemStack> rev = NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);
        for(int i = 0; i < rev.size(); i++) {
            ItemStack stack = inv.getStackInSlot(i);
            if(EntityLiving.getSlotForItemStack(stack) == EntityEquipmentSlot.CHEST && ItemHydrationPack.hasHydrationPackAttached(stack)) {
                rev.set(i, ItemHydrationPack.getHydrationPack(stack).copy());
                break;
            }
        }
        return rev;
    }

    @Override
    public boolean isDynamic() {
        return true;
    }
}
