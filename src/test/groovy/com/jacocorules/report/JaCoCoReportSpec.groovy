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

package com.jacocorules.report

import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

class JaCoCoReportSpec extends Specification
{
    @Rule
    TemporaryFolder tempFolder // spock initializes this!

    def reportWithHeadersAndAtLeastOneRowWorks()
    {
        given:
        def rptName = "goodreport.txt"
        def expectedKeys = ReportFormat.EXPECTED_COLUMNS as Set
        def expectedSize = expectedKeys.size()
        def expectedValue = '1'

        and:
        File goodreport = tempFolder.newFile(rptName)
        goodreport << ReportFormat.EXPECTED_COLUMNS.join(",") + "\n"
        goodreport << ([ expectedValue ] * expectedSize).join(",")

        and:
        def rpt = new JaCoCoReportCSV(goodreport.absolutePath)
        rpt.read()

        and:
        def seenKeys = [] as Set
        def seenValues = [] as Set

        when:
        rpt.eachRowKeyed { HashMap<String,String> h -> seenKeys.addAll(h.keySet()); seenValues.addAll(h.values()) }

        then:
        seenKeys.containsAll(expectedKeys)
        seenValues.size() == 1 && seenValues.contains(expectedValue)
    }
}
