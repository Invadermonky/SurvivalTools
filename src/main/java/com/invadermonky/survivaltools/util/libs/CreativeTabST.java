package com.invadermonky.survivaltools.util.libs;

import com.invadermonky.survivaltools.SurvivalTools;
import com.invadermonky.survivaltools.api.fluid.IPurifiedFluidContainerItem;
import com.invadermonky.survivaltools.registry.ModItemsST;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class CreativeTabST {
    public static final CreativeTabs TAB_ST = new CreativeTabs(SurvivalTools.MOD_ID) {
        @Override
        public @NotNull ItemStack createIcon() {
            ItemStack canteen = new ItemStack(ModItemsST.CANTEEN);
            ((IPurifiedFluidContainerItem) canteen.getItem()).setFluidFull(canteen);
            return canteen;
        }
    };
}
