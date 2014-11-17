/*
 * Copyright 2014 Goldman Sachs.
 *
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

package com.gs.collections.impl.lazy.parallel.list;

import java.util.Comparator;
import java.util.Iterator;
import java.util.RandomAccess;
import java.util.concurrent.ExecutorService;

import com.gs.collections.api.LazyIterable;
import com.gs.collections.api.annotation.Beta;
import com.gs.collections.api.block.function.Function;
import com.gs.collections.api.block.function.primitive.DoubleFunction;
import com.gs.collections.api.block.function.primitive.FloatFunction;
import com.gs.collections.api.block.function.primitive.IntFunction;
import com.gs.collections.api.block.function.primitive.LongFunction;
import com.gs.collections.api.block.predicate.Predicate;
import com.gs.collections.api.block.procedure.Procedure;
import com.gs.collections.api.block.procedure.Procedure2;
import com.gs.collections.api.block.procedure.primitive.ObjectIntProcedure;
import com.gs.collections.api.list.ListIterable;
import com.gs.collections.api.list.ParallelListIterable;
import com.gs.collections.api.map.MapIterable;
import com.gs.collections.api.multimap.list.ListMultimap;
import com.gs.collections.impl.lazy.AbstractLazyIterable;

@Beta
public final class ListIterableParallelIterable<T> extends AbstractParallelListIterable<T, RootListBatch<T>>
{
    private final ListIterable<T> delegate;
    private final ExecutorService executorService;
    private final int batchSize;

    public ListIterableParallelIterable(ListIterable<T> delegate, ExecutorService executorService, int batchSize)
    {
        if (executorService == null)
        {
            throw new NullPointerException();
        }
        if (batchSize < 1)
        {
            throw new IllegalArgumentException();
        }
        if (!(delegate instanceof RandomAccess))
        {
            throw new IllegalArgumentException();
        }
        this.delegate = delegate;
        this.executorService = executorService;
        this.batchSize = batchSize;
    }

    @Override
    public ExecutorService getExecutorService()
    {
        return this.executorService;
    }

    @Override
    public LazyIterable<RootListBatch<T>> split()
    {
        return new ListIterableParallelBatchLazyIterable();
    }

    public void forEach(Procedure<? super T> procedure)
    {
        forEach(this, procedure);
    }

    public boolean anySatisfy(Predicate<? super T> predicate)
    {
        return anySatisfy(this, predicate);
    }

    public boolean allSatisfy(Predicate<? super T> predicate)
    {
        return allSatisfy(this, predicate);
    }

    public T detect(Predicate<? super T> predicate)
    {
        return detect(this, predicate);
    }

    @Override
    public <V> ParallelListIterable<V> flatCollect(Function<? super T, ? extends Iterable<V>> function)
    {
        // TODO: Implement in parallel
        return this.delegate.flatCollect(function).asParallel(this.executorService, this.batchSize);
    }

    @Override
    public T min(Comparator<? super T> comparator)
    {
        // TODO: Implement in parallel
        return this.delegate.min(comparator);
    }

    @Override
    public T max(Comparator<? super T> comparator)
    {
        // TODO: Implement in parallel
        return this.delegate.max(comparator);
    }

    @Override
    public T min()
    {
        // TODO: Implement in parallel
        return this.delegate.min();
    }

    @Override
    public T max()
    {
        // TODO: Implement in parallel
        return this.delegate.max();
    }

    @Override
    public <V extends Comparable<? super V>> T minBy(Function<? super T, ? extends V> function)
    {
        // TODO: Implement in parallel
        return this.delegate.minBy(function);
    }

    @Override
    public <V extends Comparable<? super V>> T maxBy(Function<? super T, ? extends V> function)
    {
        // TODO: Implement in parallel
        return this.delegate.maxBy(function);
    }

    @Override
    public long sumOfInt(IntFunction<? super T> function)
    {
        // TODO: Implement in parallel
        return this.delegate.sumOfInt(function);
    }

    @Override
    public double sumOfFloat(FloatFunction<? super T> function)
    {
        // TODO: Implement in parallel
        return this.delegate.sumOfFloat(function);
    }

    @Override
    public long sumOfLong(LongFunction<? super T> function)
    {
        // TODO: Implement in parallel
        return this.delegate.sumOfLong(function);
    }

    @Override
    public double sumOfDouble(DoubleFunction<? super T> function)
    {
        // TODO: Implement in parallel
        return this.delegate.sumOfDouble(function);
    }

    @Override
    public Object[] toArray()
    {
        // TODO: Implement in parallel
        return this.delegate.toArray();
    }

    @Override
    public <E> E[] toArray(E[] array)
    {
        // TODO: Implement in parallel
        return this.delegate.toArray(array);
    }

    @Override
    public <V> ListMultimap<V, T> groupBy(Function<? super T, ? extends V> function)
    {
        // TODO: Implement in parallel
        return this.delegate.groupBy(function);
    }

    @Override
    public <V> ListMultimap<V, T> groupByEach(Function<? super T, ? extends Iterable<V>> function)
    {
        // TODO: Implement in parallel
        return this.delegate.groupByEach(function);
    }

    @Override
    public <V> MapIterable<V, T> groupByUniqueKey(Function<? super T, ? extends V> function)
    {
        // TODO: Implement in parallel
        return this.delegate.groupByUniqueKey(function);
    }

    public int getBatchSize()
    {
        return this.batchSize;
    }

    private class ListIterableParallelBatchIterator implements Iterator<RootListBatch<T>>
    {
        protected int chunkIndex;

        public boolean hasNext()
        {
            return this.chunkIndex * ListIterableParallelIterable.this.getBatchSize() < ListIterableParallelIterable.this.delegate.size();
        }

        public RootListBatch<T> next()
        {
            int chunkStartIndex = this.chunkIndex * ListIterableParallelIterable.this.getBatchSize();
            int chunkEndIndex = (this.chunkIndex + 1) * ListIterableParallelIterable.this.getBatchSize();
            int truncatedChunkEndIndex = Math.min(chunkEndIndex, ListIterableParallelIterable.this.delegate.size());
            this.chunkIndex++;
            return new ListIterableBatch<T>(ListIterableParallelIterable.this.delegate, chunkStartIndex, truncatedChunkEndIndex);
        }

        public void remove()
        {
            throw new UnsupportedOperationException("Cannot call remove() on " + ListIterableParallelIterable.this.delegate.getClass().getSimpleName());
        }
    }

    private class ListIterableParallelBatchLazyIterable
            extends AbstractLazyIterable<RootListBatch<T>>
    {
        public void forEach(Procedure<? super RootListBatch<T>> procedure)
        {
            this.each(procedure);
        }

        public void each(Procedure<? super RootListBatch<T>> procedure)
        {
            for (RootListBatch<T> chunk : this)
            {
                procedure.value(chunk);
            }
        }

        public <P> void forEachWith(Procedure2<? super RootListBatch<T>, ? super P> procedure, P parameter)
        {
            for (RootListBatch<T> chunk : this)
            {
                procedure.value(chunk, parameter);
            }
        }

        public void forEachWithIndex(ObjectIntProcedure<? super RootListBatch<T>> objectIntProcedure)
        {
            throw new UnsupportedOperationException(this.getClass().getSimpleName() + ".forEachWithIndex() not implemented yet");
        }

        public Iterator<RootListBatch<T>> iterator()
        {
            return new ListIterableParallelBatchIterator();
        }
    }
}
