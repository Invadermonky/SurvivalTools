package com.invadermonky.survivaltools.client.render;

import com.invadermonky.survivaltools.tile.TileOpenBarrel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;

@SideOnly(Side.CLIENT)
public class RenderOpenBarrel extends TileEntitySpecialRenderer<TileOpenBarrel> {
    @Override
    public void render(@NotNull TileOpenBarrel te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if (te != null) {
            FluidStack fluidStack = te.getContainedFluid();
            if (fluidStack != null) {
                GlStateManager.pushMatrix();
                this.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
                this.renderFluid(te.getFluidHeight(), fluidStack.getFluid(), x, y, z);
                GlStateManager.popMatrix();
            }
        }
    }

    public void renderFluid(float maxHeight, Fluid renderFluid, double x, double y, double z) {
        maxHeight *= 0.71875f;
        GlStateManager.translate(x, y, z);
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.disableBlend();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        TextureAtlasSprite fluidSprite = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(renderFluid.getStill().toString());
        fluidSprite = fluidSprite == null ? Minecraft.getMinecraft().getTextureMapBlocks().getMissingSprite() : fluidSprite;
        buffer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
        int rgbaColor = renderFluid.getColor();
        int rColor = rgbaColor >> 16 & 255;
        int gColor = rgbaColor >> 8 & 255;
        int bColor = rgbaColor & 255;
        int aColor = rgbaColor >> 24 & 255;
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);
        GlStateManager.color((float) rColor, (float) gColor, (float) bColor, (float) aColor);
        float u1 = fluidSprite.getMinU();
        float v1 = fluidSprite.getMinV();
        float u2 = fluidSprite.getMaxU();
        float v2 = fluidSprite.getMaxV();
        if (maxHeight > 0.0F) {
            float texWidth = u2 - u1;
            buffer.pos(0.01, (double) maxHeight + 0.25, 0.01).tex((double) u1 + 0.75 * (double) texWidth, (double) v1 + ((double) maxHeight + 0.05) * (double) texWidth).color(rColor, gColor, bColor, aColor).endVertex();
            buffer.pos(0.01, (double) maxHeight + 0.25, 0.99).tex((double) u1 + 0.75 * (double) texWidth, v1).color(rColor, gColor, bColor, aColor).endVertex();
            buffer.pos(0.99, (double) maxHeight + 0.25, 0.99).tex((double) u1 + 0.25 * (double) texWidth, v1).color(rColor, gColor, bColor, aColor).endVertex();
            buffer.pos(0.99, (double) maxHeight + 0.25, 0.01).tex((double) u1 + 0.25 * (double) texWidth, (double) v1 + ((double) maxHeight + 0.05) * (double) texWidth).color(rColor, gColor, bColor, aColor).endVertex();

            buffer.pos(0.99, (double) maxHeight + 0.25, 0.01).tex((double) u1 + 0.75 * (double) texWidth, (double) v1 + ((double) maxHeight + 0.05) * (double) texWidth).color(rColor, gColor, bColor, aColor).endVertex();
            buffer.pos(0.99, 0.25, 0.01).tex((double) u1 + 0.75 * (double) texWidth, v1).color(rColor, gColor, bColor, aColor).endVertex();
            buffer.pos(0, 0.25, 0.01).tex((double) u1 + 0.25 * (double) texWidth, v1).color(rColor, gColor, bColor, aColor).endVertex();
            buffer.pos(0, (double) maxHeight + 0.25, 0.01).tex((double) u1 + 0.25 * (double) texWidth, (double) v1 + ((double) maxHeight + 0.05) * (double) texWidth).color(rColor, gColor, bColor, aColor).endVertex();

            buffer.pos(0.01, 0.25, 0.99).tex((double) u1 + 0.75 * (double) texWidth, (double) v1 + ((double) maxHeight + 0.05) * (double) texWidth).color(rColor, gColor, bColor, aColor).endVertex();
            buffer.pos(0.01, (double) maxHeight + 0.25, 0.99).tex((double) u1 + 0.75 * (double) texWidth, v1).color(rColor, gColor, bColor, aColor).endVertex();
            buffer.pos(0.01, (double) maxHeight + 0.25, 0.01).tex((double) u1 + 0.25 * (double) texWidth, v1).color(rColor, gColor, bColor, aColor).endVertex();
            buffer.pos(0.01, 0.25, 0.01).tex((double) u1 + 0.25 * (double) texWidth, (double) v1 + ((double) maxHeight + 0.05) * (double) texWidth).color(rColor, gColor, bColor, aColor).endVertex();

            buffer.pos(0.99, 0.25, 0.99).tex((double) u1 + 0.75 * (double) texWidth, (double) v1 + ((double) maxHeight + 0.05) * (double) texWidth).color(rColor, gColor, bColor, aColor).endVertex();
            buffer.pos(0.99, (double) maxHeight + 0.25, 0.99).tex((double) u1 + 0.75 * (double) texWidth, v1).color(rColor, gColor, bColor, aColor).endVertex();
            buffer.pos(0.01, (double) maxHeight + 0.25, 0.99).tex((double) u1 + 0.25 * (double) texWidth, v1).color(rColor, gColor, bColor, aColor).endVertex();
            buffer.pos(0.01, 0.25, 0.99).tex((double) u1 + 0.25 * (double) texWidth, (double) v1 + ((double) maxHeight + 0.05) * (double) texWidth).color(rColor, gColor, bColor, aColor).endVertex();

            buffer.pos(0.99, (double) maxHeight + 0.25, 0.99).tex((double) u1 + 0.75 * (double) texWidth, (double) v1 + ((double) maxHeight + 0.05) * (double) texWidth).color(rColor, gColor, bColor, aColor).endVertex();
            buffer.pos(0.99, 0.25, 0.99).tex((double) u1 + 0.75 * (double) texWidth, v1).color(rColor, gColor, bColor, aColor).endVertex();
            buffer.pos(0.99, 0.25, 0.01).tex((double) u1 + 0.25 * (double) texWidth, v1).color(rColor, gColor, bColor, aColor).endVertex();
            buffer.pos(0.99, (double) maxHeight + 0.25, 0.01).tex((double) u1 + 0.25 * (double) texWidth, (double) v1 + ((double) maxHeight + 0.05) * (double) texWidth).color(rColor, gColor, bColor, aColor).endVertex();
        }
        tessellator.draw();
        RenderHelper.enableStandardItemLighting();
    }
}
