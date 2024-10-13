package com.invadermonky.survivaltools.items;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import com.invadermonky.survivaltools.SurvivalTools;
import com.invadermonky.survivaltools.api.IAddition;
import com.invadermonky.survivaltools.api.fluid.FluidEnergyContainerItemWrapper;
import com.invadermonky.survivaltools.api.fluid.IFluidContainerItem;
import com.invadermonky.survivaltools.api.items.AbstractRFItem;
import com.invadermonky.survivaltools.config.ConfigHandlerST;
import com.invadermonky.survivaltools.registry.ModItemsST;
import com.invadermonky.survivaltools.util.helpers.StringHelper;
import com.invadermonky.survivaltools.util.helpers.SurvivalHelper;
import com.invadermonky.survivaltools.util.helpers.SurvivalItemHelper;
import com.invadermonky.survivaltools.util.libs.LibTags;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

import static com.invadermonky.survivaltools.util.libs.LibNames.PORTABLE_PURIFIER;

public class ItemPortablePurifier extends AbstractRFItem implements IFluidContainerItem, IBauble, IAddition {
    protected int fluidCapacity;
    protected int fluidCost;

    public ItemPortablePurifier() {
        this.setEnergyCapacity(ConfigHandlerST.flux_tools.portable_purifier.energyCapacity);
        this.setFluidCapacity(ConfigHandlerST.flux_tools.portable_purifier.fluidCapacity);
        this.setEnergyCost(ConfigHandlerST.flux_tools.portable_purifier.energyCost);
        this.setFluidCost(ConfigHandlerST.flux_tools.portable_purifier.fluidCost);
        this.setEnergyMaxReceive(20000);
        this.setEnergyMaxExtract(0);
        this.setMaxStackSize(1);
        this.addPropertyOverride(new ResourceLocation(SurvivalTools.MOD_ID, "fluid_level"), (stack, worldIn, entityIn) -> {
            int stored = this.getFluidStored(stack);
            return stored <= 0 ? 0 : (float) Math.ceil((double) stored / this.getMaxFluidCapacity() * 8.0);
        });
    }

    public ItemPortablePurifier setFluidCapacity(int fluidCapacity) {
        this.fluidCapacity = fluidCapacity;
        return this;
    }

    public int getMaxFluidCapacity() {
        return this.fluidCapacity;
    }

    public ItemPortablePurifier setFluidCost(int fluidCost) {
        this.fluidCost = fluidCost;
        return this;
    }

    public int getFluidCost() {
        return this.fluidCost;
    }

    public boolean getActivated(ItemStack stack) {
        return this.getTag(stack).getBoolean(LibTags.TAG_ENABLED);
    }

    public void setActivated(ItemStack stack, boolean active) {
        this.getTag(stack).setBoolean(LibTags.TAG_ENABLED, active);
    }

    public void setFluidStored(ItemStack stack, int amount) {
        this.getTag(stack).setInteger(LibTags.TAG_FLUID, Math.min(amount, this.getMaxFluidCapacity()));
    }

    public int getFluidStored(ItemStack stack) {
        if((stack.getTagCompound() == null) || (!stack.getTagCompound().hasKey(LibTags.TAG_FLUID))) {
            return 0;
        }
        return getTag(stack).getInteger(LibTags.TAG_FLUID);
    }

    public NBTTagCompound getTag(ItemStack stack) {
        if(!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }
        NBTTagCompound tag = stack.getTagCompound();
        if(!tag.hasKey(LibTags.TAG_FLUID)) {
            tag.setInteger(LibTags.TAG_FLUID, 0);
        }
        if(!tag.hasKey(LibTags.TAG_ENERGY)) {
            tag.setInteger(LibTags.TAG_ENERGY, 0);
        }
        if(!tag.hasKey(LibTags.TAG_ENABLED)) {
            tag.setBoolean(LibTags.TAG_ENABLED, false);
        }
        return tag;
    }

    public ItemStack fillEnergy(ItemStack stack) {
        this.setEnergyStored(stack, this.getMaxEnergyStored(stack));
        return stack;
    }

    public ItemStack fillFluid(ItemStack stack) {
        this.setFluidStored(stack, this.getMaxFluidCapacity());
        return stack;
    }

    public void doUpdateTick(ItemStack stack, EntityPlayer player) {
        int fluidStored = this.getFluidStored(stack);
        int energyStored = this.getEnergyStored(stack);

        if(fluidStored > this.getMaxFluidCapacity()) {
            this.setFluidStored(stack, this.getMaxFluidCapacity());
        }
        if(energyStored > this.getMaxEnergyStored(stack)) {
            this.setEnergyStored(stack, this.getMaxEnergyStored(stack));
        }

        if(this.getActivated(stack)) {
            if(energyStored < this.getEnergyCost() || fluidStored < this.getFluidCost()) {
                this.setActivated(stack, false);
            } else {
                if (player.ticksExisted % 60 == 0 && !(player).isCreative()) {
                    if (SurvivalHelper.isThirsty(player)) {
                        SurvivalHelper.hydratePlayer(player, 1, 1.0f);
                        this.drain(stack, this.getFluidCost(), true);
                    }
                }
                this.setEnergyStored(stack, this.getEnergyStored(stack) - this.getEnergyCost());
            }
        }
    }

