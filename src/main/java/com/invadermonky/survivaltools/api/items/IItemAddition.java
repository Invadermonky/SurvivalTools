package com.invadermonky.survivaltools.api.items;

import com.invadermonky.survivaltools.api.IAddition;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.registries.IForgeRegistry;

public interface IItemAddition extends IAddition {
    /**
     * Registers an item from within the item or block class. This must be called to register the item.
     */
    default void registerItem(IForgeRegistry<Item> registry) {
        if(this instanceof Item) {
            registry.register((Item) this);
        }
    }

    @Override
    default void registerModel(ModelRegistryEvent event) {
        if(this instanceof Item && ((Item) this).getRegistryName() != null) {
            ModelResourceLocation loc = new ModelResourceLocation(((Item) this).getRegistryName(), "inventory");
            ModelLoader.setCustomModelResourceLocation((Item) this, 0, loc);
        }
    }
}
