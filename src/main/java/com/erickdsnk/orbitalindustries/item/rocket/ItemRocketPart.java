package com.erickdsnk.orbitalindustries.item.rocket;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;

import com.erickdsnk.orbitalindustries.registry.CreativeTabOI;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;

/**
 * Item that represents a data-driven rocket part. Part identity is stored in
 * NBT
 * ("PartId") so modpack-defined parts work without new item types.
 */
public class ItemRocketPart extends Item {

    public static final String NBT_PART_ID = "PartId";

    private static final String ICON_SMALL_FUEL_TANK = "orbitalindustries:small-fuel-tank";
    private static final String ICON_MEDIUM_FUEL_TANK = "orbitalindustries:medium-fuel-tank";

    @SideOnly(Side.CLIENT)
    private IIcon iconSmallFuelTank;
    @SideOnly(Side.CLIENT)
    private IIcon iconMediumFuelTank;

    public ItemRocketPart() {
        setCreativeTab(CreativeTabOI.TAB);
        setMaxStackSize(1);
        setUnlocalizedName("orbitalindustries.rocket_part");
    }

    public static String getPartId(ItemStack stack) {
        if (stack == null || !stack.hasTagCompound())
            return null;
        return stack.getTagCompound().getString(NBT_PART_ID);
    }

    public static void setPartId(ItemStack stack, String partId) {
        if (stack == null)
            return;
        if (stack.getTagCompound() == null)
            stack.setTagCompound(new NBTTagCompound());
        stack.getTagCompound().setString(NBT_PART_ID, partId != null ? partId : "");
    }

    /**
     * Per-part display names via
     * {@code item.orbitalindustries.rocket_part.<PartId>.name}
     * in lang files.
     */
    @Override
    public String getUnlocalizedName(ItemStack stack) {
        String id = getPartId(stack);
        if (id != null && !id.isEmpty()) {
            return "item.orbitalindustries.rocket_part." + id;
        }
        return super.getUnlocalizedName();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister reg) {
        iconSmallFuelTank = reg.registerIcon(ICON_SMALL_FUEL_TANK);
        iconMediumFuelTank = reg.registerIcon(ICON_MEDIUM_FUEL_TANK);
        itemIcon = iconSmallFuelTank;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(ItemStack stack, int renderPass) {
        String id = getPartId(stack);
        if ("medium_fuel_tank".equals(id)) {
            return iconMediumFuelTank;
        }
        if ("small_fuel_tank".equals(id)) {
            return iconSmallFuelTank;
        }
        return itemIcon;
    }
}
