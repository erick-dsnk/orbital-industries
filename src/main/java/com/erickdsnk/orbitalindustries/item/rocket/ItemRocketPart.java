package com.erickdsnk.orbitalindustries.item.rocket;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;

import com.erickdsnk.orbitalindustries.OrbitalIndustriesAPI;
import com.erickdsnk.orbitalindustries.registry.CreativeTabOI;
import com.erickdsnk.orbitalindustries.rocket.RocketPartDefinition;
import com.erickdsnk.orbitalindustries.rocket.RocketPartRegistry;
import com.erickdsnk.orbitalindustries.rocket.RocketPartType;

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
    @SideOnly(Side.CLIENT)
    private IIcon iconGeneric;
    @SideOnly(Side.CLIENT)
    private IIcon iconEngine;
    @SideOnly(Side.CLIENT)
    private IIcon iconGuidance;
    @SideOnly(Side.CLIENT)
    private IIcon iconHull;
    @SideOnly(Side.CLIENT)
    private IIcon iconPayload;
    /** Indexed by {@link RocketPartRegistry#getSubtypeMetaForPartId(String)}. */
    @SideOnly(Side.CLIENT)
    private IIcon[] iconsBySubtype;

    public ItemRocketPart() {
        setCreativeTab(CreativeTabOI.TAB);
        setMaxStackSize(1);
        setUnlocalizedName("orbitalindustries.rocket_part");
        setHasSubtypes(true);
    }

    public static String getPartId(ItemStack stack) {
        if (stack == null || !stack.hasTagCompound())
            return null;
        return stack.getTagCompound().getString(NBT_PART_ID);
    }

    /** NBT part id, or registry id inferred from item damage (subtype meta). */
    public static String getEffectivePartId(ItemStack stack) {
        String id = getPartId(stack);
        if (id != null && !id.isEmpty()) {
            return id;
        }
        RocketPartRegistry reg = OrbitalIndustriesAPI.rocketPartRegistry;
        if (reg != null) {
            return reg.getPartIdForSubtypeMeta(stack.getItemDamage());
        }
        return null;
    }

    public static void setPartId(ItemStack stack, String partId) {
        if (stack == null)
            return;
        if (stack.getTagCompound() == null)
            stack.setTagCompound(new NBTTagCompound());
        stack.getTagCompound().setString(NBT_PART_ID, partId != null ? partId : "");
        RocketPartRegistry reg = OrbitalIndustriesAPI.rocketPartRegistry;
        if (partId != null && !partId.isEmpty() && reg != null) {
            stack.setItemDamage(reg.getSubtypeMetaForPartId(partId));
        } else {
            stack.setItemDamage(0);
        }
    }

    /**
     * Per-part display names via
     * {@code item.orbitalindustries.rocket_part.<PartId>.name}
     * in lang files.
     */
    @Override
    public String getUnlocalizedName(ItemStack stack) {
        String id = getEffectivePartId(stack);
        if (id != null && !id.isEmpty()) {
            return "item.orbitalindustries.rocket_part." + id;
        }
        return super.getUnlocalizedName();
    }

    /**
     * One creative / NEI entry per loaded {@link RocketPartDefinition} (NBT
     * PartId).
     */
    @Override
    @SideOnly(Side.CLIENT)
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void getSubItems(Item item, CreativeTabs tab, List list) {
        RocketPartRegistry reg = OrbitalIndustriesAPI.rocketPartRegistry;
        if (reg != null && !reg.getAllDefinitions().isEmpty()) {
            for (RocketPartDefinition def : reg.getAllDefinitions()) {
                if (def == null || def.getId() == null || def.getId().isEmpty()) {
                    continue;
                }
                int dmg = reg.getSubtypeMetaForPartId(def.getId());
                ItemStack stack = new ItemStack(item, 1, dmg);
                setPartId(stack, def.getId());
                list.add(stack);
            }
            return;
        }
        list.add(new ItemStack(item, 1, 0));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister reg) {
        iconSmallFuelTank = reg.registerIcon(ICON_SMALL_FUEL_TANK);
        iconMediumFuelTank = reg.registerIcon(ICON_MEDIUM_FUEL_TANK);
        iconGeneric = reg.registerIcon("orbitalindustries:rocket_part");
        iconEngine = reg.registerIcon("orbitalindustries:rocket_part_engine");
        iconGuidance = reg.registerIcon("orbitalindustries:rocket_part_guidance");
        iconHull = reg.registerIcon("orbitalindustries:rocket_part_hull");
        iconPayload = reg.registerIcon("orbitalindustries:rocket_part_payload");
        itemIcon = iconGeneric;

        RocketPartRegistry apiReg = OrbitalIndustriesAPI.rocketPartRegistry;
        if (apiReg != null && !apiReg.getOrderedPartIds().isEmpty()) {
            List<String> order = apiReg.getOrderedPartIds();
            iconsBySubtype = new IIcon[order.size()];
            for (int i = 0; i < order.size(); i++) {
                String id = order.get(i);
                RocketPartDefinition def = apiReg.get(id);
                iconsBySubtype[i] = resolvePartIcon(reg, def, id);
            }
        } else {
            iconsBySubtype = new IIcon[] { itemIcon };
        }
    }

    @SideOnly(Side.CLIENT)
    private IIcon resolvePartIcon(IIconRegister reg, RocketPartDefinition def, String partId) {
        if (def != null && def.getIcon() != null && !def.getIcon().isEmpty()) {
            return reg.registerIcon(def.getIcon());
        }
        if ("medium_fuel_tank".equals(partId)) {
            return iconMediumFuelTank;
        }
        if ("small_fuel_tank".equals(partId)) {
            return iconSmallFuelTank;
        }
        if (def != null && def.getType() != null) {
            switch (def.getType()) {
                case ENGINE:
                    return iconEngine;
                case GUIDANCE:
                    return iconGuidance;
                case HULL:
                    return iconHull;
                case PAYLOAD:
                    return iconPayload;
                case FUEL_TANK:
                default:
                    return iconGeneric;
            }
        }
        return iconGeneric;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int damage) {
        if (iconsBySubtype != null && damage >= 0 && damage < iconsBySubtype.length) {
            IIcon ic = iconsBySubtype[damage];
            if (ic != null) {
                return ic;
            }
        }
        return itemIcon;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(ItemStack stack, int renderPass) {
        String id = getEffectivePartId(stack);
        if (id != null && !id.isEmpty()) {
            RocketPartRegistry reg = OrbitalIndustriesAPI.rocketPartRegistry;
            if (reg != null) {
                return getIconFromDamage(reg.getSubtypeMetaForPartId(id));
            }
        }
        return getIconFromDamage(stack.getItemDamage());
    }
}
