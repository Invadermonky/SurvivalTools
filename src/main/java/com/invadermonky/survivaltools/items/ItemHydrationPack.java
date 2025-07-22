package com.invadermonky.survivaltools.items;

import baubles.api.BaubleType;
import com.invadermonky.survivaltools.api.IAddition;
import com.invadermonky.survivaltools.api.SurvivalToolsAPI;
import com.invadermonky.survivaltools.api.fluid.FluidContainerItemWrapper;
import com.invadermonky.survivaltools.api.fluid.IPurifiedFluidContainerItem;
import com.invadermonky.survivaltools.config.ConfigHandlerST;
import com.invadermonky.survivaltools.crafting.recipes.RecipeHydrationPackAttach;
import com.invadermonky.survivaltools.crafting.recipes.RecipeHydrationPackRemove;
import com.invadermonky.survivaltools.items.base.AbstractEquipableBauble;
import com.invadermonky.survivaltools.util.ChatUtils;
import com.invadermonky.survivaltools.util.helpers.StringHelper;
import com.invadermonky.survivaltools.util.libs.LibNames;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

import static com.invadermonky.survivaltools.util.libs.LibTags.TAG_HYDRATION_PACK;

public class ItemHydrationPack extends AbstractEquipableBauble implements IPurifiedFluidContainerItem, IAddition {
    protected int capacity;
    protected int fluidCost;
    protected int restoredThirst;
    protected float restoredHydration;

    public ItemHydrationPack(int maxCapacity) {
        this.setMaxStackSize(1);
        this.setMaxFluidCapacity(maxCapacity);
        this.setFluidCost(ConfigHandlerST.tools.hydration_packs.fluidCost);
        this.setRestoredThirst(ConfigHandlerST.tools.hydration_packs.restoredThirst);
        this.setRestoredHydration(ConfigHandlerST.tools.hydration_packs.restoredHydration);
    }

    public ItemHydrationPack() {
        this(ConfigHandlerST.tools.hydration_packs.fluidCapacityBasic);
    }

    public static boolean hasHydrationPackAttached(ItemStack stack) {
        return stack.hasTagCompound() && stack.getTagCompound().hasKey(TAG_HYDRATION_PACK);
    }

    public static ItemStack getHydrationPack(ItemStack stack) {
        if (stack.hasTagCompound() && hasHydrationPackAttached(stack)) {
            NBTTagCompound packTag = stack.getTagCompound().getCompoundTag(TAG_HYDRATION_PACK);
            return new ItemStack(packTag);
        }
        return ItemStack.EMPTY;
    }

    public static void attachHydrationPack(ItemStack chestStack, ItemStack hydrationPack) {
        if (!chestStack.hasTagCompound()) {
            chestStack.setTagCompound(new NBTTagCompound());
        }
        NBTTagCompound packTag = new NBTTagCompound();
        hydrationPack.writeToNBT(packTag);
        chestStack.getTagCompound().setTag(TAG_HYDRATION_PACK, packTag);
    }

    public static ItemStack removeHydrationPack(ItemStack chestStack) {
        if (chestStack.hasTagCompound() && hasHydrationPackAttached(chestStack)) {
            NBTTagCompound packTag = chestStack.getTagCompound().getCompoundTag(TAG_HYDRATION_PACK);
            chestStack.getTagCompound().removeTag(TAG_HYDRATION_PACK);
            return new ItemStack(packTag);
        }
        return ItemStack.EMPTY;
    }

