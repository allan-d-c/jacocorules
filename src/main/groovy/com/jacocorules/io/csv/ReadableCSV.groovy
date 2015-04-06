/*
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

import com.jacocorules.io.ReadableText

/**
 * Encapsulates the idea of lines of text that can be parsed into rows
 * of cells (the concept of CSV data).
 */
class ReadableCSV extends ReadableText
{
    protected List<String> headers = []
    private List<List<String>> cells = []

    ReadableCSV(String name) {
        super(name)
    }

    ReadableCSV(TextSource source)
    {
        super(source)
    }

    protected def lineRead(line)
    {
        final String REGEX = /,(?=([^\"]*\"[^\"]*\")*[^\"]*$)/

        def these = []

        line.split(REGEX).each {String cell ->
            these << cell.replaceAll(/"/, '').trim()
        }

        if (!these.isEmpty())
        {
            if (headers.isEmpty())
                headers = these
            else
                cells << these
        }
    }

    List<String> headers()
    {
        headers.clone()
    }

    List<String> content(int i)
    {
        cells.get(i)
    }

    int count()
    {
        cells.size()
    }

    def eachRow(Closure callback)
    {
        cells.each { List<String> row -> callback(row) }
    }

    def eachRowKeyed(Closure callback)
    {
        cells.each { List<String> row ->
            def keyed = [headers, row].transpose().collectEntries { it }
            callback(keyed)
        }
    }

    String toString()
    {
        "FileName: $source.name, Headers: $headers, Content: $cells"
    }
}
