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

package com.jacocorules.io
/**
 * Encapsulates idea of a body of lines to be written to a file. Originally was going to
 * be a seam for better testability. May need to rethink the added complexity.
 */
class WriteableText
{
    private def lines = []

    WriteableText()
    {
    }

    def add(String s)
    {
        lines << s
    }

    def write(String name)
    {
        write(new File(name))
    }

    def write(File outputFile)
    {
        def sep = new Format().sep()

        outputFile.withWriter {
            out ->
                lines.each {
                    out.write(it)
                    out.write(sep)
                }
        }
    }

    def isEmpty()
    {
        lines.isEmpty()
    }
}
