package com.invadermonky.survivaltools.registry;

import com.invadermonky.survivaltools.SurvivalTools;
import com.invadermonky.survivaltools.api.IAddition;
import com.invadermonky.survivaltools.crafting.recipes.RecipeFillPurifiedContainer;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

@Mod.EventBusSubscriber(modid = SurvivalTools.MOD_ID)
public class ModRecipesST {

    @SubscribeEvent
    public static void registerRecipes(RegistryEvent.Register<IRecipe> event) {
        IForgeRegistry<IRecipe> registry = event.getRegistry();

        registry.register(new RecipeFillPurifiedContainer());

        ModItemsST.allItems.forEach(item -> {
            if(item instanceof IAddition) {
                ((IAddition) item).registerRecipe(registry);
            }
        });

        ModBlocksST.allBlocks.keySet().forEach(block -> {
            if(block instanceof IAddition) {
                ((IAddition) block).registerRecipe(registry);
            }
        });
    }
}
