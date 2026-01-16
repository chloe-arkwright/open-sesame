package q1d7n.chloearkwright.opensesame.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import q1d7n.chloearkwright.opensesame.OpenSesame;

import java.util.function.BiConsumer;

@Mixin(DoorBlock.class)
public abstract class OpenDoorsTogetherMixin {
    @Inject(
            method = "updateShape(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/world/level/ScheduledTickAccess;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/util/RandomSource;)Lnet/minecraft/world/level/block/state/BlockState;",
            at = @At("RETURN"),
            cancellable = true
    )
    private void openSesame$updateDoorsTogether(BlockState state, LevelReader level, ScheduledTickAccess tickScheduler, BlockPos selfPos, Direction directionToNeighbour, BlockPos neighbourPos, BlockState neighbourState, RandomSource random, CallbackInfoReturnable<BlockState> cir) {
        if (level.isClientSide()) {
            // Don't check on the client, causes a slight desync.
            return;
        }

        if (!(neighbourState.getBlock() instanceof DoorBlock)) {
            // Don't check if not being updated by a door block.
            return;
        }

        if (directionToNeighbour.getAxis().isVertical()) {
            // Don't check updates from the same door or doors above/below
            return;
        }

        DoorHingeSide neighbourHinge = neighbourState.getValue(DoorBlock.HINGE);

        if (!OpenSesame.isPairedDoor(state, directionToNeighbour, neighbourHinge)) {
            // Don't check updates from doors that aren't facing each other.
            return;
        }

        boolean selfIsWood = DoorBlock.isWoodenDoor(state);
        boolean facingIsWood = DoorBlock.isWoodenDoor(neighbourState);
        DoubleBlockHalf ownHalf = state.getValue(DoorBlock.HALF);
        DoubleBlockHalf neighbourHalf = neighbourState.getValue(DoorBlock.HALF);
        boolean selfIsPowered = state.getValue(DoorBlock.POWERED);
        boolean neighbourPowered = neighbourState.getValue(DoorBlock.POWERED);
        DoorHingeSide selfHinge = state.getValue(DoorBlock.HINGE);
        boolean selfIsOpen = state.getValue(DoorBlock.OPEN);
        boolean neighbourIsOpen = neighbourState.getValue(DoorBlock.OPEN);

        if (neighbourHalf == ownHalf && neighbourHinge != selfHinge) { // if we've been updated by an opposite door
            if (!selfIsPowered) {
                if (neighbourPowered) {
                    cir.setReturnValue(cir.getReturnValue().setValue(DoorBlock.OPEN, neighbourIsOpen));
                } else if (neighbourIsOpen != selfIsOpen) {
                    if (!facingIsWood && selfIsWood || facingIsWood == selfIsWood) { // if the doors are the same, or we're wooden and the other is iron.
                        cir.setReturnValue(cir.getReturnValue().setValue(DoorBlock.OPEN, !selfIsOpen));
                    } else {
                        cir.setReturnValue(cir.getReturnValue().setValue(DoorBlock.OPEN, false));
                    }
                }
            }
        }
    }

    @Inject(
            method = "onExplosionHit(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/Explosion;Ljava/util/function/BiConsumer;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/DoorBlock;setOpen(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Z)V"
            ),
            cancellable = true
    )
    private void openSesame$jankDontOpenDoorOnWindCharge(BlockState state, ServerLevel level, BlockPos pos, Explosion explosion, BiConsumer<ItemStack, BlockPos> dropConsumer, CallbackInfo ci) {
        Direction pairedDir = OpenSesame.directionToPairedDoor(state);
        BlockState neighbourState = level.getBlockState(pos.relative(pairedDir));

        if (
                DoorBlock.isWoodenDoor(neighbourState) &&
                neighbourState.getValue(DoorBlock.HINGE) == DoorHingeSide.RIGHT &&
                state.getValue(DoorBlock.HINGE) == DoorHingeSide.LEFT
        ) {
            ci.cancel();
        }
    }
}