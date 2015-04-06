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

package com.jacocorules.app

import com.jacocorules.io.csv.WriteableCSV
import com.jacocorules.report.JaCoCoReport
import com.jacocorules.rules.JaCoCoRules
import com.jacocorules.ui.Console
import com.jacocorules.ui.PrintingConsole


/**
 * This utility takes an input JaCoCo coverage table and an input set of CSV rules and applies
 * those rules to the coverages. It will print diagnostics and return a failure code if the rules
 * cannot be passed by the coverage results.
 *
 * Example rules where
 *   P = package name
 *   C = class name
 *   Missed = LINE_MISSED or BRANCH_MISSED
 *   Covered = LINE_COVERED or BRANCH_COVERED
 *   Lim = minimum coverage limit (0 - 1.0)
 *
 *   P  C  Missed Covered Lim ->means specific package P and class C should have Lim limit
 *   P  [] Missed Covered Lim -> each class in package P should have Lim limit
 *   P  *  Missed Covered Lim -> aggregate of all classes in package P should have Lim limit
 *   [] [] Missed Covered Lim -> each class in each package should have Lim limit
 *   [] *  Missed Covered Lim -> aggregate of all classes in each package should have Lim limit
 *   *  *  Missed Covered Lim -> global aggregate should have Lim limit
 */

/**
 * Main application class for the utility, driven exclusively by supplied arguments.
 */
class JaCoCoRulesApp
{
    private def results = []

    JaCoCoRulesApp(Arguments parsedArgs)
    {
        Console.ui = (parsedArgs.isDebug()) ? new PrintingConsole() : new Console()

        Console.ui.show(parsedArgs)

        def reportName = parsedArgs.inputSummaryName(0)
        def report = new JaCoCoReport(reportName)

        if (parsedArgs.isSnapshot())
        {
            def rules = new JaCoCoRules()
            report.eachRowKeyed { Map item -> rules.extend(item) }

            rules.extend()
            WriteableCSV csvOut = new WriteableCSV()
            rules.write(csvOut)

            def snapName = parsedArgs.snapShotName(0)
            csvOut.write(snapName)

            Console.ui.show("Snapshot written to file $snapName\n")
        }
        else
        {
            def rulesName = parsedArgs.inputRulesName(0)
            def rules = new JaCoCoRules(rulesName)

            rules.ingest(report)
            rules.checkAll()

            results = rules.results()
            if (parsedArgs.isDebug())
                results.each { Console.ui.showResult(it) }

            if (!parsedArgs.isNoThrow())
                rules.throwViolations()

            Console.ui.show("Coverage checkAll passed\n")
        }
    }

    def results()
    {
        this.results
    }

    static def main(args)
    {
        new JaCoCoRulesApp(new Arguments().processAndValidate(args))
    }
}
