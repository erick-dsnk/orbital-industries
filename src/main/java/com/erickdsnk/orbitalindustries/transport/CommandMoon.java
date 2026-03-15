package com.erickdsnk.orbitalindustries.transport;

import java.util.Collections;
import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.WorldServer;

import com.erickdsnk.orbitalindustries.OrbitalIndustriesAPI;
import com.erickdsnk.orbitalindustries.core.ConfigManager;
import com.erickdsnk.orbitalindustries.core.OIModLogger;
import com.erickdsnk.orbitalindustries.util.PositionUtils;

import cpw.mods.fml.common.FMLCommonHandler;

/**
 * Debug command /moon: teleports the commanding player to the moon dimension.
 * Uses TeleportManager and PositionUtils for safe spawn.
 */
public class CommandMoon extends CommandBase {

    private static final OIModLogger LOG = new OIModLogger("CommandMoon");

    @Override
    public String getCommandName() {
        return "moon";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/moon";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        EntityPlayerMP player = getCommandSenderAsPlayer(sender);
        if (player == null) {
            return;
        }
        int dimensionId = ConfigManager.getMoonDimensionId();
        net.minecraft.server.MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        if (server == null) {
            return;
        }
        WorldServer targetWorld = server.worldServerForDimension(dimensionId);
        if (targetWorld == null) {
            return;
        }

        int x = (int) player.posX;
        int z = (int) player.posZ;
        int y = PositionUtils.findSafeSpawnY(targetWorld, x, z);

        OrbitalIndustriesAPI.teleportManager.teleportToDimension(player, dimensionId, x, y, z);
        LOG.info("Player " + player.getCommandSenderName() + " teleported to moon dimension");
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return sender instanceof EntityPlayerMP;
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args) {
        return Collections.emptyList();
    }
}
