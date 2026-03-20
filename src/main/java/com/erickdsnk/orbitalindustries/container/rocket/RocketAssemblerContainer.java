package com.erickdsnk.orbitalindustries.container.rocket;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import com.erickdsnk.orbitalindustries.tile.rocket.RocketAssemblerTileEntity;

/**
 * Container for the Rocket Assembler GUI. Left: engine(0), guidance(1),
 * hull(2),
 * payload(3) in first column; fuel(4-8) in second column. Right: output(9).
 * Then player inventory and hotbar.
 */
public class RocketAssemblerContainer extends Container {

    /**
     * Left margin to match 256px GUI texture; must match
     * RocketAssemblerGUI.GUI_LEFT_MARGIN.
     */
    private static final int GUI_LEFT_MARGIN = 40;
    private static final int LEFT_COL_X = 24 + GUI_LEFT_MARGIN;
    private static final int LEFT_COL_GAP = 8;
    private static final int FUEL_COL_X = 72 + GUI_LEFT_MARGIN;
    private static final int FUEL_COL_GAP = 2;
    private static final int SLOT_DY = 16;
    private static final int SLOT_Y_START = 33;
    private static final int OUTPUT_X = 125 + GUI_LEFT_MARGIN;
    private static final int OUTPUT_Y = 139;
    private static final int PLAYER_INV_Y = 172;
    private static final int PLAYER_INV_X = 8 + GUI_LEFT_MARGIN;

    private final RocketAssemblerTileEntity tile;

    public RocketAssemblerContainer(InventoryPlayer playerInv, RocketAssemblerTileEntity tile) {
        this.tile = tile;

        // Left column: engine(0), guidance(1), hull(2), payload(3)
        for (int i = 0; i < 4; i++) {
            addSlotToContainer(new SlotRocketPart(tile, i, LEFT_COL_X, SLOT_Y_START + i * (SLOT_DY + LEFT_COL_GAP)));
        }

        // Second column: fuel (4-8)
        for (int i = 0; i < 5; i++) {
            addSlotToContainer(new SlotRocketPart(tile, 4 + i, FUEL_COL_X,
                    SLOT_Y_START + i * (SLOT_DY + FUEL_COL_GAP)));
        }

        addSlotToContainer(new SlotOutput(tile, 9, OUTPUT_X, OUTPUT_Y));

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlotToContainer(
                        new Slot(playerInv, col + row * 9 + 9, PLAYER_INV_X + col * 18, PLAYER_INV_Y + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            addSlotToContainer(new Slot(playerInv, col, PLAYER_INV_X + col * 18, PLAYER_INV_Y + 58));
        }
    }

    public RocketAssemblerTileEntity getTile() {
        return tile;
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return tile.isUseableByPlayer(player);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index) {
        ItemStack copy = null;
        Slot slot = (Slot) inventorySlots.get(index);
        if (slot != null && slot.getHasStack()) {
            ItemStack stack = slot.getStack();
            copy = stack.copy();
            int tileEnd = 10;
            if (index < tileEnd) {
                if (!mergeItemStack(stack, tileEnd, inventorySlots.size(), true))
                    return null;
            } else {
                if (!mergeItemStack(stack, 0, tileEnd - 1, false))
                    return null;
            }
            if (stack.stackSize == 0)
                slot.putStack(null);
            else
                slot.onSlotChanged();
        }
        return copy;
    }

    /**
     * Vanilla {@link Container#mergeItemStack} puts stacks into the first empty
     * slot
     * without calling {@link Slot#isItemValid}, so shift-click ignored assembler
     * slot
     * rules. Require {@code isItemValid} before placing into an empty slot.
     */
    @Override
    protected boolean mergeItemStack(ItemStack stack, int start, int end, boolean reverse) {
        boolean didMerge = false;
        int k = start;
        if (reverse) {
            k = end - 1;
        }
        Slot slot;
        ItemStack inslot;
        if (stack.isStackable()) {
            while (stack.stackSize > 0 && (!reverse && k < end || reverse && k >= start)) {
                slot = (Slot) inventorySlots.get(k);
                inslot = slot.getStack();
                if (inslot != null && inslot.getItem() == stack.getItem()
                        && (!stack.getHasSubtypes() || stack.getItemDamage() == inslot.getItemDamage())
                        && ItemStack.areItemStackTagsEqual(stack, inslot)) {
                    int total = inslot.stackSize + stack.stackSize;
                    if (total <= stack.getMaxStackSize()) {
                        stack.stackSize = 0;
                        inslot.stackSize = total;
                        slot.onSlotChanged();
                        didMerge = true;
                    } else if (inslot.stackSize < stack.getMaxStackSize()) {
                        stack.stackSize -= stack.getMaxStackSize() - inslot.stackSize;
                        inslot.stackSize = stack.getMaxStackSize();
                        slot.onSlotChanged();
                        didMerge = true;
                    }
                }
                if (reverse) {
                    --k;
                } else {
                    ++k;
                }
            }
        }
        if (stack.stackSize > 0) {
            if (reverse) {
                k = end - 1;
            } else {
                k = start;
            }
            while (!reverse && k < end || reverse && k >= start) {
                slot = (Slot) inventorySlots.get(k);
                inslot = slot.getStack();
                if (inslot == null && slot.isItemValid(stack)) {
                    slot.putStack(stack.copy());
                    slot.onSlotChanged();
                    stack.stackSize = 0;
                    didMerge = true;
                    break;
                }
                if (reverse) {
                    --k;
                } else {
                    ++k;
                }
            }
        }
        return didMerge;
    }

    /**
     * Assembler input: only parts whose {@link RocketPartType} matches the slot
     * (vanilla {@link Slot#isItemValid} defaults to true otherwise).
     */
    private static class SlotRocketPart extends Slot {
        SlotRocketPart(RocketAssemblerTileEntity inv, int index, int x, int y) {
            super(inv, index, x, y);
        }

        @Override
        public boolean isItemValid(ItemStack stack) {
            return inventory.isItemValidForSlot(slotNumber, stack);
        }
    }

    /** Slot that does not accept manual insertion (output slot). */
    private static class SlotOutput extends Slot {
        public SlotOutput(RocketAssemblerTileEntity inv, int index, int x, int y) {
            super(inv, index, x, y);
        }

        @Override
        public boolean isItemValid(ItemStack stack) {
            return false;
        }
    }
}
