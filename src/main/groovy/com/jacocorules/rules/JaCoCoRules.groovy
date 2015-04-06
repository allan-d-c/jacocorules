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

package com.jacocorules.rules

import com.jacocorules.io.csv.WriteableCSV
import com.jacocorules.misc.MultiMap4
import com.jacocorules.report.JaCoCoReport

/**
 * This is the main logic for the utility. It reads and instantiates rules from a text
 * rules file (CSV) and can both validate coverage rules are met and can generate
 * example rules from the coverage report.
 */
class JaCoCoRules
{
    private def debug = true
    private def rules = [:]
    private MultiMap4<List<Tally>> accumulated = new MultiMap4<List<Tally>>()
    private List<NanoResult> results = []

    JaCoCoRules()
    {
    }

    JaCoCoRules(String rulesName)
    {
        JaCoCoRulesCSV csv = new JaCoCoRulesCSV(rulesName)
        csv.readAndValidate()
        initRules(csv)
    }

    private def initRules(JaCoCoRulesCSV csv)
    {
        csv.eachRowKeyed {
            Map<String,String> hash ->
                String pkg = hash[RulesFormat.PACKAGE]
                String clazz = hash[RulesFormat.CLASS]
                String missd = hash[RulesFormat.MISSED]
                String covd = hash[RulesFormat.COVERED]
                Double limit = hash[RulesFormat.LIMIT].toDouble()

                ensure(rules, pkg, clazz, missd, covd, Tally.zero())
                rules[pkg][clazz][missd][covd] = new Tally(0, 0, limit)
        }

        if (rules.isEmpty())
            throw new RuntimeException("There were no rules found in "+csv.name())
    }

    def ingest(JaCoCoReport report)
    {
        report.eachRowKeyed({ this.checkOne(it) })
    }

    private def checkOne(Map<String,String> item)
    {
        String thispkg = item[RulesFormat.PACKAGE]
        String thisclass = item[RulesFormat.CLASS]

        Map pkgchunk = null
        String pkgkey = null

        [thispkg, "[]", "*"].find {
            if (rules.containsKey(it)) {
                pkgchunk = rules[it]
                pkgkey = it
                return true
            }

            false
        }

        if (pkgchunk == null)
            throw new RuntimeException("Could not locate a rule for package $thispkg (with class $thisclass)")

        Map classchunk = null
        String classkey = null

        [thisclass, "[]", "*"].find {
            if (pkgchunk.containsKey(it)) {
                classchunk = pkgchunk[it]
                classkey = it
                return true
            }

            false
        }

        if (classchunk == null)
            throw new RuntimeException("Could not locate a rule for class $thisclass (in class $thispkg)")

        def isWild = { String s -> s == "*" }
        def isEach = { String s -> s == '[]' }
        def isSpecific = { String s -> !isWild(s) && !isEach(s) }

        if (isWild(pkgkey) && !isWild(classkey)) // * C, * []
        {
            throw new RuntimeException("Rules like ...,*,class,... are not supported")
        }

        if (isEach(pkgkey) && isSpecific(classkey)) // [] C
        {
            throw new RuntimeException("Rules like ...,[],class,... are not supported")
        }

        // Remaining possibilities: P C, P [], P *, * *, [] [], [] *

        classchunk.each {
            left, rights ->
                rights.each {
                    right, tally ->
                        Double part1 = Double.parseDouble(item[left])
                        Double part2 = Double.parseDouble(item[right])
                        Double limit = tally.limit()

                        if (part1 || part2)
                        {
                            def newTally = new Tally(part1, part2, limit)

                            if (!isWild(pkgkey) && !isWild(classkey)) // P C, P [], [] []
                            {
                                violateIfLow(thispkg, thisclass, left, right, newTally)
                            }

                            if (!isWild(pkgkey)) // P C, P [], [] [], P *
                            {
                                accumulate(thispkg, '*', left, right, newTally)
                            }

                            if (isWild(pkgkey) && isWild(classkey)) // * *
                            {
                                accumulate('*', '*', left, right, newTally)
                            }
                        }
                }
        }
    }

