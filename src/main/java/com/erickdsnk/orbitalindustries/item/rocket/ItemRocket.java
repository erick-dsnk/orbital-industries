package com.erickdsnk.orbitalindustries.item.rocket;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;

import com.erickdsnk.orbitalindustries.OrbitalIndustriesAPI;
import com.erickdsnk.orbitalindustries.rocket.RocketAssemblerMultiblock;
import com.erickdsnk.orbitalindustries.rocket.RocketBlueprint;
import com.erickdsnk.orbitalindustries.rocket.RocketPart;
import com.erickdsnk.orbitalindustries.rocket.RocketPartRegistry;
import com.erickdsnk.orbitalindustries.registry.CreativeTabOI;

/**
 * Item representing an assembled rocket. NBT stores part ids ("Parts" list);
 * blueprint can be recomputed from registry when needed.
 */
public class ItemRocket extends Item {

    public static final String NBT_PARTS = "Parts";

    public ItemRocket() {
        setCreativeTab(CreativeTabOI.TAB);
        setMaxStackSize(1);
        setUnlocalizedName("orbitalindustries.rocket");
    }

    public static List<String> getPartIds(ItemStack stack) {
        if (stack == null || !stack.hasTagCompound()) {
            return Collections.emptyList();
        }
        NBTTagList list = stack.getTagCompound().getTagList(NBT_PARTS, 8); // 8 = string
        List<String> out = new ArrayList<String>(list.tagCount());
        for (int i = 0; i < list.tagCount(); i++) {
            out.add(list.getStringTagAt(i));
        }
        return out;
    }

    public static void setPartIds(ItemStack stack, List<String> partIds) {
        if (stack == null)
            return;
        if (stack.getTagCompound() == null)
            stack.setTagCompound(new NBTTagCompound());
        NBTTagList list = new NBTTagList();
        if (partIds != null) {
            for (String id : partIds) {
                list.appendTag(new NBTTagString(id != null ? id : ""));
            }
        }
        stack.getTagCompound().setTag(NBT_PARTS, list);
    }

    /**
     * Rebuild blueprint from part ids using registry; returns null if registry
     * unavailable or no valid parts.
     */
    public static RocketBlueprint getBlueprint(ItemStack stack) {
        List<String> ids = getPartIds(stack);
        if (ids.isEmpty())
            return null;
        RocketPartRegistry registry = OrbitalIndustriesAPI.rocketPartRegistry;
        if (registry == null)
            return null;
        List<RocketPart> parts = registry.getPartsByIds(ids);
        if (parts.isEmpty())
            return null;
        RocketAssemblerMultiblock assembler = new RocketAssemblerMultiblock();
        return assembler.buildFromParts(parts);
    }
}
