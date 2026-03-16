package com.erickdsnk.orbitalindustries.container.rocket;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import com.erickdsnk.orbitalindustries.tile.rocket.RocketAssemblerTileEntity;

/**
 * Container for the Rocket Assembler GUI. Slots 0-8 part slots, slot 9 output
 * (no manual insert); then player inventory and hotbar.
 */
public class RocketAssemblerContainer extends Container {

    private final RocketAssemblerTileEntity tile;

    public RocketAssemblerContainer(InventoryPlayer playerInv, RocketAssemblerTileEntity tile) {
        this.tile = tile;

        for (int i = 0; i < 9; i++) {
            addSlotToContainer(new Slot(tile, i, 8 + i * 18, 18));
        }
        addSlotToContainer(new SlotOutput(tile, 9, 8 + 4 * 18, 54));

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlotToContainer(new Slot(playerInv, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            addSlotToContainer(new Slot(playerInv, col, 8 + col * 18, 142));
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
