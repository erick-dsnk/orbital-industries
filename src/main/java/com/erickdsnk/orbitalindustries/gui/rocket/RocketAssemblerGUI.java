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
 * GUI for the Rocket Assembler. Left: engine, guidance, hull, payload (column
 * 1)
 * and fuel x5 (column 2). Right: stats preview and Assemble button.
 */
public class RocketAssemblerGUI extends GuiContainer {

    private static final ResourceLocation TEXTURE = new ResourceLocation("orbitalindustries",
            "textures/gui/rocket_assembler.png");

    private static final int TEXTURE_WIDTH = 256;
    private static final int TEXTURE_HEIGHT = 256;
    /**
     * Left margin on the 256px texture where the content area starts (for alignment
     * with slot positions).
     */
    private static final int GUI_LEFT_MARGIN = 40;
    private static final int STATS_X = 105 + GUI_LEFT_MARGIN;
    private static final int STATS_Y = 35;
    /** Scale for stats text (1.0 = normal, 0.75 = 75% size, 0.5 = half). */
    private static final float STATS_TEXT_SCALE = 0.75f;
    private static final int STATS_LINE_HEIGHT = (int) (10 / STATS_TEXT_SCALE);

    private final RocketAssemblerContainer container;

    public RocketAssemblerGUI(Container container) {
        super(container);
        this.container = (RocketAssemblerContainer) container;
        xSize = TEXTURE_WIDTH;
        ySize = TEXTURE_HEIGHT;
    }

    @Override
    public void initGui() {
        super.initGui();
        // buttonList.add(new GuiButton(BUTTON_ASSEMBLE, guiLeft + BUTTON_X, guiTop +
        // BUTTON_Y, 70, 20, "Assemble"));
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        // if (button.id == BUTTON_ASSEMBLE) {
        // RocketAssemblerTileEntity tile = container.getTile();
        // PacketHandler.CHANNEL.sendToServer(new AssembleRocketPacket(tile.xCoord,
        // tile.yCoord, tile.zCoord));
        // }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partial, int mouseX, int mouseY) {
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        mc.getTextureManager().bindTexture(TEXTURE);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, TEXTURE_WIDTH, TEXTURE_HEIGHT);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        fontRendererObj.drawString("Rocket Assembler", 8 + GUI_LEFT_MARGIN, 12, 0x404040);

        RocketBlueprint blueprint = container.getTile().getBlueprint();

        // Draw stats at reduced scale so text is smaller
        GL11.glPushMatrix();
        GL11.glScalef(STATS_TEXT_SCALE, STATS_TEXT_SCALE, 1.0f);
        int sx = (int) (STATS_X / STATS_TEXT_SCALE);
        int y = (int) (STATS_Y / STATS_TEXT_SCALE);
        if (blueprint != null) {
            RocketStats stats = blueprint.getStats();
            fontRendererObj.drawString("Thrust: " + String.format("%.0f", stats.getThrust()), sx, y, 0x404040);
            y += STATS_LINE_HEIGHT;
            fontRendererObj.drawString("Fuel: " + String.format("%.0f", stats.getFuelCapacity()), sx, y, 0x404040);
            y += STATS_LINE_HEIGHT;
            fontRendererObj.drawString("Mass: " + String.format("%.0f", stats.getMass()), sx, y, 0x404040);
            y += STATS_LINE_HEIGHT;
            fontRendererObj.drawString("Nav Tier: " + stats.getNavigationTier(), sx, y, 0x404040);
            y += STATS_LINE_HEIGHT;
            fontRendererObj.drawString("Range: " + String.format("%.0f", stats.getMaxRange()), sx, y, 0x404040);
        } else {
            fontRendererObj.drawString("Thrust: None", sx, y, 0x404040);
            y += STATS_LINE_HEIGHT;
            fontRendererObj.drawString("Fuel: None", sx, y, 0x404040);
            y += STATS_LINE_HEIGHT;
            fontRendererObj.drawString("Mass: None", sx, y, 0x404040);
            y += STATS_LINE_HEIGHT;
            fontRendererObj.drawString("Nav Tier: None", sx, y, 0x404040);
            y += STATS_LINE_HEIGHT;
            fontRendererObj.drawString("Range: None", sx, y, 0x404040);
        }
        GL11.glPopMatrix();
    }
}
