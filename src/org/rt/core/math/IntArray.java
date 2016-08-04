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

import java.util.Arrays;

/**
 *
 * @author user
 */
public class IntArray 
{
    private int[] array;
    private int size;

    public IntArray()
    {
        array = new int[10];
        size = 0;
    }

    public void clear()
    {
        array = new int[10];
        size = 0;
    }
    
    public final void add(int i)
    {
        if (size == array.length)
        {
            int[] oldArray = array;
            array = new int[(size * 3) / 2 + 1];
            System.arraycopy(oldArray, 0, array, 0, size);
        }
        array[size] = i;
        size++;
    }
    
    public final void add(int... value)
    {
        for(int i : value)
            add(i);
    }

    public final void set(int index, int value)
    {
        array[index] = value;
    }

    public final int get(int index)
    {
        return array[index];
    }

    public final int getSize()
    {
        return size;
    }

    public final int[] trim()
    {
        if (size < array.length)
        {
            int[] oldArray = array;
            array = new int[size];
            System.arraycopy(oldArray, 0, array, 0, size);            
        }
        return array;
    }
    
    @Override
    public String toString()
    {
        return Arrays.toString(trim());
    }
}
