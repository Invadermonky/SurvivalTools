package com.invadermonky.survivaltools.api.items;

import baubles.api.BaublesApi;
import baubles.api.IBauble;
import baubles.api.cap.IBaublesItemHandler;
import com.invadermonky.survivaltools.SurvivalTools;
import com.invadermonky.survivaltools.util.libs.CreativeTabST;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractEquipableBauble extends Item implements IBauble, IItemAddition {
    public AbstractEquipableBauble(String unlocName) {
        this.setRegistryName(SurvivalTools.MOD_ID, unlocName);
        this.setTranslationKey(this.getRegistryName().toString());
        this.setCreativeTab(CreativeTabST.TAB_ST);
        this.setMaxStackSize(1);
    }

    @Override
    public @NotNull ActionResult<ItemStack> onItemRightClick(@NotNull World world, EntityPlayer player, @NotNull EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        ItemStack toEquip = stack.copy();
        if (player.isSneaking() && this.canEquip(toEquip, player)) {
            if (world.isRemote) {
                return new ActionResult<>(EnumActionResult.SUCCESS, stack);
            }

            IBaublesItemHandler baubles = BaublesApi.getBaublesHandler(player);
            for (int slot = 0; slot < baubles.getSlots(); ++slot) {
                if (baubles.isItemValidForSlot(slot, toEquip, player)) {
                    ItemStack slotStack = baubles.getStackInSlot(slot);
                    if(slotStack.isEmpty()) {
                        baubles.setStackInSlot(slot, toEquip);
                        ((IBauble) toEquip.getItem()).onEquipped(toEquip, player);
                        stack.shrink(1);
                        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
                    }
                }
            }
        }
        return new ActionResult<>(EnumActionResult.PASS, stack);
    }
}
