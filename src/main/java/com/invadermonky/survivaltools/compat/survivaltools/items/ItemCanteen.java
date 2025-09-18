package com.invadermonky.survivaltools.compat.survivaltools.items;

import com.invadermonky.survivaltools.SurvivalTools;
import com.invadermonky.survivaltools.api.SurvivalToolsAPI;
import com.invadermonky.survivaltools.api.fluid.FluidContainerItemWrapper;
import com.invadermonky.survivaltools.api.fluid.IPurifiedFluidContainerItem;
import com.invadermonky.survivaltools.api.items.IItemAddition;
import com.invadermonky.survivaltools.config.ConfigHandlerST;
import com.invadermonky.survivaltools.util.libs.CreativeTabST;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class ItemCanteen extends Item implements IPurifiedFluidContainerItem, IItemAddition {
    private int capacity;
    private int fluidCost;
    private int restoredThirst;
    private float restoredHydration;

    public ItemCanteen(String unlocName, int maxCapacity) {
        this.setRegistryName(SurvivalTools.MOD_ID, unlocName);
        this.setTranslationKey(this.getRegistryName().toString());
        this.setCreativeTab(CreativeTabST.TAB_ST);
        this.setMaxStackSize(1);
        this.setMaxFluidCapacity(maxCapacity);
        this.setFluidCost(ConfigHandlerST.tools.canteens.fluidCost);
        this.setRestoredThirst(ConfigHandlerST.tools.canteens.restoredThirst);
        this.setRestoredHydration((float) ConfigHandlerST.tools.canteens.restoredHydration);
    }

    public ItemCanteen setMaxFluidCapacity(int maxFluidCapacity) {
        this.capacity = maxFluidCapacity;
        return this;
    }

    @Override
    public int getMaxFluidCapacity(ItemStack stack) {
        return this.capacity;
    }

    public int getFluidCost() {
        return this.fluidCost;
    }

    public ItemCanteen setFluidCost(int fluidCost) {
        this.fluidCost = fluidCost;
        return this;
    }

    public int getRestoredThirst() {
        return this.restoredThirst;
    }

    public ItemCanteen setRestoredThirst(int restoredThirst) {
        this.restoredThirst = restoredThirst;
        return this;
    }

    public float getRestoredHydration() {
        return this.restoredHydration;
    }

    public ItemCanteen setRestoredHydration(float restoredHydration) {
        this.restoredHydration = restoredHydration;
        return this;
    }

    /*
     *  Item
     */

    @Override
    public @NotNull ActionResult<ItemStack> onItemRightClick(@NotNull World world, EntityPlayer player, @NotNull EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        int fluidAmount = this.getFluidAmountStored(stack);
        int fluidMissing = this.getMaxFluidCapacity(stack) - fluidAmount;
        if (fluidMissing > 0) {
            RayTraceResult rtr = this.rayTrace(world, player, true);
            if (rtr != null && rtr.typeOfHit == RayTraceResult.Type.BLOCK) {
                BlockPos tracePos = rtr.getBlockPos();
                if (world.isBlockModifiable(player, tracePos) && player.canPlayerEdit(tracePos, rtr.sideHit, stack)) {
                    if (FluidUtil.interactWithFluidHandler(player, hand, world, tracePos, rtr.sideHit)) {
                        return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
                    } else {
                        return new ActionResult<>(EnumActionResult.PASS, stack);
                    }
                }
            }
        }

        if (this.getFluidAmountStored(stack) > 0) {
            player.setActiveHand(hand);
            return new ActionResult<>(EnumActionResult.SUCCESS, stack);
        } else {
            return new ActionResult<>(EnumActionResult.PASS, stack);
        }
    }

    @Override
    public @NotNull ItemStack onItemUseFinish(@NotNull ItemStack stack, World world, @NotNull EntityLivingBase entity) {
        if (!world.isRemote && entity instanceof EntityPlayer) {
            if (this.getFluidAmountStored(stack) <= 0) {
                return stack;
            } else {
                EntityPlayer player = (EntityPlayer) entity;
                FluidStack drainFluid = this.drain(stack, this.getFluidCost(), true);
                if (drainFluid != null) {
                    int restoredThirst = (int) ((double) this.getRestoredThirst() * Math.min(1.0, (double) drainFluid.amount / this.getRestoredThirst()));
                    SurvivalToolsAPI.hydratePlayer(player, restoredThirst, this.getRestoredHydration());
                    return this.getContainer(stack);
                }
            }
        }
        return super.onItemUseFinish(stack, world, entity);
    }

    @Override
    public void onUpdate(@NotNull ItemStack stack, @NotNull World world, @NotNull Entity entity, int itemSlot, boolean isSelected) {
        if (this.getFluidAmountStored(stack) > this.getMaxFluidCapacity(stack)) {
            this.setFluidAmountStored(stack, getMaxFluidCapacity(stack));
        }

        if (entity instanceof EntityLivingBase) {
            if (!world.isRemote && entity.isWet() && !entity.isInWater() && (isSelected || ((EntityLivingBase) entity).getHeldItemOffhand() == stack)) {
                IFluidHandlerItem handler = FluidUtil.getFluidHandler(stack);
                if (world.getTotalWorldTime() % 20L == 0 && handler != null) {
                    handler.fill(new FluidStack(SurvivalToolsAPI.getPurifiedWater(), 5), true);
                }
            }
        }
        super.onUpdate(stack, world, entity, itemSlot, isSelected);
    }

    @Override
    public @NotNull EnumAction getItemUseAction(@NotNull ItemStack stack) {
        return EnumAction.DRINK;
    }

    @Override
    public int getMaxItemUseDuration(@NotNull ItemStack stack) {
        return 32;
    }

    @Override
    public void addInformation(@NotNull ItemStack stack, @Nullable World worldIn, List<String> tooltip, @NotNull ITooltipFlag flagIn) {
        tooltip.add(String.format("%d/%dmb %s", getFluidAmountStored(stack), this.capacity, new FluidStack(SurvivalToolsAPI.getPurifiedWater(), 1).getLocalizedName()));
    }

    @Override
    public void getSubItems(@NotNull CreativeTabs tab, @NotNull NonNullList<ItemStack> items) {
        if (this.isInCreativeTab(tab)) {
            items.add(new ItemStack(this));
            if (ConfigHandlerST.general.enableFullVariants) {
                ItemStack stack = new ItemStack(this);
                this.setFluidFull(stack);
                items.add(stack);
            }
        }
    }

    @Override
    public boolean showDurabilityBar(@NotNull ItemStack stack) {
        return this.getFluidAmountStored(stack) != this.getMaxFluidCapacity(stack);
    }

    @Override
    public double getDurabilityForDisplay(@NotNull ItemStack stack) {
        double max = this.getMaxFluidCapacity(stack);
        return (max - this.getFluidAmountStored(stack)) / max;
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return oldStack.getItem() != newStack.getItem() || slotChanged;
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(@NotNull ItemStack stack, @Nullable NBTTagCompound nbt) {
        return new FluidContainerItemWrapper(stack, this);
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
