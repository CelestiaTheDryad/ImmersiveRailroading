package cam72cam.immersiverailroading.gui;

import cam72cam.immersiverailroading.entity.CarTank;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class TankContainer extends Container implements ISyncableSlots {
	
	private CarTank Tank;
	protected int numRows;

	public TankContainer(IInventory playerInventory, CarTank stock) {
		this.Tank = stock;
        int horizSlots = 2;
		this.numRows = (int) Math.ceil((double)stock.getInventorySize() / horizSlots);
        int i = (this.numRows - 4) * 18;
		
		IItemHandler itemHandler = this.Tank.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);

		stock.addListener(this);
		
		for (int slotID = 0; slotID < stock.getInventorySize(); slotID++) {
			int row = slotID / horizSlots;
			int col = slotID % horizSlots;
            this.addSlotToContainer(new SlotItemHandler(itemHandler, slotID, 8 + col * 18, 18 + row * 18));
		}

		int normInvOffset = (horizSlots - 9) * 9;
        for (int l = 0; l < 3; ++l)
        {
            for (int j1 = 0; j1 < 9; ++j1)
            {
                this.addSlotToContainer(new Slot(playerInventory, j1 + l * 9 + 9, normInvOffset + 8 + j1 * 18, 103 + l * 18 + i));
            }
        }

        for (int i1 = 0; i1 < 9; ++i1)
        {
            this.addSlotToContainer(new Slot(playerInventory, i1, normInvOffset + 8 + i1 * 18, 161 + i));
        }
	}
	
	/*
	 * Workaround a bug where stacks were not synced correctly
	 */
	@Override
	public void syncSlots()
    {
        for (int i = 0; i < this.inventorySlots.size(); ++i)
        {
            ItemStack itemstack = ((Slot)this.inventorySlots.get(i)).getStack();
            itemstack = itemstack.isEmpty() ? ItemStack.EMPTY : itemstack.copy();

            for (int j = 0; j < this.listeners.size(); ++j)
            {
                ((IContainerListener)this.listeners.get(j)).sendSlotContents(this, i, itemstack);
            }
        }
    }

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return true;
	}

}
