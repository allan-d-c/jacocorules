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
 * Encapsulates the notion of line separator to support running in
 * non-Windows environments.
 */
class Format
{
    private final String sep = "\n"

    Format()
    {
        def alternate = System.getProperty("line.separator")
        if (alternate != null && !alternate.isEmpty())
            sep = alternate
    }

    def sep()
    {
        sep
    }
}
