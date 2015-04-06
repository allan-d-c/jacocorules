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

package com.jacocorules.io.csv

import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

class ReadableCSVSpec extends Specification
{
    @Rule
    TemporaryFolder tempFolder // spock initializes this!

    def csv(List<String> cells)
    {
        cells.join(",")
    }

    def hraw = ["a","b","c"]
    def craw = ["1","2","3"]

    def parsingOfJustHeadersWorks()
    {
        given:
        File headers = tempFolder.newFile("headers.txt")
        headers << csv(hraw)

        and:
        def rt = new ReadableCSV(headers.absolutePath)

        when:
        rt.read()

        then:
        rt.headers() == hraw
        rt.count() == 0
    }

    def parsingOfHeadersAndContentWorks()
    {
        given:
        File headersbody = tempFolder.newFile("headers+body.txt")
        headersbody << csv(hraw) + "\n"
        headersbody << csv(craw)

        and:
        def rt = new ReadableCSV(headersbody.absolutePath)

        when:
        rt.read()

        then:
        rt.headers() == hraw
        rt.content(0) == craw
    }
}
