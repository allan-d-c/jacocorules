/**
 Copyright 2015 Allan Clarke

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

package com.jacocorules.misc

/**
 * Class is represents a series of nested maps using four keys to get to
 * the value, which is the generic @param <T>. This is not providing much
 * utility anymore - consider deleting it.
  */
class MultiMap4<T>
{
    private Map<String,Map<String,Map<String,Map<String,T>>>> map = [:]

    def eachEntry(Closure callback)
    {
        map.each {
            k1, v1 ->
                v1.each {
                    k2, v2 ->
                        v2.each {
                            k3, v3 ->
                                v3.each{
                                    k4, v4 ->
                                        callback(k1, k2, k3, k4, v4)
                                }
                        }
                }
        }
    }

    def sortedEach(Closure callback)
    {
        map.entrySet().sort().each {
            k1, v1 ->
                v1.entrySet().sort().each {
                    k2, v2 ->
                        v2.entrySet().sort().each {
                            k3, v3 ->
                                v3.entrySet().sort().each{
                                    k4, v4 ->
                                        callback(k1, k2, k3, k4, v4)
                                }
                        }
                }
        }
    }

    T ensure(String key1, String key2, String key3, String key4, T defValue)
    {
        if (!map.containsKey(key1))
            map.put(key1, [:])

        def map1 = map.get(key1)

        if (!map1.containsKey(key2))
            map1.put(key2, [:])

        def map2 = map1.get(key2)

        if(!map2.containsKey(key3))
            map2.put(key3, [:])

        def map3 = map2.get(key3)

        if (!map3.containsKey(key4))
            map3.put(key4, defValue)

        get(key1, key2, key3, key4)
    }

    T get(String key1, String key2, String key3, String key4)
    {
        map?.get(key1)?.get(key2)?.get(key3)?.get(key4)
    }

    def put(String key1, String key2, String key3, String key4, T value)
    {
        def map1 = map.get(key1)
        def map2 = map1.get(key2)
        def map3 = map2.get(key3)
        map3.put(key4, value)
    }

    def containsKey(String key1)
    {
        map.containsKey(key1)
    }

    def get(String key1)
    {
        map.get(key1)
    }

    boolean isEmpty()
    {
        map.isEmpty()
    }
}
