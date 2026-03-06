package xyz.ravensharp.gc.utils;

import java.util.ArrayList;

import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemUtil {
	public static Minecraft mc = Minecraft.getMinecraft();
	
    public static int getStackInSlots(Item item) {
    	int stackSize = 0;
		for (int i = 0; i < 9; i++) {
		    ItemStack stack = mc.thePlayer.inventory.getStackInSlot(i);
		    if (stack != null && stack.getItem().equals(item)) {
		    	stackSize += stack.stackSize;
		    }
		}
		return stackSize;
    }
    
    public static int getStackInSlots(int itemSlot) {
    	ItemStack item = mc.thePlayer.inventory.getStackInSlot(itemSlot);
    	if (item == null) return 0;
    	
    	int stackSize = 0;
		for (int i = 0; i < 9; i++) {
		    ItemStack stack = mc.thePlayer.inventory.getStackInSlot(i);
		    if (stack != null && stack.getItem().equals(item.getItem())) {
		    	stackSize += stack.stackSize;
		    }
		}
		return stackSize;
    }
    
	public static int nearestSlot(int nearMe, ArrayList<Integer> slots) {
        int bestSlot = -1;
        int bestDist = Integer.MAX_VALUE;

        for (int slot : slots) {
            int d = Math.abs(nearMe - slot);
            int dist = Math.min(d, 9 - d);

            if (dist < bestDist) {
                bestDist = dist;
                bestSlot = slot;
            }
        }
        return bestSlot;
    }
	
	public static int nextSlotToward(int currentSlot, int targetSlot) {
	    int current = (currentSlot % 9 + 9) % 9;
	    int target = (targetSlot % 9 + 9) % 9;

	    if (current == target) {
	        return current;
	    }

	    int diff = (target - current + 9) % 9;

	    if (diff <= 4) {
	        return (current + 1) % 9;
	    } else {
	        return (current + 8) % 9;
	    }
	}
}
