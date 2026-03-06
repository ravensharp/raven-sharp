package xyz.ravensharp.gc.module.modules.utility;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import xyz.ravensharp.gc.module.Category;
import xyz.ravensharp.gc.module.Module;
import xyz.ravensharp.gc.setting.Setting;
import xyz.ravensharp.gc.utils.BlockUtil;
import xyz.ravensharp.gc.utils.ItemUtil;

public class AutoTool extends Module {
	public AutoTool() {
		super("AutoTool", "Automatically switch to better tools.", Category.UTILITY);
		this.addSetting(new Setting("Dynamic Switch", false)
				.setDescription("If you prefer to switch directly to the target tool."));
		this.addSetting(new Setting("Switch Delay", 1D, 1000D, 230D)
				.setDescription("Delay before switching to the target tool."));
		this.addSetting(new Setting("Switching Delay", 1D, 500D, 50D)
				.setDescription("Delay in each slot while switching back to the target tool."));
		this.addSetting(new Setting("Sneaking Only", false).setDescription("You must sneak."));
		this.addSetting(new Setting("On Tools Only", false)
				.setDescription("You must hold any tools such as Pickaxe, Shovel, Axe."));
	}

	private Thread timerThread;

	@Override
	public void onEnable() {
		super.onEnable();
		timerThread = new Thread(() -> {
			while (this.isToggled()) {
				try {
					Thread.sleep(1);
					mc.addScheduledTask(() -> tasker());
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					break;
				}
			}
		});
		timerThread.start();
	}

	@Override
	public void onDisable() {
		super.onDisable();
		if (timerThread != null) {
			timerThread.interrupt();
		}
	}

	private BlockPos lastBreakingBlock = null;
	private long breakingSince = 0;
	private int bestToolSlot = -1;

	private long lastSwitching = 0;

	@SubscribeEvent
	public void onClientTick(TickEvent.PlayerTickEvent event) {
		if (mc.thePlayer == null || mc.theWorld == null)
			return;

		if (bestToolSlot != -1) {
			long diff = System.currentTimeMillis() - breakingSince;
			double delay = this.getSetting("Switch Delay").getDouble();

			if (diff > delay) {
				if (this.getSetting("Dynamic Switch").getBoolean()) {
					mc.thePlayer.inventory.currentItem = bestToolSlot;
					return;
				}

				long switchingDiff = System.currentTimeMillis() - lastSwitching;
				double switchingDelay = this.getSetting("Switching Delay").getDouble();

				if (switchingDiff > switchingDelay) {
					mc.thePlayer.inventory.currentItem = ItemUtil.nextSlotToward(mc.thePlayer.inventory.currentItem,
							bestToolSlot);
					lastSwitching = System.currentTimeMillis();
				}
			}
		} else {
			lastSwitching = 0;
			breakingSince = 0;
		}
	}

	public void tasker() {
		if (mc.thePlayer == null || mc.theWorld == null)
			return;

		if (!this.isToggled())
			return;

		BlockPos breakingBlockPos = getBreakingBlockPosition();

		if (breakingBlockPos != lastBreakingBlock) {
			lastBreakingBlock = breakingBlockPos;
			bestToolSlot = -1;
			if (breakingBlockPos != null) {
				breakingSince = System.currentTimeMillis();
			}
		}
		if (breakingBlockPos == null)
			return;

		if (this.getSetting("Sneaking Only").getBoolean() && !mc.thePlayer.isSneaking())
			return;
		if (bestToolSlot != -1)
			return;

		IBlockState blockState = mc.theWorld.getBlockState(breakingBlockPos);
		if (blockState == null)
			return;
		Block block = blockState.getBlock();
		if (block == null)
			return;
		if (block == Blocks.air || block instanceof BlockLiquid)
			return;
		ItemStack heldItem = mc.thePlayer.getHeldItem();
		if (heldItem == null)
			return;
		Item item = heldItem.getItem();
		if (this.getSetting("On Tools Only").getBoolean()) {
			if (!(item instanceof ItemTool || item instanceof ItemShears))
				return;
		}

		int bestSlot = -1;
		float bestSpeed = -1;

		for (int i = 0; i < 9; i++) {
			ItemStack stack = mc.thePlayer.inventory.getStackInSlot(i);
			if (stack != null) {
				float speed = getBreakSpeed(stack, block);
				if (speed > bestSpeed) {
					bestSpeed = speed;
					bestSlot = i;
				}
			}
		}

		if (bestSlot != -1 && mc.thePlayer.inventory.currentItem != bestSlot) {
			bestToolSlot = bestSlot;
		}
	}

	private float getBreakSpeed(ItemStack heldItem, Block block) {
		return heldItem.getItem().getStrVsBlock(heldItem, block);
	}

	private BlockPos getBreakingBlockPosition() {
		if (isPlayerDigging()) {
			return getBreakingBlockPos();
		}
		return null;
	}

	private boolean isPlayerDigging() {
		return BlockUtil.isPlayerDigging();
	}

	private BlockPos getBreakingBlockPos() {
		return BlockUtil.getBreakingBlockPos();
	}
}
