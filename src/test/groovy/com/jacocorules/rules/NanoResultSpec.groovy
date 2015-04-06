package com.jacocorules.rules

import spock.lang.Specification

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

class NanoResultSpec extends Specification
{
    def "assignment and equality work for this near pojo"()
    {
        given:
        NanoResult nr1 = new NanoResult()
                .forPackage("p")
                .forClass("c")
                .withMetric("m1", 0.3)
                .hitting(0.3, 0.4)
                .isViolation(true)

        NanoResult nr2 = new NanoResult()
                .forPackage("p")
                .forClass("c")
                .withMetric("m1", 0.3)
                .hitting(0.3, 0.4)
                .isViolation(true)

        expect:
        nr1.equals(nr2)
        nr1.toString() == nr2.toString()
    }

    def "non-equality works"()
    {
        given:
        NanoResult nr1 = new NanoResult()
                .forPackage("p")
                .forClass("c")
                .withMetric("m1", 0.3)
                .hitting(0.3, 0.4)
                .isViolation(true)

        NanoResult nr2 = new NanoResult()
                .forPackage("p1")
                .forClass("c1")
                .withMetric("m2", 0.3)
                .hitting(0.3, 0.4)

        expect:
        !nr1.equals(nr2)
    }
}
