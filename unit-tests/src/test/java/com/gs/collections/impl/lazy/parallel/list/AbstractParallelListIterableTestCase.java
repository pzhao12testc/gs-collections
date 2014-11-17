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

import com.gs.collections.api.ParallelIterable;
import com.gs.collections.api.RichIterable;
import com.gs.collections.api.list.MutableList;
import com.gs.collections.api.list.ParallelListIterable;
import com.gs.collections.impl.lazy.parallel.AbstractParallelIterableTestCase;
import com.gs.collections.impl.list.mutable.FastList;

public abstract class AbstractParallelListIterableTestCase extends AbstractParallelIterableTestCase
{
    @Override
    protected abstract ParallelListIterable<Integer> classUnderTest();

    @Override
    protected MutableList<Integer> getExpected()
    {
        return FastList.newListWith(1, 2, 2, 3, 3, 3, 4, 4, 4, 4);
    }

    @Override
    protected <T> RichIterable<T> getActual(ParallelIterable<T> actual)
    {
        return actual.toList();
    }

    @Override
    protected boolean isOrdered()
    {
        return true;
    }

    @Override
    protected boolean isUnique()
    {
        return false;
    }
}
