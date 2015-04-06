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

package com.jacocorules.rules

import com.jacocorules.io.csv.ReadableCSV

class JaCoCoRulesCSV extends ReadableCSV
{
    JaCoCoRulesCSV(String name) {
        super(name)
    }

    def readAndValidate()
    {
        read()
        validate()
    }

    def validate()
    {
        def inputName = name()

        if (headers().isEmpty())
            throw new RuntimeException("JaCoCoRulesCSV $inputName has no headers")
        if (count() == 0)
            throw new RuntimeException("JaCoCoRulesCSV $inputName has no content")

        def expected = RulesFormat.EXPECTED_COLUMNS as Set
        def found = headers().toSet()

        if (!found.containsAll(expected)) {
            def unfound = expected - found
            throw new RuntimeException("JaCoCoRulesCSV $inputName does not contain the expected headers. Not found: $unfound")
        }
    }
}