    /*
     *  Item
     */

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if(this.isInCreativeTab(tab)) {
            items.add(new ItemStack(this));
            if (ConfigHandlerST.enableFullVariants) {
                ItemStack fullStack = new ItemStack(this);
                this.fillEnergy(fullStack);
                this.fillFluid(fullStack);
                items.add(fullStack);
            }
        }
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
        return new FluidEnergyContainerItemWrapper(stack, this, this);
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return oldStack.getItem() != newStack.getItem() || slotChanged;
    }

    @Override
    public boolean hasEffect(ItemStack stack) {
        return this.getActivated(stack);
    }

    @Override
    public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if(!worldIn.isRemote && entityIn instanceof EntityPlayer) {
            doUpdateTick(stack, (EntityPlayer) entityIn);
        }
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if(player.isSneaking() && getEnergyStored(stack) > getEnergyCost()) {
            setActivated(stack, !getActivated(stack));
            return new ActionResult<>(EnumActionResult.SUCCESS, stack);
        } else {
            boolean flag =  this.getFluidStored(stack) < this.getMaxFluidCapacity();
            RayTraceResult rtr = this.rayTrace(world, player, flag);
            if(rtr == null || rtr.typeOfHit != RayTraceResult.Type.BLOCK) {
                return new ActionResult<>(EnumActionResult.PASS, stack);
            } else {
                BlockPos tracePos = rtr.getBlockPos();
                if(!world.isBlockModifiable(player, tracePos)) {
                    return new ActionResult<>(EnumActionResult.FAIL, stack);
                } else if(flag) {
                    if(!player.canPlayerEdit(tracePos.offset(rtr.sideHit), rtr.sideHit, stack)) {
                        return new ActionResult<>(EnumActionResult.FAIL, stack);
                    } else {
                        IBlockState state = world.getBlockState(tracePos);
                        Block block = state.getBlock();
                        if(block == Blocks.WATER) {
                            world.setBlockToAir(tracePos);
                            player.playSound(SoundEvents.ITEM_BUCKET_FILL, 1.0f, 1.0f);
                            this.fill(stack, new FluidStack(FluidRegistry.WATER, 1000), true);
                            return new ActionResult<>(EnumActionResult.SUCCESS, stack);
                        } else if(FluidUtil.interactWithFluidHandler(player, hand, world, tracePos, rtr.sideHit)) {
                            return new ActionResult<>(EnumActionResult.SUCCESS, stack);
                        }
                    }
                }
            }
        }
        return new ActionResult<>(EnumActionResult.PASS, stack);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        if(ConfigHandlerST.flux_tools.portable_purifier.shortenEnergyTooltip) {
            tooltip.add(ChatFormatting.ITALIC + String.format("%s/%s RF", StringHelper.getCleanNumber(getEnergyStored(stack)), StringHelper.getCleanNumber(getMaxEnergyStored(stack))));
        } else {
            tooltip.add(ChatFormatting.ITALIC + String.format("%,d/%,d RF", getEnergyStored(stack), getMaxEnergyStored(stack)));
        }
        tooltip.add(String.format("%d/%dmb %s", getFluidStored(stack), this.fluidCapacity, new FluidStack(FluidRegistry.WATER, 1).getLocalizedName()));
        tooltip.add(I18n.format(StringHelper.getTranslationKey(getTag(stack).getBoolean(LibTags.TAG_ENABLED) ? "enabled" : "disabled", "tooltip")));
        tooltip.add(I18n.format(StringHelper.getTranslationKey(PORTABLE_PURIFIER, "tooltip", "desc")));
    }

    /*
     *  IFluidContainerItem
     */

    @Nonnull
    @Override
    public ItemStack getContainer(ItemStack stack) {
        return stack;
    }

    @Override
    public IFluidTankProperties[] getTankProperties(ItemStack stack) {
        return new IFluidTankProperties[0];
    }

    @Override
    public int fill(ItemStack stack, FluidStack resource, boolean doFill) {
        if(resource.getFluid() == FluidRegistry.WATER) {
            int fluid = this.getTag(stack).getInteger(LibTags.TAG_FLUID);
            int fluidReceived = Math.min(this.getMaxFluidCapacity() - fluid, resource.amount);
            if(doFill) {
                fluid += fluidReceived;
                this.getTag(stack).setInteger(LibTags.TAG_FLUID, fluid);
            }
            return fluidReceived;
        }
        return 0;
    }

    @Nullable
    @Override
    public FluidStack drain(ItemStack stack, FluidStack resource, boolean doDrain) {
        return null;
    }

    @Nullable
    @Override
    public FluidStack drain(ItemStack stack, int maxDrain, boolean doDrain) {
        int fluid = this.getTag(stack).getInteger(LibTags.TAG_FLUID);
        int fluidReceived = Math.min(fluid, maxDrain);
        if(doDrain) {
            fluid -= fluidReceived;
            this.getTag(stack).setInteger(LibTags.TAG_FLUID, fluid);
        }
        return null;
    }

    /*
     *  IBauble
     */

    @Override
    public void onWornTick(ItemStack itemstack, EntityLivingBase player) {
        if(!player.world.isRemote && player instanceof EntityPlayer) {
            this.doUpdateTick(itemstack, (EntityPlayer) player);
        }
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
                "BEB", "FHF", "RHR",
                'B', Items.GLASS_BOTTLE,
                'E', Items.ENDER_PEARL,
                'F', SurvivalItemHelper.getCharcoalFilterStack(),
                'H', ModItemsST.hydration_pack_reinforced,
                'R', "blockRedstone"
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
        return ConfigHandlerST.flux_tools.portable_purifier.enable && SurvivalHelper.isThirstFeatureEnabled();
    }
}
