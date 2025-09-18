package com.invadermonky.survivaltools.api;

import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.registries.IForgeRegistry;

/**
 * Interface used to help register
 */
public interface IAddition {
    /**
     * Registers a recipe during the IRecipe registry event. Always try to use this over proxy recipe
     * registries to avoid incompatibilities.
     */
    default void registerRecipe(IForgeRegistry<IRecipe> registry) {}

    /**
     * Registers the model for the item or block. This method must be called to register the model.
     */
    default void registerModel(ModelRegistryEvent event) {}

    /**
     * A helper method for registering ore dictionary values. This fires right after the item registry event completes.
     */
    default void registerOreDicts() {}

    /**
     * Returns true if this item is enabled. Used for additions with configurable Enable/Disable or if they
     * are reliant on another feature.
     */
    boolean isEnabled();
}
