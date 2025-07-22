package com.invadermonky.survivaltools.compat.waterskin;

import com.invadermonky.survivaltools.api.IProxy;
import com.invadermonky.survivaltools.items.ItemCanteen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

public class WaterskinTaNST implements IProxy {
    @Override
    public void preInitClient() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void afterTooltip(RenderTooltipEvent.PostText event) {
        ItemStack stack = event.getStack();
        if (!stack.isEmpty() && stack.getItem() instanceof ItemCanteen) {
            int thirst = ((ItemCanteen) stack.getItem()).getRestoredThirst();
            float hydration = ((ItemCanteen) stack.getItem()).getRestoredHydration();

            Minecraft mc = Minecraft.getMinecraft();
            mc.getTextureManager().bindTexture(new ResourceLocation("waterskin", "textures/gui/overlay/thirst_overlay.png"));
            GuiScreen gui = mc.currentScreen;
            ScaledResolution res = new ScaledResolution(mc);
            hydration = Math.min(2.0F * hydration * (float) thirst, 20.0F);

            float hydrationRemaining = 2.0F * (float) ((int) hydration % 2);
            int lengthThirst = thirst + 1 >> 1 << 3;
            int lengthHydration = 2 + (int) (((double) hydration + 1.99) / (double) 2.0F) * 6;
            int length = Math.max(lengthThirst, lengthHydration);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            int baseX = event.getX();
            int baseY = event.getY() + event.getHeight();
            if (baseY + 29 >= res.getScaledHeight()) {
                baseY = event.getY() - 33;
            }

            gui.drawTexturedModalRect(baseX - 2, baseY + 3, 0, 42, length + 4, 26);
            gui.drawTexturedModalRect(baseX + length + 2, baseY + 3, 88, 42, 3, 26);

            for (int i = 0; i * 2 < thirst; ++i) {
                if (thirst - i * 2 == 1) {
                    gui.drawTexturedModalRect(baseX + 2 + i * 8, baseY + 7, 46, 0, 7, 10);
                    break;
                }

                gui.drawTexturedModalRect(baseX + 2 + i * 8, baseY + 7, 37, 0, 7, 10);
            }

            for (int i = 0; (float) (i * 2) < hydration; ++i) {
                if (hydration - (float) (i * 2) < 2.0F) {
                    gui.drawTexturedModalRect(baseX + 2 + i * 6, baseY + 17, (int) (hydrationRemaining - 1.0F) * 7, 27, 6, 7);
                    break;
                }

                gui.drawTexturedModalRect(baseX + 2 + i * 6, baseY + 17, 21, 27, 6, 7);
            }

        }
    }
}
