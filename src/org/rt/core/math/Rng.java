/* 
 * The MIT License
 *
 * Copyright 2016 user.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.rt.core.math;

import org.rt.core.coordinates.Point2f;
import org.rt.core.coordinates.Point3f;

/**
 *
 * @author user
 */
public class Rng 
{    
    public static float getFloat()
    {
        return (float)Math.random();
    }
    
    public static boolean isRndBelow(double value)
    {
        if(getFloat()<value)
            return true;
        else
            return false;
    }
    
    public static Point2f getPoint2f()
    {
        float a = getFloat();
        float b = getFloat();

        return new Point2f(a, b);
    }

    public static Point3f getPoint3f()
    {
        float a = getFloat();
        float b = getFloat();
        float c = getFloat();

        return new Point3f(a, b, c);
    }
}