    def throwViolations()
    {
        def problems = violations()
        if (!problems.isEmpty())
            throw new RuntimeException(problems.join("\n"))
    }

    def checkAll()
    {
        accumulated.eachEntry {
            k1, k2, k3, k4, tally ->
                violateIfLow(k1, k2, k3, k4, tally)
        }
    }

    private def violateIfLow(String pkgKey, String classKey, String left, String right, Tally tally)
    {
        Double candidate = tally.covered()/(tally.covered()+tally.missed())
        boolean failure = candidate < tally.limit()

        if (failure || debug)
        {
            def r = new NanoResult()
                .forPackage(pkgKey)
                .forClass(classKey)
                .withMetric(left, tally.missed())
                .withMetric(right, tally.covered())
                .hitting(candidate, tally.limit())
                .isViolation(failure)

            results << r
        }
    }

    private def accumulate(String pkgKey, String classKey, String missedName, String coveredName, Tally delta)
    {
        accumulated.ensure(pkgKey, classKey, missedName, coveredName, Tally.zero())
        def current = accumulated.get(pkgKey, classKey, missedName, coveredName)
        current.add(delta)
    }

    def extend (Map<String,String> item)
    {
        String thispkg = item[RulesFormat.PACKAGE]
        String thisclass = item[RulesFormat.CLASS]

        [RulesFormat.Metric.Prefix.LINE, RulesFormat.Metric.Prefix.BRANCH].each {
            String key ->
                String missKey = key + RulesFormat.Metric.Suffix.MISSED
                String covKey = key + RulesFormat.Metric.Suffix.COVERED

                Double miss = item[missKey].toDouble()
                Double cover = item[covKey].toDouble()
                Double limit = (miss+cover) ? cover/(miss+cover) : 0

                ensure(rules, thispkg, thisclass, missKey, covKey, Tally.zero())
                rules[thispkg][thisclass][missKey][covKey] = new Tally(miss, cover, limit)
        }
    }

    def extend()
    {
        def morphedRules = [:]

        each(rules, {
            k1, k2, k3, k4, from ->
                def missCovLim = ensure(morphedRules, k1, "*", k3, k4, Tally.zero())
                missCovLim.add(from)
        })

        each(morphedRules, {
            k1, k2, k3, k4, missCovLim ->
                ensure(rules, k1, k2, k3, k4, Tally.zero())
                rules[k1][k2][k3][k4] = missCovLim.renew()
        })
    }

    def write(WriteableCSV outCsv)
    {
        outCsv.addHeaders(RulesFormat.EXPECTED_COLUMNS)

        each(rules, {
            k1, k2, k3, k4, tally ->
                outCsv.add([k1, k2, k3, k4, tally.limitAsString()])
        })
    }

    private def each(Map map1, Closure callback)
    {
        map1.sort().each {
             k1, map2 ->
                 map2.sort().each {
                     k2, map3 ->
                         map3.sort().each {
                             k3, map4 ->
                                 map4.sort().each {
                                     k4, v ->
                                         callback(k1, k2, k3, k4, v)
                                 }
                         }
                 }
         }
    }

    private Tally ensure(Map map1, String key1, String key2, String key3, String key4, Tally defValue)
    {
        if (!map1.containsKey(key1))
            map1[key1] = [:]

        Map map2 = map1[key1]

        if (!map2.containsKey(key2))
            map2[key2] = [:]

        Map map3 = map2[key2]

        if(!map3.containsKey(key3))
            map3[key3] = [:]

        Map map4 = map3[key3]

        if (!map4.containsKey(key4))
            map4[key4] = defValue

        map4[key4]
    }

    List<NanoResult> violations()
    {
        results.grep { it.violated() }
    }

    def results()
    {
        this.results
    }
}