    public static void onEntityUpdate(LivingEvent.LivingUpdateEvent event) {
        if (ConfigHandlerST.tools.hydration_packs.attachmentRecipe) {
            if (event.getEntityLiving().world.isRemote || !(event.getEntityLiving() instanceof EntityPlayer))
                return;

            EntityPlayer player = (EntityPlayer) event.getEntityLiving();
            ItemStack chestStack = player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
            if (player.ticksExisted % 60 == 0 && hasHydrationPackAttached(chestStack)) {
                ItemStack packStack = getHydrationPack(chestStack);
                if (((ItemHydrationPack) packStack.getItem()).doTick(player, packStack)) {
                    attachHydrationPack(chestStack, packStack);
                }
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public static void onTooltipEvent(ItemTooltipEvent event) {
        if (ConfigHandlerST.tools.hydration_packs.attachmentRecipe) {
            ItemStack stack = event.getItemStack();
            if (!stack.isEmpty() && hasHydrationPackAttached(stack)) {
                ItemStack packStack = getHydrationPack(stack);
                ItemHydrationPack pack = (ItemHydrationPack) packStack.getItem();
                event.getToolTip().add(I18n.format(StringHelper.getTranslationKey(LibNames.HYDRATION_PACK, "tooltip", "attached"), pack.getFluidAmountStored(packStack), pack.getMaxFluidCapacity(stack)));
            }
        }
    }

    public ItemHydrationPack setMaxFluidCapacity(int capacity) {
        this.capacity = capacity;
        return this;
    }

    public int getFluidCost() {
        return this.fluidCost;
    }

    public ItemHydrationPack setFluidCost(int fluidCost) {
        this.fluidCost = fluidCost;
        return this;
    }

    public ItemHydrationPack setRestoredThirst(int restoredThirst) {
        this.restoredThirst = restoredThirst;
        return this;
    }

    public ItemHydrationPack setRestoredHydration(double restoredHydration) {
        this.restoredHydration = (float) restoredHydration;
        return this;
    }

    public boolean doTick(EntityPlayer player, ItemStack stack) {
        boolean did = false;
        if (this.getFluidAmountStored(stack) > this.capacity) {
            this.setFluidAmountStored(stack, this.capacity);
            did = true;
        }

        if (player.ticksExisted % 60 == 0 && !player.isCreative()) {
            int currentFluid = this.getFluidAmountStored(stack);
            if (this.restoredThirst <= SurvivalToolsAPI.getMissingThirst(player) && currentFluid > 0) {
                int thirstRestored = this.getFluidCost() <= currentFluid ? this.restoredThirst : (int) (((double) currentFluid / this.getFluidCost()) * this.restoredThirst);
                SurvivalToolsAPI.hydratePlayer(player, thirstRestored, this.restoredHydration);
                player.world.playSound(null, player.getPosition(), SoundEvents.ENTITY_GENERIC_DRINK, SoundCategory.PLAYERS, 0.6f, 1.0f);
                this.drain(stack, this.getFluidCost(), true);
                did = true;
                if (this.getFluidAmountStored(stack) <= 0) {
                    ChatUtils.sendNoSpam(player, StringHelper.getTranslatedComponent(LibNames.HYDRATION_PACK, "chat", "empty"));
                }
            }
        }
        return did;
    }

    /*
     * Item
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
                    }
                }
            }
        }
        return super.onItemRightClick(world, player, hand);
    }

    @Override
    public void onUpdate(@NotNull ItemStack stack, @NotNull World world, @NotNull Entity entity, int itemSlot, boolean isSelected) {
        if (this.getFluidAmountStored(stack) > this.getMaxFluidCapacity(stack)) {
            this.setFluidAmountStored(stack, getMaxFluidCapacity(stack));
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(@NotNull ItemStack stack, @Nullable World worldIn, List<String> tooltip, @NotNull ITooltipFlag flagIn) {
        tooltip.add(String.format("%d/%dmb %s", getFluidAmountStored(stack), this.capacity, new FluidStack(SurvivalToolsAPI.getPurifiedWater(), 1).getLocalizedName()));
        tooltip.add(StringHelper.getTranslatedString(LibNames.HYDRATION_PACK, "tooltip", "desc0"));
        if (ConfigHandlerST.tools.hydration_packs.attachmentRecipe) {
            tooltip.add(StringHelper.getTranslatedString(TAG_HYDRATION_PACK, "tooltip", "desc1"));
        }
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

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(@NotNull ItemStack stack, @Nullable NBTTagCompound nbt) {
        return new FluidContainerItemWrapper(stack, this);
    }

    /*
     *  IPurifiedFluidContainerItem
     */

    @Override
    public int getMaxFluidCapacity(ItemStack stack) {
        return this.capacity;
    }

    /*
     *  IBauble
     */

    @Override
    public BaubleType getBaubleType(ItemStack itemStack) {
        return BaubleType.BODY;
    }

    @Override
    public void onWornTick(ItemStack stack, EntityLivingBase player) {
        if (player.world.isRemote || !(player instanceof EntityPlayer))
            return;

        this.doTick((EntityPlayer) player, stack);
    }

    @Override
    public boolean willAutoSync(ItemStack itemstack, EntityLivingBase player) {
        return true;
    }

    /*
     *  IAddition
     */

    @Override
    public void registerRecipe(IForgeRegistry<IRecipe> registry) {
        if (ConfigHandlerST.tools.hydration_packs.attachmentRecipe) {
            registry.register(new RecipeHydrationPackAttach());
            registry.register(new RecipeHydrationPackRemove());
        }
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
