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

import com.jacocorules.rules.NanoResult
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

class JaCoCoRulesAppSpec extends Specification
{
    @Rule
    TemporaryFolder tempFolder // spock initializes this!

    def RPT_HEADER = "GROUP,PACKAGE,CLASS,INSTRUCTION_MISSED,INSTRUCTION_COVERED,BRANCH_MISSED,BRANCH_COVERED,LINE_MISSED,LINE_COVERED,COMPLEXITY_MISSED,COMPLEXITY_COVERED,METHOD_MISSED,METHOD_COVERED"
    def RULES_HEADER = "PACKAGE,CLASS,ABC_MISSED,ABC_COVERED,LIMIT"

    def "each package with each class meeting coverage works"()
    {
        given:
        File rpt = tempFolder.newFile("Test_Each_Each_Pass_Report.csv")
        rpt << RPT_HEADER + "\n"
        rpt << "JaCoCo,com.abc.foo,ClassA,2,1,2,1,4,6,2,1,2,1\n"
        rpt << "JaCoCo,com.abc.foo,ClassB,2,1,2,1,3,3,2,1,2,1\n"

        and:
        File rules = tempFolder.newFile("Test_Each_Each_Pass_Rules.csv")
        rules << RULES_HEADER + "\n"
        rules << "[],[],LINE_MISSED,LINE_COVERED,0.5\n"

        when:
        def args = new Arguments().process("-input",rpt.absolutePath,"-rules",rules.absolutePath)
        def app = new JaCoCoRulesApp(args)

        then:
        def expected1 = new NanoResult()
                .forPackage("com.abc.foo")
                .forClass("ClassA")
                .withMetric("LINE_MISSED", 4.0)
                .withMetric("LINE_COVERED", 6.0)
                .hitting(6.0/10.0, 0.5)
        def expected2 = new NanoResult()
                .forPackage("com.abc.foo")
                .forClass("ClassB")
                .withMetric("LINE_MISSED", 3.0)
                .withMetric("LINE_COVERED", 3.0)
                .hitting(3.0/6.0, 0.5)
        def expected3 = new NanoResult()
                .forPackage("com.abc.foo")
                .forClass("*")
                .withMetric("LINE_MISSED", 7.0)
                .withMetric("LINE_COVERED", 9.0)
                .hitting(9.0/16.0, 0.5)
        app.results() == [expected1, expected2, expected3]
    }

    def "each package with each class not meeting coverage is caught"()
    {
        given:
        File rpt = tempFolder.newFile("Test_Each_Each_Fail_Report.csv")
        rpt << RPT_HEADER + "\n"
        rpt << "JaCoCo,com.abc.foo,ClassA,2,1,2,1,4,2,2,1,2,1\n"
        rpt << "JaCoCo,com.abc.foo,ClassB,2,1,2,1,2,3,2,1,2,1\n"

        and:
        File rules = tempFolder.newFile("Test_Each_Each_Fail_Rules.csv")
        rules << RULES_HEADER + "\n"
        rules << "[],[],LINE_MISSED,LINE_COVERED,0.5\n"

        when:
        def args = new Arguments().process("-nothrow", "-input",rpt.absolutePath,"-rules",rules.absolutePath)
        def app = new JaCoCoRulesApp(args)

        then:
        def expected1 = new NanoResult()
                .forPackage("com.abc.foo")
                .forClass("ClassA")
                .withMetric("LINE_MISSED", 4.0)
                .withMetric("LINE_COVERED", 2.0)
                .hitting(2.0/6.0, 0.5)
                .isViolation(true)
        def expected2 = new NanoResult()
                .forPackage("com.abc.foo")
                .forClass("ClassB")
                .withMetric("LINE_MISSED", 2.0)
                .withMetric("LINE_COVERED", 3.0)
                .hitting(3.0/5.0, 0.5)
        def expected3 = new NanoResult()
                .forPackage("com.abc.foo")
                .forClass("*")
                .withMetric("LINE_MISSED", 6.0)
                .withMetric("LINE_COVERED", 5.0)
                .hitting(5.0/11.0, 0.5)
                .isViolation(true)
        app.results() == [expected1, expected2, expected3]
    }

