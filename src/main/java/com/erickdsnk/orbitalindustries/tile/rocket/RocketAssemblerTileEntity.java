package com.erickdsnk.orbitalindustries.tile.rocket;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;

import com.erickdsnk.orbitalindustries.OrbitalIndustriesAPI;
import com.erickdsnk.orbitalindustries.item.rocket.ItemRocketPart;
import com.erickdsnk.orbitalindustries.rocket.RocketBlueprint;
import com.erickdsnk.orbitalindustries.rocket.RocketPart;
import com.erickdsnk.orbitalindustries.rocket.RocketAssemblerMultiblock;
import com.erickdsnk.orbitalindustries.rocket.RocketPartRegistry;
import com.erickdsnk.orbitalindustries.rocket.RocketPartType;

/**
 * Tile entity for the Rocket Assembler block. IInventory with 10 slots:
 * 0=engine,
 * 1=guidance, 2=hull, 3=payload, 4-8=fuel, 9=output. Rebuilds blueprint from
 * part
 * slots on change; stores blueprint and part ids for assemble.
 */
public class RocketAssemblerTileEntity extends TileEntity implements IInventory {

    private static final int SLOT_ENGINE = 0;
    private static final int SLOT_GUIDANCE = 1;
    private static final int SLOT_HULL = 2;
    private static final int SLOT_PAYLOAD = 3;
    private static final int SLOT_FUEL_START = 4;
    private static final int SLOT_FUEL_COUNT = 5;
    private static final int SLOT_OUTPUT = 9;
    private static final int INV_SIZE = 10;

    private final ItemStack[] slots = new ItemStack[INV_SIZE];
    private RocketBlueprint blueprint;
    private List<String> partIds = new ArrayList<String>();

    @Override
    public int getSizeInventory() {
        return INV_SIZE;
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        return index >= 0 && index < INV_SIZE ? slots[index] : null;
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        if (index < 0 || index >= INV_SIZE || slots[index] == null)
            return null;
        ItemStack stack = slots[index];
        if (stack.stackSize <= count) {
            slots[index] = null;
            markDirty();
            return stack;
        }
        ItemStack split = stack.splitStack(count);
        if (stack.stackSize == 0)
            slots[index] = null;
        markDirty();
        return split;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int index) {
        if (index < 0 || index >= INV_SIZE)
            return null;
        ItemStack s = slots[index];
        slots[index] = null;
        return s;
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        if (index < 0 || index >= INV_SIZE)
            return;
        slots[index] = stack;
        if (stack != null && stack.stackSize > getInventoryStackLimit())
            stack.stackSize = getInventoryStackLimit();
        markDirty();
    }

    @Override
    public String getInventoryName() {
        return "container.orbitalindustries.rocket_assembler";
    }

    @Override
    public boolean hasCustomInventoryName() {
        return false;
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return worldObj.getTileEntity(xCoord, yCoord, zCoord) == this
                && player.getDistanceSq(xCoord + 0.5, yCoord + 0.5, zCoord + 0.5) <= 64;
    }

    @Override
    public void openInventory() {
    }

    @Override
    public void closeInventory() {
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        if (index == SLOT_OUTPUT)
            return false;
        if (stack == null || !(stack.getItem() instanceof ItemRocketPart))
            return false;
        RocketPartRegistry registry = OrbitalIndustriesAPI.rocketPartRegistry;
        if (registry == null)
            return false;
        RocketPart part = registry.getPartFromItem(stack);
        if (part == null)
            return false;
        RocketPartType required = getRequiredTypeForSlot(index);
        return part.getType() == required;
    }

    private static RocketPartType getRequiredTypeForSlot(int index) {
        switch (index) {
            case SLOT_ENGINE:
                return RocketPartType.ENGINE;
            case SLOT_GUIDANCE:
                return RocketPartType.GUIDANCE;
            case SLOT_HULL:
                return RocketPartType.HULL;
            case SLOT_PAYLOAD:
                return RocketPartType.PAYLOAD;
            default:
                if (index >= SLOT_FUEL_START && index < SLOT_FUEL_START + SLOT_FUEL_COUNT)
                    return RocketPartType.FUEL_TANK;
                return null;
        }
    }

    @Override
    public void markDirty() {
        super.markDirty();
        rebuildBlueprint();
    }

    private void rebuildBlueprint() {
        partIds.clear();
        List<RocketPart> parts = new ArrayList<RocketPart>();
        RocketPartRegistry registry = OrbitalIndustriesAPI.rocketPartRegistry;
        if (registry == null) {
            blueprint = null;
            return;
        }
        for (int i = 0; i < SLOT_OUTPUT; i++) {
            ItemStack stack = slots[i];
            if (stack == null || !(stack.getItem() instanceof ItemRocketPart))
                continue;
            RocketPart part = registry.getPartFromItem(stack);
            if (part == null || part.getType() != getRequiredTypeForSlot(i))
                continue;
            parts.add(part);
            partIds.add(ItemRocketPart.getEffectivePartId(stack));
        }
        if (parts.isEmpty()) {
            blueprint = null;
            return;
        }
        RocketAssemblerMultiblock assembler = new RocketAssemblerMultiblock();
        blueprint = assembler.buildFromParts(parts);
    }

    public RocketBlueprint getBlueprint() {
        return blueprint;
    }

    /** Part ids in slot order, for writing to ItemRocket NBT on assemble. */
    public List<String> getPartIds() {
        return Collections.unmodifiableList(partIds);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        NBTTagList list = tag.getTagList("Items", 10);
        for (int i = 0; i < list.tagCount(); i++) {
            NBTTagCompound itemTag = list.getCompoundTagAt(i);
            int slot = itemTag.getByte("Slot") & 255;
            if (slot >= 0 && slot < INV_SIZE)
                slots[slot] = ItemStack.loadItemStackFromNBT(itemTag);
        }
        rebuildBlueprint();
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        NBTTagList list = new NBTTagList();
        for (int i = 0; i < INV_SIZE; i++) {
            if (slots[i] != null) {
                NBTTagCompound itemTag = new NBTTagCompound();
                itemTag.setByte("Slot", (byte) i);
                slots[i].writeToNBT(itemTag);
                list.appendTag(itemTag);
            }
        }
        tag.setTag("Items", list);
    }
}
