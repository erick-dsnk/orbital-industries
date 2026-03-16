package com.erickdsnk.orbitalindustries.block.rocket;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import com.erickdsnk.orbitalindustries.OrbitalIndustries;
import com.erickdsnk.orbitalindustries.registry.CreativeTabOI;
import com.erickdsnk.orbitalindustries.tile.rocket.RocketAssemblerTileEntity;

/**
 * Block for the Rocket Assembler. Opens GUI on right-click; tile entity holds
 * part slots and output.
 */
public class RocketAssemblerBlock extends BlockContainer {

    public static final int GUI_ID = 0;

    public RocketAssemblerBlock() {
        super(Material.iron);
        setBlockName("orbitalindustries.rocket_assembler");
        setBlockTextureName("orbitalindustries:rocket_assembler");
        setCreativeTab(CreativeTabOI.TAB);
        setHardness(3.0f);
        setResistance(10.0f);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int metadata) {
        return new RocketAssemblerTileEntity();
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX,
            float hitY, float hitZ) {
        if (world.isRemote)
            return true;
        player.openGui(OrbitalIndustries.instance, GUI_ID, world, x, y, z);
        return true;
    }
}
