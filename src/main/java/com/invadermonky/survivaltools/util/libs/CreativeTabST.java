package com.invadermonky.survivaltools.util.libs;

import com.invadermonky.survivaltools.SurvivalTools;
import com.invadermonky.survivaltools.api.fluid.IPurifiedFluidContainerItem;
import com.invadermonky.survivaltools.registry.ModItemsST;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

public class CreativeTabST {
    public static final CreativeTabs TAB_ST = new CreativeTabs(SurvivalTools.MOD_ID) {
        @Override
        public ItemStack createIcon() {
            ItemStack canteen = new ItemStack(ModItemsST.canteen);
            ((IPurifiedFluidContainerItem) canteen.getItem()).setFluidFull(canteen);
            return canteen;
        }
    };
}
