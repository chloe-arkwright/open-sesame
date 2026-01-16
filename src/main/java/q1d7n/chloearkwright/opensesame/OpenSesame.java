package q1d7n.chloearkwright.opensesame;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;

public class OpenSesame {
    public static boolean isPairedDoor(BlockState state, Direction neighbourDir, DoorHingeSide neighbourHinge) {
        Direction selfFacing = state.getValue(DoorBlock.FACING);
        DoorHingeSide selfHinge = state.getValue(DoorBlock.HINGE);

        if (selfHinge == DoorHingeSide.RIGHT) {
            if (neighbourDir == selfFacing.getCounterClockWise()) {
                return neighbourHinge == DoorHingeSide.LEFT;
            }
        } else if (selfHinge == DoorHingeSide.LEFT) {
            if (neighbourDir == selfFacing.getClockWise()) {
                return neighbourHinge == DoorHingeSide.RIGHT;
            }
        }

        return false;
    }

    public static Direction directionToPairedDoor(BlockState state) {
        Direction facing = state.getValue(DoorBlock.FACING);
        DoorHingeSide hinge = state.getValue(DoorBlock.HINGE);

        if (hinge == DoorHingeSide.RIGHT) {
            return facing.getCounterClockWise();
        } else if (hinge == DoorHingeSide.LEFT) {
            return facing.getClockWise();
        }

        throw new IllegalStateException("OpenSesame.directionToPairedDoor must be called on a door block.");
    }
}
