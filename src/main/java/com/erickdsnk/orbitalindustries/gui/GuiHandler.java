package com.erickdsnk.orbitalindustries.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import com.erickdsnk.orbitalindustries.block.rocket.RocketAssemblerBlock;
import com.erickdsnk.orbitalindustries.container.rocket.RocketAssemblerContainer;
import com.erickdsnk.orbitalindustries.gui.rocket.RocketAssemblerGUI;
import com.erickdsnk.orbitalindustries.tile.rocket.RocketAssemblerTileEntity;

import cpw.mods.fml.common.network.IGuiHandler;

/**
 * FML GUI handler. Returns server container and client GUI for mod GUIs.
 */
public class GuiHandler implements IGuiHandler {

    @Override
    public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        if (id == RocketAssemblerBlock.GUI_ID) {
            RocketAssemblerTileEntity tile = (RocketAssemblerTileEntity) world.getTileEntity(x, y, z);
            if (tile != null)
                return new RocketAssemblerContainer(player.inventory, tile);
        }
        return null;
    }

    @Override
    public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        if (id == RocketAssemblerBlock.GUI_ID) {
            RocketAssemblerTileEntity tile = (RocketAssemblerTileEntity) world.getTileEntity(x, y, z);
            if (tile != null) {
                RocketAssemblerContainer container = new RocketAssemblerContainer(player.inventory, tile);
                return new RocketAssemblerGUI(container);
            }
        }
        return null;
    }
}
