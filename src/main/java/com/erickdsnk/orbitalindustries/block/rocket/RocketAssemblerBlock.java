package com.erickdsnk.orbitalindustries.block.rocket;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import com.erickdsnk.orbitalindustries.OrbitalIndustries;
import com.erickdsnk.orbitalindustries.registry.CreativeTabOI;
import com.erickdsnk.orbitalindustries.tile.rocket.RocketAssemblerTileEntity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Block for the Rocket Assembler. Opens GUI on right-click; tile entity holds
 * part slots and output. Uses separate textures: control panel (front), gear
 * (sides), and metallic top/bottom.
 */
public class RocketAssemblerBlock extends BlockContainer {

    public static final int GUI_ID = 0;

    private IIcon iconFront;
    private IIcon iconSide;
    private IIcon iconTop;
    private IIcon iconBottom;

    public RocketAssemblerBlock() {
        super(Material.iron);
        setBlockName("orbitalindustries.rocket_assembler");
        setCreativeTab(CreativeTabOI.TAB);
        setHardness(3.0f);
        setResistance(10.0f);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister reg) {
        iconFront = reg.registerIcon("orbitalindustries:rocket_assembler_front");
        iconSide = reg.registerIcon("orbitalindustries:rocket_assembler_side");
        iconTop = reg.registerIcon("orbitalindustries:rocket_assembler_top");
        iconBottom = reg.registerIcon("orbitalindustries:rocket_assembler_bottom");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int metadata) {
        switch (side) {
            case 0:
                return iconBottom;
            case 1:
                return iconTop;
            case 3:
                return iconFront;
            default:
                return iconSide;
        }
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
