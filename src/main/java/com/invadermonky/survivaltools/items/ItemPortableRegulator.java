package com.invadermonky.survivaltools.items;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import com.invadermonky.survivaltools.api.IAddition;
import com.invadermonky.survivaltools.api.SurvivalToolsAPI;
import com.invadermonky.survivaltools.api.items.AbstractRFItem;
import com.invadermonky.survivaltools.config.ConfigHandlerST;
import com.invadermonky.survivaltools.util.helpers.StringHelper;
import com.invadermonky.survivaltools.util.libs.LibTags;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;

import javax.annotation.Nullable;
import java.util.List;

public class ItemPortableRegulator extends AbstractRFItem implements IBauble, IAddition {
    public ItemPortableRegulator() {
        this.setEnergyMaxReceive(20000);
        this.setEnergyMaxExtract(0);
        this.setEnergyCapacity(ConfigHandlerST.flux_tools.portable_regulator.energyCapacity);
        this.setEnergyCost(ConfigHandlerST.flux_tools.portable_regulator.energyCost);
        this.setMaxStackSize(1);
    }

    @Override
    public void onUpdate(ItemStack stack, World worldIn, Entity entity, int itemSlot, boolean isSelected) {
        if(!worldIn.isRemote && entity instanceof EntityPlayer) {
            doUpdateTick(stack, (EntityPlayer) entity);
        }
    }

    @Override
    public void onWornTick(ItemStack stack, EntityLivingBase player) {
        if(!player.world.isRemote && player instanceof EntityPlayer) {
            doUpdateTick(stack, (EntityPlayer) player);
        }
    }

    private void doUpdateTick(ItemStack stack, EntityPlayer player) {
        if(getMaxEnergyStored(stack) < getEnergyStored(stack)) {
            setEnergyStored(stack, getMaxEnergyStored(stack));
        }

        if(getActivated(stack)) {
            if(getEnergyStored(stack) < getEnergyCost()) {
                setActivated(stack, false);
            } else {
                if(player.ticksExisted % 100 == 0) {
                    SurvivalToolsAPI.stabilizePlayerTemperature(player, ConfigHandlerST.flux_tools.portable_regulator.maxCooling, ConfigHandlerST.flux_tools.portable_regulator.maxHeating);
                }
                setEnergyStored(stack, getEnergyStored(stack) - this.energyCost);
            }
        }
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return oldStack.getItem() != newStack.getItem() || slotChanged;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if(player.isSneaking() && getEnergyStored(stack) > getEnergyCost()) {
            setActivated(stack, !getActivated(stack));
            return new ActionResult<>(EnumActionResult.SUCCESS, stack);
        }
        return new ActionResult<>(EnumActionResult.PASS, stack);
    }

    @Override
    public boolean hasEffect(ItemStack stack) {
        return getActivated(stack);
    }

    public boolean getActivated(ItemStack stack) {
        return getTag(stack).getBoolean(LibTags.TAG_ENABLED);
    }

    public void setActivated(ItemStack stack, boolean activated) {
        getTag(stack).setBoolean(LibTags.TAG_ENABLED, activated);
    }

    public NBTTagCompound getTag(ItemStack stack) {
        if(!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }
        return stack.getTagCompound();
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        if(ConfigHandlerST.flux_tools.portable_regulator.shortenEnergyTooltip) {
            tooltip.add(ChatFormatting.ITALIC + String.format("%s/%s RF", StringHelper.getCleanNumber(getEnergyStored(stack)), StringHelper.getCleanNumber(getMaxEnergyStored(stack))));
        } else {
            tooltip.add(ChatFormatting.ITALIC + String.format("%,d/%,d RF", getEnergyStored(stack), getMaxEnergyStored(stack)));
        }
        tooltip.add(I18n.format(StringHelper.getTranslationKey(getTag(stack).getBoolean(LibTags.TAG_ENABLED) ? "enabled" : "disabled", "tooltip")));
        tooltip.add(I18n.format(StringHelper.getTranslationKey("portable_regulator", "tooltip", "desc")));
        super.addInformation(stack, worldIn, tooltip, flagIn);
    }

    @Override
    public boolean willAutoSync(ItemStack itemstack, EntityLivingBase player) {
        return true;
    }

    @Override
    public BaubleType getBaubleType(ItemStack itemStack) {
        return BaubleType.TRINKET;
    }

    /*
     *  IAddition
     */

    @Override
    public void registerRecipe(IForgeRegistry<IRecipe> registry) {
        CraftingHelper.ShapedPrimer primer = CraftingHelper.parseShaped(
                "HEC", "HGC", "RTR",
                'C', SurvivalToolsAPI.getCoolerStack(),
                'E', Items.ENDER_PEARL,
                'H', SurvivalToolsAPI.getHeaterStack(),
                'G', "blockGold",
                'R', "blockRedstone",
                'T', SurvivalToolsAPI.getThermometerStack()
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
        return ConfigHandlerST.flux_tools.portable_regulator.enable && SurvivalToolsAPI.isTemperatureFeatureEnabled();
    }
}