    def "each package with in-aggregate classes meeting coverage works"()
    {
        given:
        File rpt = tempFolder.newFile("Test_Each_All_Pass_Report.csv")
        rpt << RPT_HEADER + "\n"
        rpt << "JaCoCo,com.abc.foo,ClassA,2,1,2,1,4,2,2,1,2,1\n"
        rpt << "JaCoCo,com.abc.foo,ClassB,2,1,2,1,2,4,2,1,2,1\n"
        rpt << "JaCoCo,com.abc.bar,ClassC,2,1,2,1,4,2,2,1,2,1\n"
        rpt << "JaCoCo,com.abc.bar,ClassD,2,1,2,1,0,1,2,1,2,1\n"
        rpt << "JaCoCo,com.abc.bar,ClassE,2,1,2,1,0,1,2,1,2,1\n"

        and:
        File rules = tempFolder.newFile("Test_Each_All_Pass_Rules.csv")
        rules << RULES_HEADER + "\n"
        rules << "[],*,LINE_MISSED,LINE_COVERED,0.5\n"

        when:
        def args = new Arguments().process("-input",rpt.absolutePath,"-rules",rules.absolutePath)
        def app = new JaCoCoRulesApp(args)

        then:
        def expected1 = new NanoResult()
                .forPackage("com.abc.foo")
                .forClass("*")
                .withMetric("LINE_MISSED", 6.0)
                .withMetric("LINE_COVERED", 6.0)
                .hitting(0.5, 0.5)
        def expected2 = new NanoResult()
                .forPackage("com.abc.bar")
                .forClass("*")
                .withMetric("LINE_MISSED", 4.0)
                .withMetric("LINE_COVERED", 4.0)
                .hitting(0.5, 0.5)
        app.results() == [expected1, expected2]
    }

    def "each package with in-aggregate classes not meeting coverage is caught"()
    {
        given:
        File rpt = tempFolder.newFile("Test_Each_All_Fail_Report.csv")
        rpt << RPT_HEADER + "\n"
        rpt << "JaCoCo,com.abc.foo,ClassA,2,1,2,1,4,2,2,1,2,1\n"
        rpt << "JaCoCo,com.abc.foo,ClassB,2,1,2,1,2,3,2,1,2,1\n"

        and:
        File rules = tempFolder.newFile("Test_Each_All_Fail_Rules.csv")
        rules << RULES_HEADER + "\n"
        rules << "[],*,LINE_MISSED,LINE_COVERED,0.5"

        when:
        def args = new Arguments().process("-nothrow", "-input",rpt.absolutePath,"-rules",rules.absolutePath)
        def app = new JaCoCoRulesApp(args)

        then:
        def expected = new NanoResult()
                .forPackage("com.abc.foo")
                .forClass("*")
                .withMetric("LINE_MISSED", 6.0)
                .withMetric("LINE_COVERED", 5.0)
                .hitting(5.0/11.0, 0.5)
                .isViolation(true)
        app.results() == [expected]
    }

    def "in-aggregate packages with in-aggregate classes meeting coverage works"()
    {
        given:
        File rpt = tempFolder.newFile("Test_All_All_Pass_Report.csv")
        rpt << RPT_HEADER + "\n"
        rpt << "JaCoCo,com.abc.foo,ClassA,2,1,2,1,4,2,2,1,2,1\n"
        rpt << "JaCoCo,com.abc.bar,ClassB,2,1,2,1,2,4,2,1,2,1\n"

        and:
        File rules = tempFolder.newFile("Test_All_All_Pass_Rules.csv")
        rules << RULES_HEADER + "\n"
        rules << "*,*,LINE_MISSED,LINE_COVERED,0.5\n"

        when:
        def args = new Arguments().process("-input",rpt.absolutePath,"-rules",rules.absolutePath)
        def app = new JaCoCoRulesApp(args)

        then:
        def expected = new NanoResult()
                .forPackage("*")
                .forClass("*")
                .withMetric("LINE_MISSED", 6.0)
                .withMetric("LINE_COVERED", 6.0)
                .hitting(0.5, 0.5)
        app.results() == [expected]
    }

    def "in-aggregate packages with in-aggregate classes not meeting coverage is caught"()
    {
        given:
        File rpt = tempFolder.newFile("Test_All_All_Fail_Report.csv")
        rpt << RPT_HEADER + "\n"
        rpt << "JaCoCo,com.abc.foo,ClassA,2,1,2,1,4,2,2,1,2,1\n"

        and:
        File rules = tempFolder.newFile("Test_All_All_Fail_Rules.csv")
        rules << RULES_HEADER + "\n"
        rules << "*,*,LINE_MISSED,LINE_COVERED,0.5\n"

        when:
        def args = new Arguments().process("-nothrow", "-input", rpt.absolutePath,"-rules",rules.absolutePath)
        def app = new JaCoCoRulesApp(args)

        then:
        def expected = new NanoResult()
                .forPackage("*")
                .forClass("*")
                .withMetric("LINE_MISSED", 4.0)
                .withMetric("LINE_COVERED", 2.0)
                .hitting(2.0/6.0, 0.5)
                .isViolation(true)
        app.results() == [expected]
    }

