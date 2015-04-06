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

class JaCoCoReportCSVSpec extends Specification
{
    @Rule
    TemporaryFolder tempFolder // spock initializes this!

    def anEmptyReportFileIsRejected()
    {
        given:
        File empty = tempFolder.newFile("empty.txt")

        and:
        def rpt = new JaCoCoReportCSV(empty.absolutePath)
        rpt.read()

        when:
        rpt.validate()

        then:
        thrown(RuntimeException)
    }

    def reportWithNoRowsIsRejected()
    {
        given:
        File headers = tempFolder.newFile("headers.txt")
        headers << ReportFormat.EXPECTED_COLUMNS.join(",")

        and:
        def rpt = new JaCoCoReportCSV(headers.absolutePath)
        rpt.read()

        when:
        rpt.validate()

        then:
        thrown(RuntimeException)
    }

    def reportWithMissingColumnRejected()
    {
        given:
        def columns = ReportFormat.EXPECTED_COLUMNS.drop(1)

        and:
        File badreport = tempFolder.newFile("badreport.txt")
        badreport << columns.join(",") + "\n"
        badreport << columns.each { 1 }.join(",")

        and:
        def rpt = new JaCoCoReportCSV(badreport.absolutePath)
        rpt.read()

        when:
        rpt.validate()

        then:
        thrown(RuntimeException)
    }

    def reportWithHeadersAndAtLeastOneRowWorks()
    {
        given:
        File goodreport = tempFolder.newFile("goodreport.txt")
        goodreport << ReportFormat.EXPECTED_COLUMNS.join(",") + "\n"
        goodreport << ReportFormat.EXPECTED_COLUMNS.each { 1 }.join(",")

        and:
        def rpt = new JaCoCoReportCSV(goodreport.absolutePath)
        rpt.read()

        when:
        rpt.validate()

        then:
        noExceptionThrown()
    }
}
