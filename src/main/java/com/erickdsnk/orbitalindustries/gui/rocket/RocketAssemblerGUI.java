package com.erickdsnk.orbitalindustries.gui.rocket;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import com.erickdsnk.orbitalindustries.container.rocket.RocketAssemblerContainer;
import com.erickdsnk.orbitalindustries.network.AssembleRocketPacket;
import com.erickdsnk.orbitalindustries.network.PacketHandler;
import com.erickdsnk.orbitalindustries.rocket.RocketBlueprint;
import com.erickdsnk.orbitalindustries.rocket.RocketStats;
import com.erickdsnk.orbitalindustries.tile.rocket.RocketAssemblerTileEntity;

/**
 * GUI for the Rocket Assembler: part slots, blueprint stats, Assemble button.
 */
public class RocketAssemblerGUI extends GuiContainer {

    private static final ResourceLocation TEXTURE = new ResourceLocation("orbitalindustries",
            "textures/gui/rocket_assembler.png");

    private static final int BUTTON_ASSEMBLE = 0;

    private final RocketAssemblerContainer container;

    public RocketAssemblerGUI(Container container) {
        super(container);
        this.container = (RocketAssemblerContainer) container;
        xSize = 176;
        ySize = 166;
    }

    @Override
    public void initGui() {
        super.initGui();
        int x = guiLeft + (xSize - 80) / 2;
        int y = guiTop + 70;
        buttonList.add(new GuiButton(BUTTON_ASSEMBLE, x, y, 80, 20, "Assemble"));
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == BUTTON_ASSEMBLE) {
            RocketAssemblerTileEntity tile = container.getTile();
            PacketHandler.CHANNEL.sendToServer(new AssembleRocketPacket(tile.xCoord, tile.yCoord, tile.zCoord));
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partial, int mouseX, int mouseY) {
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        mc.getTextureManager().bindTexture(TEXTURE);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        fontRendererObj.drawString("Rocket Assembler", 8, 6, 0x404040);
        RocketBlueprint blueprint = container.getTile().getBlueprint();
        if (blueprint != null) {
            RocketStats stats = blueprint.getStats();
            int y = 38;
            fontRendererObj.drawString("Thrust: " + String.format("%.0f", stats.getThrust()), 8, y, 0x404040);
            y += 10;
            fontRendererObj.drawString("Fuel: " + String.format("%.0f", stats.getFuelCapacity()), 8, y, 0x404040);
            y += 10;
            fontRendererObj.drawString("Mass: " + String.format("%.0f", stats.getMass()), 8, y, 0x404040);
            y += 10;
            fontRendererObj.drawString("Nav Tier: " + stats.getNavigationTier(), 8, y, 0x404040);
            y += 10;
            fontRendererObj.drawString("Range: " + String.format("%.0f", stats.getMaxRange()), 8, y, 0x404040);
        }
    }
}
