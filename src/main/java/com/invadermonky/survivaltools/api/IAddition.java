package com.invadermonky.survivaltools.api;

import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;

public interface IAddition {
    /** Adding recipes during the IRecipe forge registry event. */
    default void registerRecipe(IForgeRegistry<IRecipe> registry) {}

    /** Registering models during the ModelRegistryEvent. Return false if the model is not registered during this action. */
    @SideOnly(Side.CLIENT)
    void registerModel(ModelRegistryEvent event);

    /** Configuration toggle for this feature. */
    boolean isEnabled();
}
