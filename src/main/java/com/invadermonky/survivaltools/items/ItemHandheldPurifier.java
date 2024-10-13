package com.invadermonky.survivaltools.items;

import com.invadermonky.survivaltools.SurvivalTools;
import com.invadermonky.survivaltools.api.IAddition;
import com.invadermonky.survivaltools.util.helpers.StringHelper;
import com.invadermonky.survivaltools.util.helpers.SurvivalHelper;
import com.invadermonky.survivaltools.util.helpers.SurvivalItemHelper;
import com.invadermonky.survivaltools.util.libs.LibNames;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.I18n;
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
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.registries.IForgeRegistry;

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
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return oldStack.getItem() != newStack.getItem() || slotChanged;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack mhStack = player.getHeldItemMainhand();
        ItemStack ohStack = player.getHeldItemOffhand();
        if(mhStack.getItem() instanceof ItemHandheldPurifier && this.canFillOffhand(ohStack)) {
            player.setActiveHand(EnumHand.MAIN_HAND);
            return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
        }
        return super.onItemRightClick(world, player, hand);
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        IFluidHandler handler = FluidUtil.getFluidHandler(worldIn, pos, facing);
        if(handler != null) {
            player.setActiveHand(hand);
            return EnumActionResult.SUCCESS;
        }
        return super.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);
    }

    @Override
    public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
        IFluidHandler handler = FluidUtil.getFluidHandler(world, pos, side);
        if(handler != null) {
            player.setActiveHand(hand);
            return EnumActionResult.SUCCESS;
        }
        return super.onItemUseFirst(player, world, pos, side, hitX, hitY, hitZ, hand);
    }

    @Override
    public ItemStack onItemUseFinish(ItemStack stack, World world, EntityLivingBase entity) {
        if(entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entity;
            ItemStack mhStack = player.getHeldItemMainhand();
            ItemStack ohStack = player.getHeldItemOffhand();
            if(mhStack.getItem() instanceof ItemHandheldPurifier) {
                RayTraceResult rtr = this.rayTrace(world, player, true);
                FluidStack drainedStack = this.drainFromBlock(world, player, rtr, 1000, false);
                if(drainedStack != null) {
                    if(ohStack.getItem() == Items.GLASS_BOTTLE && drainedStack.amount == 1000) {
                        this.drainFromBlock(world, player, rtr, 1000, true);
                        this.handleFilledOffhand(player, mhStack, SurvivalItemHelper.getPurifiedWaterBottleStack());
                        return mhStack;
                    } else {
                        ItemStack ohCopy = ohStack.copy();
                        ohCopy.setCount(1);
                        IFluidHandlerItem handler = FluidUtil.getFluidHandler(ohCopy);
                        if(handler != null) {
                            int fillAmount = handler.fill(drainedStack, false);
                            if(fillAmount > 0) {
                                this.drainFromBlock(world, player, rtr, fillAmount, true);
                                handler.fill(drainedStack, true);
                                this.handleFilledOffhand(player, mhStack, handler.getContainer());
                                return mhStack;
                            }
                        }
                    }
                }
            }
        }
        return super.onItemUseFinish(stack, world, entity);
    }

    @Nullable
    public FluidStack drainFromBlock(World world, EntityPlayer player, @Nullable RayTraceResult trace, int drainAmount, boolean doDrain) {
        FluidStack drainedStack = null;
        if(trace != null) {
            BlockPos pos = trace.getBlockPos();
            if (world.isBlockModifiable(player, pos) && player.canPlayerEdit(pos.offset(trace.sideHit), trace.sideHit, player.getHeldItemMainhand())) {
                IFluidHandler handler = FluidUtil.getFluidHandler(world, pos, trace.sideHit);
                if (handler != null) {
                    if (handler.drain(new FluidStack(FluidRegistry.WATER, drainAmount), false) != null) {
                        drainedStack = handler.drain(new FluidStack(FluidRegistry.WATER, drainAmount), doDrain);
                    } else if (drainAmount != 1000 && handler.drain(new FluidStack(FluidRegistry.WATER, 1000), false) != null) {
                        drainedStack = handler.drain(new FluidStack(FluidRegistry.WATER, 1000), doDrain);
                    }
                }
            }
        }
        return drainedStack != null ? new FluidStack(SurvivalHelper.getPurifiedWater(), drainedStack.amount) : null;
    }

    public boolean canFillOffhand(ItemStack offhandStack) {
        IFluidHandlerItem handler = FluidUtil.getFluidHandler(offhandStack);
        return offhandStack.getItem() == Items.GLASS_BOTTLE || (handler != null && handler.fill(new FluidStack(SurvivalHelper.getPurifiedWater(), 1000), false) > 0);
    }

    public void handleFilledOffhand(EntityPlayer player, ItemStack mhStack, ItemStack filledStack) {
        if(!filledStack.isEmpty()) {
            ItemStack ohStack = player.getHeldItemOffhand();
            if(!player.isCreative()) {
                mhStack.damageItem(1, player);
            }
            ohStack.shrink(1);
            if(ohStack.isEmpty()) {
                player.setHeldItem(EnumHand.OFF_HAND, filledStack);
            } else if(!player.addItemStackToInventory(filledStack)) {
                player.dropItem(filledStack, true);
            }
        }
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 64;
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.BOW;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(I18n.format(StringHelper.getTranslationKey(LibNames.HANDHELD_PURIFIER, "tooltip", "desc0")));
        tooltip.add(I18n.format(StringHelper.getTranslationKey(LibNames.HANDHELD_PURIFIER, "tooltip", "desc1")));
    }

    /*
     *  IAddition
     */

    @Override
    public void registerRecipe(IForgeRegistry<IRecipe> registry) {
        CraftingHelper.ShapedPrimer primer = CraftingHelper.parseShaped(
                "LFI", "IFI", "RWR",
                'L', Blocks.LEVER,
                'F', SurvivalItemHelper.getCharcoalFilterStack(),
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
