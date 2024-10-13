package com.invadermonky.survivaltools.items;

import com.invadermonky.survivaltools.api.IAddition;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;

public class ItemAddition extends Item implements IAddition {
    protected boolean enabled;

    public ItemAddition(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public void registerModel(ModelRegistryEvent event) {
        ModelResourceLocation loc = new ModelResourceLocation(this.delegate.name(), "inventory");
        ModelLoader.setCustomModelResourceLocation(this, 0, loc);
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }
}
