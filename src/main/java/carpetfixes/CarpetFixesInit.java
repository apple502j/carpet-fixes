package carpetfixes;

import carpetfixes.helpers.UpdateScheduler;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

public class CarpetFixesInit {

    public static final ThreadLocal<Set<BlockPos>> lastDirt = ThreadLocal.withInitial(HashSet::new);

    public static HashMap<World,UpdateScheduler> updateScheduler = new HashMap<>();

    public static Direction[] directions = new Direction[]{Direction.WEST, Direction.EAST, Direction.NORTH, Direction.SOUTH, Direction.DOWN, Direction.UP};

    public static final Predicate<BlockState> IS_REPLACEABLE = (blockState) -> blockState.getMaterial().isReplaceable();

    public static void checkStepOnCollision(Entity entity) {
        Box box = entity.getBoundingBox();
        BlockPos blockPos = new BlockPos(box.minX + 0.001D, box.minY -0.20000000298023224D, box.minZ + 0.001D);
        BlockPos blockPos2 = new BlockPos(box.maxX - 0.001D, box.minY, box.maxZ - 0.001D);
        if (entity.world.isRegionLoaded(blockPos, blockPos2)) {
            BlockPos.Mutable mutable = new BlockPos.Mutable();
            for(int i = blockPos.getX(); i <= blockPos2.getX(); ++i) {
                for(int k = blockPos.getZ(); k <= blockPos2.getZ(); ++k) {
                    mutable.set(i, blockPos.getY(), k);
                    BlockState blockState = entity.world.getBlockState(mutable);
                    blockState.getBlock().onSteppedOn(entity.world, mutable, blockState, entity);
                }
            }
        }
    }

    public static void checkEntityLandOnCollision(Entity entity) {
        Box box = entity.getBoundingBox();
        BlockPos blockPos = new BlockPos(box.minX + 0.001D, box.minY -0.20000000298023224D, box.minZ + 0.001D);
        BlockPos blockPos2 = new BlockPos(box.maxX - 0.001D, box.minY, box.maxZ - 0.001D);
        if (entity.world.isRegionLoaded(blockPos, blockPos2)) {
            BlockPos.Mutable mutable = new BlockPos.Mutable();
            for(int i = blockPos.getX(); i <= blockPos2.getX(); ++i) {
                for(int k = blockPos.getZ(); k <= blockPos2.getZ(); ++k) {
                    mutable.set(i, blockPos.getY(), k);
                    BlockState blockState = entity.world.getBlockState(mutable);
                    if (!blockState.isAir()) blockState.getBlock().onEntityLand(entity.world, entity);
                }
            }
        }
    }

    public static void checkFallCollision(Entity entity, float fallDistance) {
        Box box = entity.getBoundingBox();
        BlockPos blockPos = new BlockPos(box.minX + 0.001D, box.minY -0.20000000298023224D, box.minZ + 0.001D);
        BlockPos blockPos2 = new BlockPos(box.maxX - 0.001D, box.minY, box.maxZ - 0.001D);
        if (entity.world.isRegionLoaded(blockPos, blockPos2)) {
            BlockPos.Mutable mutable = new BlockPos.Mutable();
            for(int i = blockPos.getX(); i <= blockPos2.getX(); ++i) {
                for(int k = blockPos.getZ(); k <= blockPos2.getZ(); ++k) {
                    mutable.set(i, blockPos.getY(), k);
                    BlockState blockState = entity.world.getBlockState(mutable);
                    blockState.getBlock().onLandedUpon(entity.world, blockState, mutable, entity, fallDistance);
                    if (!blockState.isAir() && !blockState.isIn(BlockTags.OCCLUDES_VIBRATION_SIGNALS)) entity.emitGameEvent(GameEvent.HIT_GROUND);
                }
            }
        }
    }

    public static float checkJumpVelocityOnCollision(Box box, World world) {
        BlockPos blockPos = new BlockPos(box.minX + 0.001D, box.minY, box.minZ + 0.001D);
        BlockPos blockPos2 = new BlockPos(box.maxX - 0.001D, box.minY, box.maxZ - 0.001D);
        if (world.isRegionLoaded(blockPos, blockPos2)) {
            BlockPos.Mutable mutable = new BlockPos.Mutable();
            float fastestBlock = 1.0F; //Highest value
            float slowestBlock = 1.0F; //Smallest value
            for(int i = blockPos.getX(); i <= blockPos2.getX(); ++i) {
                for(int k = blockPos.getZ(); k <= blockPos2.getZ(); ++k) {
                    mutable.set(i, blockPos.getY(), k);
                    float topBlock = world.getBlockState(mutable).getBlock().getVelocityMultiplier();
                    if ((double)topBlock == 1.0D) {
                        mutable.set(i, box.minY-0.5000001D, k);
                        float affectingBlock = world.getBlockState(mutable).getBlock().getVelocityMultiplier();
                        slowestBlock = Math.min(affectingBlock, slowestBlock);
                        fastestBlock = Math.max(affectingBlock, fastestBlock);
                    } else {
                        slowestBlock = Math.min(topBlock, slowestBlock);
                        fastestBlock = Math.max(topBlock, fastestBlock);
                    }
                }
            }
            return (double)slowestBlock < 1.0D ? slowestBlock : fastestBlock;
        }
        return 1.0F;
    }

    public static float checkVelocityOnCollision(Box box, World world) {
        BlockPos blockPos = new BlockPos(box.minX + 0.001D, box.minY, box.minZ + 0.001D);
        BlockPos blockPos2 = new BlockPos(box.maxX - 0.001D, box.minY, box.maxZ - 0.001D);
        if (world.isRegionLoaded(blockPos, blockPos2)) {
            BlockPos.Mutable mutable = new BlockPos.Mutable();
            float fastestBlock = 1.0F; //Highest value
            float slowestBlock = 1.0F; //Smallest value
            for(int i = blockPos.getX(); i <= blockPos2.getX(); ++i) {
                for(int k = blockPos.getZ(); k <= blockPos2.getZ(); ++k) {
                    mutable.set(i, blockPos.getY(), k);
                    BlockState blockState = world.getBlockState(mutable);
                    float topBlock = blockState.getBlock().getJumpVelocityMultiplier();
                    if (!blockState.isOf(Blocks.WATER) && !blockState.isOf(Blocks.BUBBLE_COLUMN) && (double) topBlock == 1.0D) {
                        mutable.set(i, box.minY - 0.5000001D, k);
                        float affectingBlock = world.getBlockState(mutable).getBlock().getJumpVelocityMultiplier();
                        slowestBlock = Math.min(affectingBlock, slowestBlock);
                        fastestBlock = Math.max(affectingBlock, fastestBlock);
                    } else {
                        slowestBlock = Math.min(topBlock, slowestBlock);
                        fastestBlock = Math.max(topBlock, fastestBlock);
                    }
                }
            }
            return (double)slowestBlock < 1.0D ? slowestBlock : fastestBlock;
        }
        return 1.0F;
    }
}
