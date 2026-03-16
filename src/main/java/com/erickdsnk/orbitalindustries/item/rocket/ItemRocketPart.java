package com.erickdsnk.orbitalindustries.item.rocket;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import com.erickdsnk.orbitalindustries.registry.CreativeTabOI;

/**
 * Item that represents a data-driven rocket part. Part identity is stored in
 * NBT
 * ("PartId") so modpack-defined parts work without new item types.
 */
public class ItemRocketPart extends Item {

    public static final String NBT_PART_ID = "PartId";

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
}
