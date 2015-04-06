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

import com.jacocorules.io.WriteableText

/**
 * Encapsulates the concept of CSV data read to be written, including managing
 * of the headers row distinct from the body rows. Exact formatting issues are
 * hidden from clients.
 */
class WriteableCSV
{
    protected WriteableText text = new WriteableText()
    private final String sep

    WriteableCSV()
    {
        def f = new Format()
        sep = f.sep()
    }

    def addHeaders(columns)
    {
        if (!text.isEmpty())
            throw new RuntimeException("Add headers as the very first content to a CSV file.")
        text.add(columns.join(sep))
    }

    def add(cells)
    {
        if (text.isEmpty())
            throw new RuntimeException("Add content to the CSV only after adding the headers.")
        text.add(cells.join(","))
    }

    def write(String name)
    {
        text.write(name)
    }
}