    def "mixed in-aggregate rules work"()
    {
        given:
        File rpt = tempFolder.newFile("Test_Compound_All_Report.csv")
        rpt << RPT_HEADER + "\n"
        rpt << "JaCoCo,com.abc.foo,ClassA,2,1,2,1,4,2,2,1,2,1\n"
        rpt << "JaCoCo,com.abc.bar,ClassB,2,1,2,1,2,4,2,1,2,1\n"

        and:
        File rules = tempFolder.newFile("Test_Compound_All_Rules.csv")
        rules << RULES_HEADER + "\n"
        rules << "com.abc.bar,*,LINE_MISSED,LINE_COVERED,0.5\n"
        rules << "*,*,LINE_MISSED,LINE_COVERED,0.5\n"

        when:
        def args = new Arguments().process("-nothrow","-input",rpt.absolutePath,"-rules",rules.absolutePath)
        def app = new JaCoCoRulesApp(args)

        then:
        def expected1 = new NanoResult()
                .forPackage("*")
                .forClass("*")
                .withMetric("LINE_MISSED", 4.0)
                .withMetric("LINE_COVERED", 2.0)
                .hitting(2.0/6.0, 0.5)
                .isViolation(true)

        def expected2 = new NanoResult()
                .forPackage("com.abc.bar")
                .forClass("*")
                .withMetric("LINE_MISSED", 2.0)
                .withMetric("LINE_COVERED", 4.0)
                .hitting(2.0/3.0, 0.5)

        app.results() == [expected1, expected2]
    }

    def "specific package with in-aggregate classes meeting coverage works"()
    {
        given:
        File rpt = tempFolder.newFile("Test_Pkg_All_Pass_Report.csv")
        rpt << RPT_HEADER + "\n"
        rpt << "JaCoCo,com.abc.foo,ClassA,2,1,2,1,4,1,2,1,2,1\n"
        rpt << "JaCoCo,com.abc.foo,ClassB,2,1,2,1,0,1,2,1,2,1\n"
        rpt << "JaCoCo,com.abc.foo,ClassC,2,1,2,1,0,1,2,1,2,1\n"
        rpt << "JaCoCo,com.abc.foo,ClassD,2,1,2,1,0,1,2,1,2,1\n"

        and:
        File rules = tempFolder.newFile("Test_Pkg_All_Pass_Rules.csv")
        rules << RULES_HEADER + "\n"
        rules << "com.abc.foo,*,LINE_MISSED,LINE_COVERED,0.5\n"

        when:
        def args = new Arguments().process("-input",rpt.absolutePath,"-rules",rules.absolutePath)
        def app = new JaCoCoRulesApp(args)

        then:
        def expected = new NanoResult()
                .forPackage("com.abc.foo")
                .forClass("*")
                .withMetric("LINE_MISSED", 4.0)
                .withMetric("LINE_COVERED", 4.0)
                .hitting(0.5, 0.5)
        app.results() == [expected]
    }

    def "specific package with in-aggregate classes not meeting coverage is caught"()
    {
        given:
        File rpt = tempFolder.newFile("Test_Pkg_All_Fail_Report.csv")
        rpt << RPT_HEADER + "\n"
        rpt << "JaCoCo,com.abc.foo,ClassA,2,1,2,1,3,1,2,1,2,1\n"

        and:
        File rules = tempFolder.newFile("Test_Pkg_All_Fail_Rules.csv")
        rules << RULES_HEADER + "\n"
        rules << "com.abc.foo,*,LINE_MISSED,LINE_COVERED,0.5\n"

        when:
        def args = new Arguments().process("-nothrow", "-input",rpt.absolutePath,"-rules",rules.absolutePath)
        def app = new JaCoCoRulesApp(args)

        then:
        def expected = new NanoResult()
                .forPackage("com.abc.foo")
                .forClass("*")
                .withMetric("LINE_MISSED", 3.0)
                .withMetric("LINE_COVERED", 1.0)
                .hitting(0.25, 0.5)
                .isViolation(true)
        app.results() == [expected]
    }

