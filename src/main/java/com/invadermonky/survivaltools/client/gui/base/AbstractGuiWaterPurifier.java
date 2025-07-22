package com.invadermonky.survivaltools.client.gui.base;

import com.invadermonky.survivaltools.SurvivalTools;
import com.invadermonky.survivaltools.client.gui.button.GuiButtonGuiButtonConsumer;
import com.invadermonky.survivaltools.inventory.container.base.AbstractContainerWaterPurifier;
import com.invadermonky.survivaltools.tile.base.AbstractTileWaterPurifier;
import com.invadermonky.survivaltools.util.helpers.StringHelper;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractGuiWaterPurifier<T extends AbstractTileWaterPurifier, S extends AbstractContainerWaterPurifier<T>> extends GuiContainer {
    public static final ResourceLocation TEXTURE = new ResourceLocation(SurvivalTools.MOD_ID, "textures/gui/gui_water_purifier.png");

    protected final T tilePurifier;
    protected final S container;

    //TODO: The gui does not update until you close and re-open it.

    public AbstractGuiWaterPurifier(S inventoryContainer) {
        super(inventoryContainer);
        this.tilePurifier = inventoryContainer.getPurifierTile();
        this.container = inventoryContainer;
    }

    @Override
    public void initGui() {
        super.initGui();
        this.addButton(new GuiButtonGuiButtonConsumer<>(0, this.guiLeft + 65, this.guiTop + 57, this.tilePurifier, AbstractTileWaterPurifier::emptyInputFluidTank));
        this.addButton(new GuiButtonGuiButtonConsumer<>(1, this.guiLeft + 132, this.guiTop + 57, this.tilePurifier, AbstractTileWaterPurifier::emptyOutputFluidTank));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);

        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        List<String> tooltip = new ArrayList<>();
        this.handleGuiTank(this.tilePurifier.getInputFluidTank(), i + 48, j + 16, 13, 50, mouseX, mouseY, tooltip);
        this.handleGuiTank(this.tilePurifier.getOutputFluidTank(), i + 115, j + 16, 13, 50, mouseX, mouseY, tooltip);

        if (!tooltip.isEmpty()) {
            drawHoveringTooltip(mouseX, mouseY, tooltip);
            RenderHelper.enableGUIStandardItemLighting();
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        this.fontRenderer.drawString(this.container.getInventoryPlayer().getDisplayName().getUnformattedText(), 8, this.ySize - 96 + 2, 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        this.mc.getTextureManager().bindTexture(TEXTURE);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);
        this.drawGuiContainerBackgroundElements(partialTicks, mouseX, mouseY);

        int progressScaled = this.getTileProgressScaled(24);
        this.drawTexturedModalRect(i + 77, j + 43, 176, 14, progressScaled + 1, 16);

        this.handleGuiTank(this.tilePurifier.getInputFluidTank(), i + 48, j + 16, 13, 50, mouseX, mouseY, null);
        this.handleGuiTank(this.tilePurifier.getOutputFluidTank(), i + 115, j + 16, 13, 50, mouseX, mouseY, null);
    }

    protected abstract void drawGuiContainerBackgroundElements(float partialTicks, int mouseX, int mouseY);

    public void handleGuiTank(FluidTank tank, int x, int y, int w, int h, int mouseX, int mouseY, List<String> tooltip) {
        this.handleGuiTank(tank.getFluid(), tank.getCapacity(), x, y, w, h, mouseX, mouseY, tooltip);
    }

    public void handleGuiTank(FluidStack fluid, int capacity, int x, int y, int w, int h, int mouseX, int mouseY, List<String> tooltip) {
        if (tooltip == null) {
            if (fluid != null && fluid.getFluid() != null) {
                int fluidHeight = (int) ((float) h * (float) fluid.amount / capacity);
                drawRepeatedFluidSprite(fluid, x, y + h - fluidHeight, w, fluidHeight);
                GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            }
        } else if (mouseX >= x && mouseY >= y && mouseX <= x + w && mouseY <= y + h) {
            addFluidTooltip(fluid, tooltip, capacity);
        }
    }

    public void addFluidTooltip(FluidStack fluid, List<String> tooltip, int tankCapacity) {
        if (fluid != null && fluid.getFluid() != null) {
            tooltip.add(fluid.getFluid().getRarity(fluid).color + fluid.getLocalizedName());
        } else {
            tooltip.add(StringHelper.getTranslatedString("empty", "tooltip"));
        }

        if (tankCapacity > 0) {
            tooltip.add(TextFormatting.GRAY.toString() + (fluid != null ? fluid.amount : 0) + "/" + tankCapacity + "mB");
        } else {
            tooltip.add(TextFormatting.GRAY.toString() + (fluid != null ? fluid.amount : 0) + "mB");
        }

    }

    public void drawRepeatedFluidSprite(FluidStack fluid, float x, float y, float width, float height) {
        this.mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        TextureAtlasSprite sprite = mc.getTextureMapBlocks().getAtlasSprite(fluid.getFluid().getStill(fluid).toString());
        if (sprite != null) {
            int color = fluid.getFluid().getColor(fluid);
            GlStateManager.color((float) (color >> 16 & 255) / 255.0F, (float) (color >> 8 & 255) / 255.0F, (float) (color & 255) / 255.0F, 1.0F);
            int iconWidth = sprite.getIconWidth();
            int iconHeight = sprite.getIconHeight();
            if (iconWidth > 0 && iconHeight > 0) {
                drawRepeatedSprite(x, y, width, height, iconWidth, iconHeight, sprite.getMinU(), sprite.getMaxU(), sprite.getMinV(), sprite.getMaxV());
            }
        }

    }

    public void drawRepeatedSprite(float x, float y, float width, float height, int iconWidth, int iconHeight, float uMin, float uMax, float vMin, float vMax) {
        int iterMaxW = (int) (width / (float) iconWidth);
        int iterMaxH = (int) (height / (float) iconHeight);
        float leftoverW = width % (float) iconWidth;
        float leftoverH = height % (float) iconHeight;
        float leftoverWf = leftoverW / (float) iconWidth;
        float leftoverHf = leftoverH / (float) iconHeight;
        float iconUDif = uMax - uMin;
        float iconVDif = vMax - vMin;

        for (int ww = 0; ww < iterMaxW; ++ww) {
            for (int hh = 0; hh < iterMaxH; ++hh) {
                drawTexturedRect(x + (float) (ww * iconWidth), y + (float) (hh * iconHeight), (float) iconWidth, (float) iconHeight, uMin, uMax, vMin, vMax);
            }

            drawTexturedRect(x + (float) (ww * iconWidth), y + (float) (iterMaxH * iconHeight), (float) iconWidth, leftoverH, uMin, uMax, vMin, vMin + iconVDif * leftoverHf);
        }

        if (leftoverW > 0.0F) {
            for (int hh = 0; hh < iterMaxH; ++hh) {
                drawTexturedRect(x + (float) (iterMaxW * iconWidth), y + (float) (hh * iconHeight), leftoverW, (float) iconHeight, uMin, uMin + iconUDif * leftoverWf, vMin, vMax);
            }

            drawTexturedRect(x + (float) (iterMaxW * iconWidth), y + (float) (iterMaxH * iconHeight), leftoverW, leftoverH, uMin, uMin + iconUDif * leftoverWf, vMin, vMin + iconVDif * leftoverHf);
        }

    }

    public void drawHoveringTooltip(int x, int y, List<String> tooltip) {
        if (!tooltip.isEmpty()) {
            FontRenderer renderer = this.mc.fontRenderer;
            boolean uni = renderer.getUnicodeFlag();
            renderer.setUnicodeFlag(false);

            GlStateManager.disableRescaleNormal();
            RenderHelper.disableStandardItemLighting();
            GlStateManager.disableLighting();
            GlStateManager.disableDepth();

            int k = 0;

            for (String str : tooltip) {
                int l = renderer.getStringWidth(str);
                if (l > k) {
                    k = l;
                }
            }

            int j2 = x + 12;
            int k2 = y - 12;
            int i1 = 8;
            boolean shift = false;
            if (xSize > 0 && j2 + k > xSize) {
                j2 -= 28 + k;
                shift = true;
            }

            if (ySize > 0 && k2 + i1 + 6 > ySize) {
                k2 = ySize - i1 - 6;
                shift = true;
            }

            if (!shift && this.mc.currentScreen != null) {
                if (j2 + k > this.mc.currentScreen.width) {
                    j2 -= 28 + k;
                }

                if (k2 + i1 + 6 > this.mc.currentScreen.height) {
                    k2 = this.mc.currentScreen.height - i1 - 6;
                }
            }

            if (tooltip.size() > 1) {
                i1 += 2 + (tooltip.size() - 1) * 10;
            }

            GlStateManager.translate(0.0F, 0.0F, 300.0F);
            int j1 = -267386864;
            drawGradientRect(j2 - 3, k2 - 4, j2 + k + 3, k2 - 3, j1, j1);
            drawGradientRect(j2 - 3, k2 + i1 + 3, j2 + k + 3, k2 + i1 + 4, j1, j1);
            drawGradientRect(j2 - 3, k2 - 3, j2 + k + 3, k2 + i1 + 3, j1, j1);
            drawGradientRect(j2 - 4, k2 - 3, j2 - 3, k2 + i1 + 3, j1, j1);
            drawGradientRect(j2 + k + 3, k2 - 3, j2 + k + 4, k2 + i1 + 3, j1, j1);
            int k1 = 1347420415;
            int l1 = (k1 & 16711422) >> 1 | k1 & -16777216;
            drawGradientRect(j2 - 3, k2 - 3 + 1, j2 - 3 + 1, k2 + i1 + 3 - 1, k1, l1);
            drawGradientRect(j2 + k + 2, k2 - 3 + 1, j2 + k + 3, k2 + i1 + 3 - 1, k1, l1);
            drawGradientRect(j2 - 3, k2 - 3, j2 + k + 3, k2 - 3 + 1, k1, k1);
            drawGradientRect(j2 - 3, k2 + i1 + 2, j2 + k + 3, k2 + i1 + 3, l1, l1);
            GlStateManager.translate(0.0F, 0.0F, -300.0F);

            for (int i2 = 0; i2 < tooltip.size(); ++i2) {
                String s1 = tooltip.get(i2);
                renderer.drawStringWithShadow(s1, (float) j2, (float) k2, -1);
                if (i2 == 0) {
                    k2 += 2;
                }

                k2 += 10;
            }

            GlStateManager.enableLighting();
            GlStateManager.enableDepth();
            RenderHelper.enableStandardItemLighting();
            GlStateManager.enableRescaleNormal();

            renderer.setUnicodeFlag(uni);
        }

    }

    public void drawTexturedRect(float x, float y, float w, float h, double... uv) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buff = tessellator.getBuffer();
        buff.begin(7, DefaultVertexFormats.POSITION_TEX);
        buff.pos(x, (y + h), 0.0F).tex(uv[0], uv[3]).endVertex();
        buff.pos((x + w), (y + h), 0.0F).tex(uv[1], uv[3]).endVertex();
        buff.pos((x + w), y, 0.0F).tex(uv[1], uv[2]).endVertex();
        buff.pos(x, y, 0.0F).tex(uv[0], uv[2]).endVertex();
        tessellator.draw();
    }

    protected int getTileProgressScaled(int pixels) {
        int currProgress = this.tilePurifier.getProgress();
        int maxProgress = this.tilePurifier.getMaxProgress();
        return maxProgress != 0 && currProgress != 0 ? pixels * currProgress / maxProgress : 0;
    }
}
