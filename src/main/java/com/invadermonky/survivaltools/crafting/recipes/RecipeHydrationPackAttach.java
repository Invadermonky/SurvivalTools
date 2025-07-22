package com.invadermonky.survivaltools.crafting.recipes;

import com.invadermonky.survivaltools.SurvivalTools;
import com.invadermonky.survivaltools.items.ItemHydrationPack;
import com.invadermonky.survivaltools.util.libs.LibNames;
import net.minecraft.entity.EntityLiving;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.jetbrains.annotations.NotNull;

public class RecipeHydrationPackAttach extends IForgeRegistryEntry.Impl<IRecipe> implements IRecipe {
    public RecipeHydrationPackAttach() {
        this.setRegistryName(new ResourceLocation(SurvivalTools.MOD_ID, LibNames.RECIPE_HYDRATION_PACK_ATTACH));
    }

    @Override
    public boolean matches(InventoryCrafting inv, @NotNull World worldIn) {
        boolean foundChest = false;
        boolean foundHydrationPack = false;

        for (int i = 0; i < inv.getSizeInventory(); i++) {
            ItemStack stack = inv.getStackInSlot(i);
            if (!stack.isEmpty()) {
                if (EntityLiving.getSlotForItemStack(stack) == EntityEquipmentSlot.CHEST && !ItemHydrationPack.hasHydrationPackAttached(stack) && !foundChest) {
                    foundChest = true;
                } else if (stack.getItem() instanceof ItemHydrationPack && !foundHydrationPack) {
                    foundHydrationPack = true;
                } else {
                    return false;
                }
            }
        }
        return foundChest && foundHydrationPack;
    }

    @Override
    public @NotNull ItemStack getCraftingResult(InventoryCrafting inv) {
        ItemStack chestStack = ItemStack.EMPTY;
        ItemStack packStack = ItemStack.EMPTY;

        for (int i = 0; i < inv.getSizeInventory(); i++) {
            ItemStack stack = inv.getStackInSlot(i);
            if (!stack.isEmpty()) {
                if (EntityLiving.getSlotForItemStack(stack) == EntityEquipmentSlot.CHEST) {
                    chestStack = stack.copy();
                } else if (stack.getItem() instanceof ItemHydrationPack) {
                    packStack = stack.copy();
                }
            }
        }

        if (!chestStack.isEmpty() && !packStack.isEmpty()) {
            ItemHydrationPack.attachHydrationPack(chestStack, packStack);
        }

        return chestStack;
    }

    @Override
    public boolean canFit(int width, int height) {
        return width * height > 1;
    }

    @Override
    public @NotNull ItemStack getRecipeOutput() {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean isDynamic() {
        return true;
    }
}
