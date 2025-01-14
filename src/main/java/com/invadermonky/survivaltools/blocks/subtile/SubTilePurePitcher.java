package com.invadermonky.survivaltools.blocks.subtile;

import com.invadermonky.survivaltools.SurvivalTools;
import com.invadermonky.survivaltools.api.IAddition;
import com.invadermonky.survivaltools.api.SurvivalToolsAPI;
import com.invadermonky.survivaltools.config.ConfigHandlerST;
import com.invadermonky.survivaltools.util.libs.LibNames;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.api.BotaniaAPIClient;
import vazkii.botania.api.lexicon.LexiconEntry;
import vazkii.botania.api.recipe.RecipePetals;
import vazkii.botania.api.subtile.RadiusDescriptor;
import vazkii.botania.api.subtile.SubTileFunctional;
import vazkii.botania.client.core.handler.HUDHandler;
import vazkii.botania.client.core.helper.RenderHelper;
import vazkii.botania.common.block.tile.TileFloatingSpecialFlower;
import vazkii.botania.common.crafting.ModPetalRecipes;
import vazkii.botania.common.item.block.ItemBlockSpecialFlower;
import vazkii.botania.common.lexicon.BasicLexiconEntry;
import vazkii.botania.common.lexicon.page.PagePetalRecipe;
import vazkii.botania.common.lexicon.page.PageText;

import javax.annotation.Nullable;
import java.awt.*;

public class SubTilePurePitcher extends SubTileFunctional implements IFluidHandler, IAddition {
    public static LexiconEntry PURE_PITCHER_ENTRY;
    public static RecipePetals PURE_PITCHER_RECIPE;
    public static int RANGE = 1;

    public int burnTime;
    public int knownFluidAmount = -1;
    public int fluidAmount;
    public int fluidMaxCapacity = 1000;

    @Override
    public void readFromPacketNBT(NBTTagCompound cmp) {
        super.readFromPacketNBT(cmp);
        this.burnTime = cmp.getInteger("burnTime");
        this.fluidAmount = cmp.getInteger("fluidAmount");
    }

    @Override
    public void writeToPacketNBT(NBTTagCompound cmp) {
        super.writeToPacketNBT(cmp);
        cmp.setInteger("burnTime", this.burnTime);
        cmp.setInteger("fluidAmount", this.fluidAmount);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (!this.supertile.getWorld().isRemote && this.redstoneSignal <= 0) {
            boolean did = false;
            if (this.burnTime <= 0 && this.mana >= this.getMaxMana()) {
                if (this.consumeNearbyWater()) {
                    this.burnTime = 200;
                    this.mana -= this.getMaxMana();
                    this.playSound();
                    did = true;
                }
            }

            if (this.burnTime > 0 && this.mana > 2 && this.consumeNearbyWater()) {
                --this.burnTime;
                if (this.ticksExisted % 20 == 0) {
                    if (this.fluidAmount < this.fluidMaxCapacity) {
                        this.fluidAmount = Math.min(this.fluidMaxCapacity, this.fluidAmount + this.getFluidGeneration());
                    } else if (this.supertile instanceof TileFloatingSpecialFlower) {
                        IFluidHandler handler = FluidUtil.getFluidHandler(this.supertile.getWorld(), this.getPos().down(), EnumFacing.UP);
                        if(handler != null) {
                            handler.fill(new FluidStack(SurvivalToolsAPI.getPurifiedWater(), this.getFluidGeneration()), true);
                        }
                    }
                }
                did = true;
            }

            if (did) {
                this.supertile.getWorld().updateComparatorOutputLevel(this.getPos(), this.supertile.getBlockType());
                this.sync();
            }
        }
    }

    public void playSound() {
        this.supertile.getWorld().playSound(null, this.supertile.getPos(), SoundEvents.ENTITY_GENERIC_DRINK, SoundCategory.BLOCKS, 0.01F, 0.5F + (float) Math.random() * 0.5F);
    }

