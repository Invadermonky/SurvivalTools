package com.invadermonky.survivaltools.blocks;

import com.invadermonky.survivaltools.SurvivalTools;
import com.invadermonky.survivaltools.api.IAddition;
import com.invadermonky.survivaltools.api.SurvivalToolsAPI;
import com.invadermonky.survivaltools.blocks.base.AbstractBlockWaterPurifier;
import com.invadermonky.survivaltools.config.ConfigHandlerST;
import com.invadermonky.survivaltools.tile.TilePoweredPurifier;
import com.invadermonky.survivaltools.util.ChatUtils;
import com.invadermonky.survivaltools.util.helpers.StringHelper;
import com.invadermonky.survivaltools.util.libs.LibTags;
import com.invadermonky.survivaltools.util.libs.ModGui;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.registries.IForgeRegistry;
import org.jetbrains.annotations.NotNull;

public class BlockPoweredPurifier extends AbstractBlockWaterPurifier<TilePoweredPurifier> implements IAddition {
    public BlockPoweredPurifier() {
        super(Material.IRON, TilePoweredPurifier.class);
    }

    @Override
    public boolean onPurifierActivated(@NotNull World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack heldItem = playerIn.getHeldItem(hand);
        if (heldItem.getItem() == Item.getItemFromBlock(Blocks.REDSTONE_TORCH)) {
            TileEntity tile = worldIn.getTileEntity(pos);
            if (tile instanceof TilePoweredPurifier) {
                TilePoweredPurifier purifier = (TilePoweredPurifier) tile;
                if (!playerIn.isSneaking()) {
                    purifier.nextRedstoneMode();
                } else {
                    purifier.previousRedstoneMode();
                }
                ChatUtils.sendNoSpam(playerIn, new TextComponentTranslation(StringHelper.getTranslationKey(LibTags.TAG_REDSTONE, "chat", purifier.getRedstoneMode().toString())));
                worldIn.scheduleUpdate(pos, this, 0);
                tile.markDirty();
            }
            return true;
        }
        playerIn.openGui(SurvivalTools.instance, ModGui.POWERED_PURIFIER.ordinal(), worldIn, pos.getX(), pos.getY(), pos.getZ());
        return true;
    }

    @Override
    public void registerRecipe(IForgeRegistry<IRecipe> registry) {
        CraftingHelper.ShapedPrimer primer = CraftingHelper.parseShaped(
                "IWI", "CFC", "IRI",
                'I', "ingotIron",
                'W', SurvivalToolsAPI.getWaterFilterStack(),
                'C', Items.CAULDRON,
                'F', Blocks.FURNACE,
                'R', "blockRedstone"
        );
        IRecipe recipe = new ShapedRecipes(this.getRegistryName().toString(), primer.width, primer.height, primer.input, new ItemStack(this));
        recipe.setRegistryName(this.getRegistryName());
        registry.register(recipe);
    }

    @Override
    public void registerModel(ModelRegistryEvent event) {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(this.getRegistryName(), "inventory"));
    }

    @Override
    public boolean isEnabled() {
        return ConfigHandlerST.machines.powered_purifier.enable && SurvivalToolsAPI.isThirstFeatureEnabled();
    }
}