    def "a coverage row should never match more than one rule"()
    {
        given:
        File rpt = tempFolder.newFile("OneRuleMatch_Report.csv")
        rpt << RPT_HEADER + "\n"
        rpt << "JaCoCo,com.abc.foo,ClassA,2,1,2,1,3,1,2,1,2,1\n"

        and:
        File rules = tempFolder.newFile("OneRuleMatch_Rules.csv")
        rules << RULES_HEADER + "\n"
        rules << "com.abc.foo,*,LINE_MISSED,LINE_COVERED,0.25\n"
        rules << "*,*,LINE_MISSED,LINE_COVERED,0.6\n"

        when:
        def args = new Arguments().process("-nothrow","-input",rpt.absolutePath,"-rules",rules.absolutePath)
        def app = new JaCoCoRulesApp(args)

        then:
        def expected = new NanoResult()
                .forPackage("com.abc.foo")
                .forClass("*")
                .withMetric("LINE_MISSED", 3.0)
                .withMetric("LINE_COVERED", 1.0)
                .hitting(0.25, 0.25)
        app.results() == [expected]
    }

    def "coverage that does not match any rule should be an error"()
    {
        given:
        File rpt = tempFolder.newFile("Test_Unmatched_Report.csv")
        rpt << RPT_HEADER + "\n"
        rpt << "JaCoCo,com.abc.bar,ClassX,2,1,2,1,4,1,2,1,2,1\n"

        and:
        File rules = tempFolder.newFile("Test_Unmatched_Rules.csv")
        rules << RULES_HEADER + "\n"
        rules << "com.abc.foo,*,LINE_MISSED,LINE_COVERED,0.5\n"

        when:
        def args = new Arguments().process("-input",rpt.absolutePath,"-rules",rules.absolutePath)
        new JaCoCoRulesApp(args)

        then:
        RuntimeException e = thrown()
        e.message =~ /com.abc.bar/
    }

    def "can generate a rules snapshot from a given coverage report"()
    {
        given:
        File expected = tempFolder.newFile("Snapshot_Expected_Rules.csv")
        expected << RULES_HEADER + "\n"
        expected << "com.acme.half,*,BRANCH_MISSED,BRANCH_COVERED,0.50\n"
        expected << "com.acme.half,*,LINE_MISSED,LINE_COVERED,0.50\n"
        expected << "com.acme.half,ClassA,BRANCH_MISSED,BRANCH_COVERED,0.60\n"
        expected << "com.acme.half,ClassA,LINE_MISSED,LINE_COVERED,0.60\n"
        expected << "com.acme.half,ClassB,BRANCH_MISSED,BRANCH_COVERED,0.40\n"
        expected << "com.acme.half,ClassB,LINE_MISSED,LINE_COVERED,0.40\n"
        expected << "com.acme.third,*,BRANCH_MISSED,BRANCH_COVERED,0.33\n"
        expected << "com.acme.third,*,LINE_MISSED,LINE_COVERED,0.33\n"
        expected << "com.acme.third,ClassC,BRANCH_MISSED,BRANCH_COVERED,0.33\n"
        expected << "com.acme.third,ClassC,LINE_MISSED,LINE_COVERED,0.33\n"
        expected << "com.acme.third,ClassD,BRANCH_MISSED,BRANCH_COVERED,0.33\n"
        expected << "com.acme.third,ClassD,LINE_MISSED,LINE_COVERED,0.33\n"
        expected << "com.acme.third,ClassE,BRANCH_MISSED,BRANCH_COVERED,0.33\n"
        expected << "com.acme.third,ClassE,LINE_MISSED,LINE_COVERED,0.33\n"

        and:
        File report = tempFolder.newFile("Snapshot_Report.csv")
        report << RPT_HEADER + "\n"
        report << "Group1,com.acme.half,ClassA,0,100,20,30,2,3,0,100,0,100\n"
        report << "Group1,com.acme.half,ClassB,0,100,30,20,3,2,0,100,0,100\n"
        report << "Group2,com.acme.third,ClassC,0,100,20,10,20,10,0,100,0,100\n"
        report << "Group2,com.acme.third,ClassD,0,100,20,10,20,10,0,100,0,100\n"
        report << "Group2,com.acme.third,ClassE,0,100,20,10,20,10,0,100,0,100\n"

        and:
        File found = tempFolder.newFile("Snapshot_Found_Rules.csv")

        when:
        def args = new Arguments().process("-input",report.absolutePath,"-snapshot",found.absolutePath)
        new JaCoCoRulesApp(args)

        then:
        found.readLines() == expected.readLines()
    }
}
