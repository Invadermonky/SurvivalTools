package com.invadermonky.survivaltools.compat.survivaltools.items;

import com.invadermonky.survivaltools.SurvivalTools;
import com.invadermonky.survivaltools.api.items.IItemAddition;
import com.invadermonky.survivaltools.util.libs.CreativeTabST;
import net.minecraft.item.Item;

public class ItemAddition extends Item implements IItemAddition {
    private final boolean isEnabled;

    public ItemAddition(String unlocName, boolean isEnabled) {
        this.setRegistryName(SurvivalTools.MOD_ID, unlocName);
        this.setTranslationKey(this.getRegistryName().toString());
        this.setCreativeTab(CreativeTabST.TAB_ST);
        this.isEnabled = isEnabled;
    }

    @Override
    public boolean isEnabled() {
        return this.isEnabled;
    }
}
