package net.mechanicalcat.pycode.script;

import net.mechanicalcat.pycode.entities.HandEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import org.python.core.*;

public class HandStateContextManager extends PyObject implements ContextManager {
    private BlockPos storedPos;
    private EnumFacing storedFacing;
    private HandEntity hand;

    public HandStateContextManager(HandEntity hand) {
        this.hand = hand;
    }

    public PyObject __enter__(ThreadState ts) {
        this.storedPos = this.hand.getPosition();
        this.storedFacing = this.hand.getHorizontalFacing();
        MyBlockPos pos = new MyBlockPos(this.storedPos);
        return Py.java2py(pos);
    }

    public boolean __exit__(ThreadState ts, PyException e) {
        if (e == null) {
            this.hand.moveToBlockPosAndAngles(this.storedPos,
                    this.storedFacing.getHorizontalAngle(), 0);
            return true;
        }
        return false;
    }
}
