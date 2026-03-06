package xyz.ravensharp.gc.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.util.BlockPos;

public class BlockUtil {
	public static Minecraft mc = Minecraft.getMinecraft();

	public static BlockPos getBreakingBlockPosition() {
		if (isPlayerDigging()) {
			return getBreakingBlockPos();
		}
		return null;
	}

	public static boolean isPlayerDigging() {
		try {
			java.lang.reflect.Field hittingBlock = net.minecraft.client.multiplayer.PlayerControllerMP.class
					.getDeclaredField("isHittingBlock");
			hittingBlock.setAccessible(true);
			return hittingBlock.getBoolean(mc.playerController);
		} catch (Exception e) {
			try {
				java.lang.reflect.Field hittingBlockSrg = net.minecraft.client.multiplayer.PlayerControllerMP.class
						.getDeclaredField("field_78778_j");
				hittingBlockSrg.setAccessible(true);
				return hittingBlockSrg.getBoolean(mc.playerController);
			} catch (Exception ex) {
				ex.printStackTrace();
				return false;
			}
		}
	}

	public static BlockPos getBreakingBlockPos() {
		try {
			java.lang.reflect.Field currentBlock = net.minecraft.client.multiplayer.PlayerControllerMP.class
					.getDeclaredField("currentBlock");
			currentBlock.setAccessible(true);
			BlockPos pos = (BlockPos) currentBlock.get(mc.playerController);
			if (pos != null && pos.getY() == -1) {
				return null;
			}
			return pos;
		} catch (Exception e) {
			try {
				java.lang.reflect.Field currentBlockSrg = net.minecraft.client.multiplayer.PlayerControllerMP.class
						.getDeclaredField("field_178895_c");
				currentBlockSrg.setAccessible(true);
				BlockPos pos = (BlockPos) currentBlockSrg.get(mc.playerController);
				if (pos != null && pos.getY() == -1) {
					return null;
				}
				return pos;
			} catch (Exception ex) {
				ex.printStackTrace();
				return null;
			}
		}
	}

	public static boolean isFocusOnBlock() {
		if (mc.objectMouseOver == null)
			return false;
		if (mc.objectMouseOver.typeOfHit == net.minecraft.util.MovingObjectPosition.MovingObjectType.BLOCK)
			return true;
		return false;
	}
}
