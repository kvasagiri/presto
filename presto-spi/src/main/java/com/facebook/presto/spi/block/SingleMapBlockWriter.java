/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.facebook.presto.spi.block;

import io.airlift.slice.Slice;
import org.openjdk.jol.info.ClassLayout;

public class SingleMapBlockWriter
        extends AbstractSingleMapBlock
        implements BlockBuilder
{
    private static final int INSTANCE_SIZE = ClassLayout.parseClass(SingleMapBlockWriter.class).instanceSize();

    private final BlockBuilder keyBlockBuilder;
    private final BlockBuilder valueBlockBuilder;
    private final long initialBlockBuilderSize;
    private int positionsWritten;

    private boolean writeToValueNext;

    SingleMapBlockWriter(int start, BlockBuilder keyBlockBuilder, BlockBuilder valueBlockBuilder)
    {
        super(start, keyBlockBuilder, valueBlockBuilder);
        this.keyBlockBuilder = keyBlockBuilder;
        this.valueBlockBuilder = valueBlockBuilder;
        this.initialBlockBuilderSize = keyBlockBuilder.getSizeInBytes() + valueBlockBuilder.getSizeInBytes();
    }

    @Override
    public long getSizeInBytes()
    {
        return keyBlockBuilder.getSizeInBytes() + valueBlockBuilder.getSizeInBytes() - initialBlockBuilderSize;
    }

    @Override
    public long getRetainedSizeInBytes()
    {
        return INSTANCE_SIZE + keyBlockBuilder.getRetainedSizeInBytes() + valueBlockBuilder.getRetainedSizeInBytes();
    }

    @Override
    public BlockBuilder writeByte(int value)
    {
        if (writeToValueNext) {
            valueBlockBuilder.writeByte(value);
        }
        else {
            keyBlockBuilder.writeByte(value);
        }
        return this;
    }

    @Override
    public BlockBuilder writeShort(int value)
    {
        if (writeToValueNext) {
            valueBlockBuilder.writeShort(value);
        }
        else {
            keyBlockBuilder.writeShort(value);
        }
        return this;
    }

    @Override
    public BlockBuilder writeInt(int value)
    {
        if (writeToValueNext) {
            valueBlockBuilder.writeInt(value);
        }
        else {
            keyBlockBuilder.writeInt(value);
        }
        return this;
    }

    @Override
    public BlockBuilder writeLong(long value)
    {
        if (writeToValueNext) {
            valueBlockBuilder.writeLong(value);
        }
        else {
            keyBlockBuilder.writeLong(value);
        }
        return this;
    }

    @Override
    public BlockBuilder writeBytes(Slice source, int sourceIndex, int length)
    {
        if (writeToValueNext) {
            valueBlockBuilder.writeBytes(source, sourceIndex, length);
        }
        else {
            keyBlockBuilder.writeBytes(source, sourceIndex, length);
        }
        return this;
    }

    @Override
    public BlockBuilder writeObject(Object value)
    {
        if (writeToValueNext) {
            valueBlockBuilder.writeObject(value);
        }
        else {
            keyBlockBuilder.writeObject(value);
        }
        return this;
    }

    @Override
    public BlockBuilder beginBlockEntry()
    {
        BlockBuilder result;
        if (writeToValueNext) {
            result = valueBlockBuilder.beginBlockEntry();
        }
        else {
            result = keyBlockBuilder.beginBlockEntry();
        }
        return result;
    }

    @Override
    public BlockBuilder appendNull()
    {
        if (writeToValueNext) {
            valueBlockBuilder.appendNull();
        }
        else {
            keyBlockBuilder.appendNull();
        }
        entryAdded();
        return this;
    }

    @Override
    public BlockBuilder closeEntry()
    {
        if (writeToValueNext) {
            valueBlockBuilder.closeEntry();
        }
        else {
            keyBlockBuilder.closeEntry();
        }
        entryAdded();
        return this;
    }

    private void entryAdded()
    {
        writeToValueNext = !writeToValueNext;
        positionsWritten++;
    }

    @Override
    public int getPositionCount()
    {
        return positionsWritten;
    }

    @Override
    public BlockEncoding getEncoding()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Block build()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public BlockBuilder newBlockBuilderLike(BlockBuilderStatus blockBuilderStatus)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder("SingleMapBlockWriter{");
        sb.append("positionCount=").append(getPositionCount());
        sb.append('}');
        return sb.toString();
    }
}
