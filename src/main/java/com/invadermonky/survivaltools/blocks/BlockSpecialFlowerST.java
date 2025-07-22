package com.invadermonky.survivaltools.blocks;

import com.invadermonky.survivaltools.api.IAddition;
import com.invadermonky.survivaltools.api.IProxy;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.registries.IForgeRegistry;
import vazkii.botania.api.subtile.SubTileEntity;
import vazkii.botania.common.block.BlockSpecialFlower;
import vazkii.botania.common.block.ModBlocks;
import vazkii.botania.common.item.block.ItemBlockSpecialFlower;

import javax.annotation.Nonnull;

public class BlockSpecialFlowerST<T extends SubTileEntity & IAddition & IProxy> extends BlockSpecialFlower implements IAddition, IProxy {
    private final T subTile;
    private final String name;

    public BlockSpecialFlowerST(T subTile, String subTileName) {
        this.subTile = subTile;
        this.name = subTileName;
    }

    public String getSubTileName() {
        return this.name;
    }

    @Override
    public void getSubBlocks(CreativeTabs tab, @Nonnull NonNullList<ItemStack> stacks) {
        if (this.isEnabled()) {
            stacks.add(ItemBlockSpecialFlower.ofType(this.name));
            stacks.add(ItemBlockSpecialFlower.ofType(new ItemStack(ModBlocks.floatingSpecialFlower), this.name));
        }
    }

    /*
     *  IAddition
     */
    @Override
    public void preInit() {
        this.subTile.preInit();
    }

    @Override
    public void init() {
        this.subTile.init();
    }

    @Override
    public void postInit() {
        this.subTile.postInit();
    }

    @Override
    public void registerRecipe(IForgeRegistry<IRecipe> registry) {
        this.subTile.registerRecipe(registry);
    }

    @Override
    public void registerModel(ModelRegistryEvent event) {
        this.subTile.registerModel(event);
    }

    @Override
    public boolean isEnabled() {
        return this.subTile.isEnabled();
    }
}
