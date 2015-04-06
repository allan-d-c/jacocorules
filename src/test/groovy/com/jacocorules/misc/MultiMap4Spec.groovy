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

import spock.lang.Specification

class MultiMap4Spec extends Specification
{
    def map = new MultiMap4<Double>()
    def key1 = "k1"
    def key2 = "k2"
    def key3 = "k3"
    def key4 = "k4"

    def newMapIsEmpty()
    {
        expect:
            map.isEmpty()
    }

    def mapWithContentIsNotEmpty()
    {
        when:
            map.ensure(key1, key2, key3, key4, 5)
        then:
            !map.isEmpty()
    }

    def ensureOfANewValueWorks()
    {
        when:
            map.ensure(key1, key2, key3, key4, 5)
        then:
            map.get(key1, key2, key3, key4) == 5
    }

    def ensureOfAnExistingValueDoesNotOverwriteExisting()
    {
        given:
            map.ensure(key1, key2, key3, key4, 0)
        when:
            map.ensure(key1, key2, key3, key4, 1)
        then:
            map.get(key1, key2, key3, key4) == 0
    }

    def putWorks()
    {
        given:
            map.ensure(key1, key2, key3, key4, 0)
        when:
            map.put(key1, key2, key3, key4, 1)
        then:
            map.get(key1, key2, key3, key4) == 1
    }

    def getOfMissingValueYieldsNull()
    {
        expect:
            map.get(key1, key2, key3, key4) == null
    }

    def getWorks()
    {
        given:
            map.ensure(key1, key2, key3, key4, 0)
            map.put(key1, key2, key3, key4, 1)
        expect:
            map.get(key1, key2, key3, key4) == 1
    }

    def containsKeyWorks()
    {
        when:
            map.ensure(key1, key2, key3, key4, 5)
        then:
            map.containsKey(key1)
            !map.containsKey("foobar")
    }
}
