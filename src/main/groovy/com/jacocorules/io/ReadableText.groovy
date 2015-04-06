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
 * Encapsulates idea of a body of lines to be processed. Originally was going to
 * be a seam for better testability. May need to rethink the added complexity.
 */
class ReadableText
{
    interface TextSource
    {
        String getName()
        void validate()
        void eachLine(Closure closure)
    }

    static class TextFromFile implements TextSource
    {
        private File inputFile

        TextFromFile(String name)
        {
            inputFile = new File(name)
        }

        TextFromFile(File file)
        {
            inputFile = file
        }

        String getName()
        {
            inputFile.getAbsolutePath()
        }

        void validate()
        {
            if (inputFile == null)
                throw new RuntimeException("Cannot read from a null file")
            if (!inputFile.exists())
                throw new RuntimeException("The file $inputFile does not exist")
            if (!inputFile.canRead())
                throw new RuntimeException("The file $inputFile is not readable")
        }

        void eachLine(Closure closure)
        {
            inputFile.eachLine(closure)
        }
    }

    protected TextSource source
    private def lines = []

    ReadableText(String name)
    {
        source = new TextFromFile(name)
    }

    ReadableText(File file)
    {
        source = new TextFromFile(file)
    }

    def name()
    {
        source.name
    }

    def read()
    {
        source.validate()
        readContent()
    }

    private def readContent()
    {
        source.eachLine {
            lineRead(it)
        }
    }

    protected def lineRead(line)
    {
        lines.add(line)
    }

    def getLines()
    {
        lines
    }

    String toString()
    {
        "FileName: $source.name, Lines: $lines"
    }
}
