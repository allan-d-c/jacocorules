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

import spock.lang.Specification

class ArgumentsSpec extends Specification
{
    def args = new Arguments()

    def byDefaultArgsHasNoMembers()
    {
        when:
            args.process("")

        then:
            args.inputRulesNames() == []
            args.inputSummaryNames() == []
            args.snapShotNames() == []
    }

    def singleInputSummaryNameHandledProperly()
    {
        when:
            args.process("-input", "abc")

        then:
            args.inputSummaryNames() == ["abc"]
            args.inputSummaryName(0) == "abc"
    }

    def singleInputRulesNameHandledProperly()
    {
        when:
            args.process("-rules", "abc")

        then:
            args.inputRulesNames() == ["abc"]
            args.inputRulesName(0) == "abc"
    }

    def singleSnapshotNameHandledProperly()
    {
        when:
            args.process("-snapshot", "abc")

        then:
            args.snapShotNames() == ["abc"]
            args.snapShotName(0) == "abc"
            args.isSnapshot()
    }

    def debugModeCanBeSet()
    {
        when:
            args.process("-debug")

        then:
            args.isDebug()
    }

    def argumentsCanBeReset()
    {
        given:
            def before = args.toString()
            args.process("-input", "abc", "-rules", "def", "-snapshot", "ghi", "-debug")
        when:
            args.reset()
        then:
            args.toString() == before
    }
}
