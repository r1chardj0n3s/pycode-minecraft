/*
 * Copyright (c) 2017 Richard Jones <richard@mechanicalcat.net>
 * All Rights Reserved
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

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
