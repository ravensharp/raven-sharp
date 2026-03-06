package xyz.ravensharp.gc.utils;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;

public class HitBoxUtil {
    public static double calculateSupportPercentage() {
    	Minecraft mc = Minecraft.getMinecraft();
    	
        AxisAlignedBB bb = mc.thePlayer.getEntityBoundingBox();
        double playerWidth = bb.maxX - bb.minX;
        double playerDepth = bb.maxZ - bb.minZ;
        double totalArea = playerWidth * playerDepth;

        if (totalArea <= 0.0) {
            return 0.0;
        }

        double yBelow = mc.thePlayer.posY - 0.1;
        int blockMinX = (int) Math.floor(bb.minX);
        int blockMaxX = (int) Math.floor(bb.maxX);
        int blockMinZ = (int) Math.floor(bb.minZ);
        int blockMaxZ = (int) Math.floor(bb.maxZ);
        int blockY = (int) Math.floor(yBelow);

        double supportedArea = 0.0;

        for (int x = blockMinX; x <= blockMaxX; x++) {
            for (int z = blockMinZ; z <= blockMaxZ; z++) {
                BlockPos pos = new BlockPos(x, blockY, z);
                IBlockState state = mc.theWorld.getBlockState(pos);
                if (state == null) continue;
                Block block = state.getBlock();
                if (block == null) continue;
                
                if (block.getCollisionBoundingBox(mc.theWorld, pos, state) != null) {
                    double intersectMinX = Math.max(bb.minX, x);
                    double intersectMaxX = Math.min(bb.maxX, x + 1.0);
                    double intersectMinZ = Math.max(bb.minZ, z);
                    double intersectMaxZ = Math.min(bb.maxZ, z + 1.0);

                    double intersectWidth = Math.max(0.0, intersectMaxX - intersectMinX);
                    double intersectDepth = Math.max(0.0, intersectMaxZ - intersectMinZ);

                    supportedArea += intersectWidth * intersectDepth;
                }
            }
        }

        double supportPercentage = (supportedArea / totalArea) * 100.0;

        return Math.max(0.0, Math.min(100.0, supportPercentage));
    }
    
    public static boolean canPlaceBlock(MovingObjectPosition mop, ItemStack heldItem) {
    	Minecraft mc = Minecraft.getMinecraft();
    	
        if (mop == null || mop.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK) return false;
        if (heldItem == null || !(heldItem.getItem() instanceof ItemBlock)) return false;

        ItemBlock itemBlock = (ItemBlock) heldItem.getItem();
        BlockPos targetPos = mop.getBlockPos();
        BlockPos placePos = targetPos.offset(mop.sideHit);

        Block blockAtPos = mc.theWorld.getBlockState(placePos).getBlock();
        boolean isReplaceable = blockAtPos.isReplaceable(mc.theWorld, placePos) || blockAtPos.getMaterial().isReplaceable();

        AxisAlignedBB placeBB = new AxisAlignedBB(
            placePos.getX(), placePos.getY(), placePos.getZ(), 
            placePos.getX() + 1.0, placePos.getY() + 1.0, placePos.getZ() + 1.0
        );

        boolean noEntityCollision = mc.theWorld.checkNoEntityCollision(placeBB);
        boolean canPlaceOnSide = itemBlock.canPlaceBlockOnSide(mc.theWorld, targetPos, mop.sideHit, mc.thePlayer, heldItem);

        return isReplaceable && noEntityCollision && canPlaceOnSide;
    }
}
