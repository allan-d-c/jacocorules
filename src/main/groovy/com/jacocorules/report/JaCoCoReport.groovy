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

/**
 * Simple class that reads rules from the given file name and validates them (will throw
 * if there are errors). Allows clients to iterate over the rules using a closure.
 */
class JaCoCoReport
{
    private final JaCoCoReportCSV csv

    JaCoCoReport(String reportName)
    {
        csv = new JaCoCoReportCSV(reportName)
        csv.read()
        csv.validate()
    }

    def eachRowKeyed(Closure closure)
    {
        csv.eachRowKeyed { closure(it) }
    }
}
