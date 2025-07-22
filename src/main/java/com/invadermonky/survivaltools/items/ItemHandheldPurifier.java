package com.invadermonky.survivaltools.items;

import com.invadermonky.survivaltools.SurvivalTools;
import com.invadermonky.survivaltools.api.IAddition;
import com.invadermonky.survivaltools.api.SurvivalToolsAPI;
import com.invadermonky.survivaltools.crafting.handheldpurifier.HandheldPurifierRecipe;
import com.invadermonky.survivaltools.crafting.handheldpurifier.HandheldPurifierRecipeRegistry;
import com.invadermonky.survivaltools.util.helpers.StringHelper;
import com.invadermonky.survivaltools.util.libs.LibNames;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.registries.IForgeRegistry;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class ItemHandheldPurifier extends Item implements IAddition {
    public ItemHandheldPurifier() {
        this.setMaxStackSize(1);
        this.setMaxDamage(20);
        this.addPropertyOverride(new ResourceLocation(SurvivalTools.MOD_ID, "using"), (stack, worldIn, entity) -> entity != null &&
                entity.getActiveItemStack().getItem() instanceof ItemHandheldPurifier && entity.getItemInUseCount() > 0 ? 1 : 0);
    }

    @Override
    public @NotNull EnumActionResult onItemUse(@NotNull EntityPlayer player, @NotNull World worldIn, @NotNull BlockPos pos, @NotNull EnumHand hand, @NotNull EnumFacing facing, float hitX, float hitY, float hitZ) {
        IFluidHandler handler = FluidUtil.getFluidHandler(worldIn, pos, facing);
        if (handler != null) {
            player.setActiveHand(hand);
            return EnumActionResult.SUCCESS;
        }
        return super.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);
    }

    @Override
    public @NotNull ActionResult<ItemStack> onItemRightClick(@NotNull World world, EntityPlayer player, @NotNull EnumHand hand) {
        ItemStack mhStack = player.getHeldItemMainhand();
        ItemStack ohStack = player.getHeldItemOffhand();
        if (mhStack.getItem() instanceof ItemHandheldPurifier && (ohStack.getItem() == Items.GLASS_BOTTLE || FluidUtil.getFluidHandler(ohStack) != null)) {
            player.setActiveHand(EnumHand.MAIN_HAND);
            return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
        }
        return super.onItemRightClick(world, player, hand);
    }

    @Override
    public @NotNull ItemStack onItemUseFinish(@NotNull ItemStack stack, @NotNull World world, @NotNull EntityLivingBase entity) {
        if (entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entity;
            ItemStack mhStack = player.getHeldItemMainhand();
            ItemStack ohStack = player.getHeldItemOffhand();
            if (mhStack.getItem() instanceof ItemHandheldPurifier) {
                RayTraceResult rtr = this.rayTrace(world, player, true);
                HandheldPurifierRecipe recipe = this.drainFromBlock(world, player, rtr, false);
                if (recipe != null) {
                    if (ohStack.getItem() == Items.GLASS_BOTTLE) {
                        if (recipe.canFillBottles()) {
                            this.drainFromBlock(world, player, rtr, true);
                            this.handleFilledOffhand(player, mhStack, SurvivalToolsAPI.getPurifiedWaterBottleStack());
                            return mhStack;
                        }
                    } else {
                        ItemStack ohCopy = ohStack.copy();
                        ohCopy.setCount(1);
                        IFluidHandlerItem handler = FluidUtil.getFluidHandler(ohCopy);
                        FluidStack fillStack = SurvivalToolsAPI.getPurifiedWaterStack(recipe.getOutputAmount());
                        if (handler != null && handler.fill(fillStack, false) > 0) {
                            this.drainFromBlock(world, player, rtr, true);
                            handler.fill(fillStack, true);
                            this.handleFilledOffhand(player, mhStack, handler.getContainer());
                            return mhStack;
                        }
                    }
                }
            }
        }
        return super.onItemUseFinish(stack, world, entity);
    }

    @Override
    public @NotNull EnumAction getItemUseAction(@NotNull ItemStack stack) {
        return EnumAction.BOW;
    }

    @Override
    public int getMaxItemUseDuration(@NotNull ItemStack stack) {
        return 64;
    }

    @Override
    public void addInformation(@NotNull ItemStack stack, @Nullable World worldIn, List<String> tooltip, @NotNull ITooltipFlag flagIn) {
        tooltip.add(StringHelper.getTranslatedString(LibNames.HANDHELD_PURIFIER, "tooltip", "desc0"));
        tooltip.add(StringHelper.getTranslatedString(LibNames.HANDHELD_PURIFIER, "tooltip", "desc1"));
    }

    @Override
    public @NotNull EnumActionResult onItemUseFirst(@NotNull EntityPlayer player, @NotNull World world, @NotNull BlockPos pos, @NotNull EnumFacing side, float hitX, float hitY, float hitZ, @NotNull EnumHand hand) {
        IFluidHandler handler = FluidUtil.getFluidHandler(world, pos, side);
        if (handler != null) {
            player.setActiveHand(hand);
            return EnumActionResult.SUCCESS;
        }
        return super.onItemUseFirst(player, world, pos, side, hitX, hitY, hitZ, hand);
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return oldStack.getItem() != newStack.getItem() || slotChanged;
    }

    @Nullable
    public HandheldPurifierRecipe drainFromBlock(World world, EntityPlayer player, @Nullable RayTraceResult trace, boolean doDrain) {
        HandheldPurifierRecipe recipe = null;
        if (trace != null) {
            BlockPos pos = trace.getBlockPos();
            if (world.isBlockModifiable(player, pos) && player.canPlayerEdit(pos.offset(trace.sideHit), trace.sideHit, player.getHeldItemMainhand())) {
                IFluidHandler handler = FluidUtil.getFluidHandler(world, pos, trace.sideHit);
                if (handler != null) {
                    FluidStack handlerFluid = handler.getTankProperties()[0].getContents();
                    recipe = HandheldPurifierRecipeRegistry.getRecipe(handlerFluid);
                    if (recipe != null) {
                        FluidStack drained = handler.drain(new FluidStack(recipe.getInputFluid(), 1000), false);
                        if (drained != null && drained.amount >= 1000) {
                            handler.drain(new FluidStack(recipe.getInputFluid(), 1000), doDrain);
                        } else {
                            recipe = null;
                        }
                    }
                }
            }
        }
        return recipe;
    }

    public void handleFilledOffhand(EntityPlayer player, ItemStack mhStack, ItemStack filledStack) {
        if (!filledStack.isEmpty()) {
            ItemStack ohStack = player.getHeldItemOffhand();
            if (!player.isCreative()) {
                mhStack.damageItem(1, player);
            }
            ohStack.shrink(1);
            if (ohStack.isEmpty()) {
                player.setHeldItem(EnumHand.OFF_HAND, filledStack);
            } else if (!player.addItemStackToInventory(filledStack)) {
                player.dropItem(filledStack, true);
            }
        }
    }

    /*
     *  IAddition
     */

    @Override
    public void registerRecipe(IForgeRegistry<IRecipe> registry) {
        CraftingHelper.ShapedPrimer primer = CraftingHelper.parseShaped(
                "LFI", "IFI", "RWR",
                'L', Blocks.LEVER,
                'F', SurvivalToolsAPI.getWaterFilterStack(),
                'I', "ingotIron",
                'R', "dustRedstone",
                'W', "wool"
        );
        IRecipe recipe = new ShapedRecipes(this.getRegistryName().toString(), primer.width, primer.height, primer.input, new ItemStack(this));
        recipe.setRegistryName(this.getRegistryName());
        registry.register(recipe);
    }

    @Override
    public void registerModel(ModelRegistryEvent event) {
        ModelResourceLocation loc = new ModelResourceLocation(this.delegate.name(), "inventory");
        ModelLoader.setCustomModelResourceLocation(this, 0, loc);
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
