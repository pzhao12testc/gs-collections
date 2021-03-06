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

package com.gs.collections.impl.tuple;

import com.gs.collections.api.tuple.Twin;

/**
 * A TwinImpl is a PairImpl that has the same type for both items. This is a convenience class
 */
final class TwinImpl<T>
        extends PairImpl<T, T> implements Twin<T>
{
    private static final long serialVersionUID = 1L;

    TwinImpl(T newOne, T newTwo)
    {
        super(newOne, newTwo);
    }

    @Override
    public TwinImpl<T> swap()
    {
        return new TwinImpl<T>(this.getTwo(), this.getOne());
    }
}