    public boolean consumeNearbyWater() {
        BlockPos pos = null;
        for (BlockPos checkPos : BlockPos.getAllInBox(this.getPos().add(-RANGE, -RANGE, -RANGE), this.getPos().add(RANGE, RANGE, RANGE))) {
            if (this.supertile.getWorld().getBlockState(checkPos).getMaterial() == Material.WATER) {
                PropertyInteger prop = this.supertile.getWorld().getBlockState(checkPos).getBlock() instanceof BlockLiquid ? BlockLiquid.LEVEL : (this.supertile.getWorld().getBlockState(checkPos).getBlock() instanceof BlockFluidBase ? BlockFluidBase.LEVEL : null);
                if (prop != null && this.supertile.getWorld().getBlockState(checkPos).getValue(prop) == 0) {
                    pos = checkPos;
                    break;
                }
            }
        }

        if (pos != null) {
            int waterAround = 0;
            for (EnumFacing facing : EnumFacing.HORIZONTALS) {
                if (this.supertile.getWorld().getBlockState(pos.offset(facing)).getMaterial() == Material.WATER) {
                    ++waterAround;
                }
            }
            if (waterAround < 2) {
                this.supertile.getWorld().setBlockToAir(pos);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean acceptsRedstone() {
        return true;
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        ItemStack heldStack = player.getHeldItem(hand);
        if (heldStack.getItem() == Items.GLASS_BOTTLE) {
            if (this.fluidAmount >= 333) {
                this.fluidAmount -= 333;
                heldStack.shrink(1);
                ItemStack bottleStack = SurvivalToolsAPI.getPurifiedWaterBottleStack();
                if (heldStack.isEmpty()) {
                    player.setHeldItem(hand, bottleStack);
                } else if (!player.addItemStackToInventory(bottleStack)) {
                    player.dropItem(bottleStack, true);
                }
                world.playSound(null, this.getPos(), SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.PLAYERS, 0.8f, 1.0f);
                this.sync();
                this.supertile.getWorld().updateComparatorOutputLevel(this.getPos(), this.supertile.getBlockType());
                return true;
            }
        } else if(this.fluidAmount > 0 && FluidUtil.interactWithFluidHandler(player, hand, this)) {
            this.sync();
            this.supertile.getWorld().updateComparatorOutputLevel(this.getPos(), this.supertile.getBlockType());
            return true;
        }
        return false;
    }

    @Override
    public boolean onWanded(EntityPlayer player, ItemStack wand) {
        if(player != null) {
            this.knownFluidAmount = this.fluidAmount;
        }
        return super.onWanded(player, wand);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void renderHUD(Minecraft mc, ScaledResolution res) {
        super.renderHUD(mc, res);
        int color = MapColor.LIGHT_BLUE.colorValue;
        this.drawComplexLiquidHUD(color, res, SurvivalToolsAPI.getPurifiedWaterBottleStack());
    }

    public void renderLiquidBar(int x, int y, int color, float alpha) {
        Minecraft mc = Minecraft.getMinecraft();
        GlStateManager.color(1.0f, 1.0f, 1.0f, alpha);
        mc.renderEngine.bindTexture(HUDHandler.manaBar);
        RenderHelper.drawTexturedModalRect(x, y,0.0f, 0,0,102,5);
        int fluidPercentage = Math.max(0, (int) ((double) this.knownFluidAmount / (double) this.fluidMaxCapacity * 100.0));
        if(fluidPercentage == 0 && this.knownFluidAmount < 0) {
            fluidPercentage = 1;
        }
        RenderHelper.drawTexturedModalRect(x + 1, y  + 1, 0.0f, 0, 5, 100, 3);
        Color color_ = new Color(color);
        GL11.glColor4ub((byte) color_.getRed(), (byte) color_.getGreen(), (byte) color_.getBlue(), (byte) ((int) (255.0F * alpha)));
        RenderHelper.drawTexturedModalRect(x + 1, y + 1, 0F, 0, 5, Math.min(100, fluidPercentage), 3);
        GL11.glColor4ub((byte) -1, (byte) -1, (byte) -1, (byte) -1);
    }

    public void drawSimpleLiquidHUD(int color, ScaledResolution res) {
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);
        Minecraft mc = Minecraft.getMinecraft();
        int x = res.getScaledWidth() / 2 - 51;
        int y = res.getScaledHeight() / 2 + 36;
        renderLiquidBar(x, y, color, this.knownFluidAmount < 0 ? 0.5F : 1.0F);
        if(this.knownFluidAmount < 0) {
            String text = I18n.format("botaniamisc.statusUnknown");
            x = res.getScaledWidth() / 2 - mc.fontRenderer.getStringWidth(text) / 2;
            --y;
            mc.fontRenderer.drawString(text, x, y, color);
        }
        GlStateManager.disableBlend();
    }

    public void drawComplexLiquidHUD(int color, ScaledResolution res, ItemStack bindDisplay) {
        this.drawSimpleLiquidHUD(color, res);
        Minecraft mc = Minecraft.getMinecraft();
        int x = res.getScaledWidth() / 2 + 55;
        int y = res.getScaledHeight() / 2 + 30;
        net.minecraft.client.renderer.RenderHelper.enableGUIStandardItemLighting();
        GlStateManager.enableRescaleNormal();
        mc.getRenderItem().renderItemAndEffectIntoGUI(bindDisplay, x, y);
        net.minecraft.client.renderer.RenderHelper.disableStandardItemLighting();
    }

    @Override
    public RadiusDescriptor getRadius() {
        return new RadiusDescriptor.Square(this.supertile.getPos(), RANGE);
    }

    @Override
    public int getComparatorInputOverride() {
        return (int) Math.floor((float) this.fluidAmount / (float) this.fluidMaxCapacity * 14.0F) + (this.fluidAmount > 0 ? 1 : 0);
    }

    @Override
    public int getMaxMana() {
        return 200;
    }

    public int getFluidGeneration() {
        return ConfigHandlerST.botania.pure_pitcher.fluidGeneration;
    }

    @Override
    public int getColor() {
        return 3942580;
    }

    @Override
    public LexiconEntry getEntry() {
        return PURE_PITCHER_ENTRY;
    }

    /*
     *  IFluidHandler
     */

    @Override
    public IFluidTankProperties[] getTankProperties() {
        return new IFluidTankProperties[] {new IFluidTankProperties() {
            @Nullable
            @Override
            public FluidStack getContents() {
                return fluidAmount > 0 ? new FluidStack(SurvivalToolsAPI.getPurifiedWater(), fluidAmount) : null;
            }

            @Override
            public int getCapacity() {
                return fluidMaxCapacity;
            }

            @Override
            public boolean canFill() {
                return false;
            }

            @Override
            public boolean canDrain() {
                return false;
            }

            @Override
            public boolean canFillFluidType(FluidStack fluidStack) {
                return false;
            }

            @Override
            public boolean canDrainFluidType(FluidStack fluidStack) {
                return fluidStack != null && fluidStack.getFluid() == SurvivalToolsAPI.getPurifiedWater() && fluidAmount > 0;
            }
        }};
    }

    @Override
    public int fill(FluidStack resource, boolean doFill) {
        return 0;
    }

    @Nullable
    @Override
    public FluidStack drain(FluidStack resource, boolean doDrain) {
        if(resource != null && resource.getFluid() == SurvivalToolsAPI.getPurifiedWater()) {
            return this.drain(resource.amount, doDrain);
        }
        return null;
    }

    @Nullable
    @Override
    public FluidStack drain(int maxDrain, boolean doDrain) {
        int fluidDrained = Math.min(this.fluidAmount, maxDrain);
        if(doDrain) {
            this.fluidAmount -= fluidDrained;
            this.sync();
        }
        return fluidDrained > 0 ? new FluidStack(SurvivalToolsAPI.getPurifiedWater(), fluidDrained) : null;
    }

    /*
     *  IAddition
     */

    @Override
    public void preInit() {
        BotaniaAPI.addSubTileToCreativeMenu(LibNames.PURE_PITCHER);
        BotaniaAPI.registerSubTile(LibNames.PURE_PITCHER, SubTilePurePitcher.class);
    }

    @Override
    public void postInit() {
        ItemStack waterFilterStack = SurvivalToolsAPI.getWaterFilterStack();
        PURE_PITCHER_RECIPE = new RecipePetals(ItemBlockSpecialFlower.ofType(LibNames.PURE_PITCHER), ModPetalRecipes.blue, ModPetalRecipes.lightBlue, ModPetalRecipes.cyan, waterFilterStack, waterFilterStack, new ItemStack(Items.BUCKET), "runeWaterB", "redstoneRoot");
        BotaniaAPI.registerPetalRecipe(PURE_PITCHER_RECIPE.getOutput(), PURE_PITCHER_RECIPE.getInputs().toArray());
        PURE_PITCHER_ENTRY = new BasicLexiconEntry("purePitcher", BotaniaAPI.categoryFunctionalFlowers);
        PURE_PITCHER_ENTRY.setLexiconPages(
                new PageText("0"),
                new PageText("1"),
                new PageText("2"),
                new PagePetalRecipe<>("3", PURE_PITCHER_RECIPE)
        );
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerModel(ModelRegistryEvent event) {
        BotaniaAPIClient.registerSubtileModel(SubTilePurePitcher.class, new ModelResourceLocation(new ResourceLocation(SurvivalTools.MOD_ID, LibNames.PURE_PITCHER), "normal"));
    }

    @Override
    public boolean isEnabled() {
        return ConfigHandlerST.botania.pure_pitcher.enable && SurvivalToolsAPI.isThirstFeatureEnabled();
    }
}
