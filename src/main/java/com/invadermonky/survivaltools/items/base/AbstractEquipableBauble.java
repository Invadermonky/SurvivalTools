package com.invadermonky.survivaltools.items.base;

import baubles.api.BaublesApi;
import baubles.api.IBauble;
import baubles.api.cap.BaublesCapabilities;
import baubles.api.cap.IBaublesItemHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractEquipableBauble extends Item implements IBauble {
    public AbstractEquipableBauble() {
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

            for (int i = 0; i < baubles.getSlots(); ++i) {
                if (baubles.isItemValidForSlot(i, toEquip, player)) {
                    ItemStack stackInSlot = baubles.getStackInSlot(i);
                    IBauble baubleInSlot = stackInSlot.getCapability(BaublesCapabilities.CAPABILITY_ITEM_BAUBLE, null);
                    if (stackInSlot.isEmpty() || baubleInSlot == null || baubleInSlot.canUnequip(stackInSlot, player)) {
                        baubles.setStackInSlot(i, ItemStack.EMPTY);
                        baubles.setStackInSlot(i, toEquip);
                        ((IBauble) toEquip.getItem()).onEquipped(toEquip, player);
                        stack.shrink(1);
                        if (!stackInSlot.isEmpty()) {
                            if (baubleInSlot != null) {
                                baubleInSlot.onUnequipped(stackInSlot, player);
                            }

                            if (stack.isEmpty()) {
                                return new ActionResult<>(EnumActionResult.SUCCESS, stack);
                            }

                            ItemHandlerHelper.giveItemToPlayer(player, stackInSlot);
                        }

                        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
                    }
                }
            }
        }
        return new ActionResult<>(EnumActionResult.PASS, stack);
    }
}
