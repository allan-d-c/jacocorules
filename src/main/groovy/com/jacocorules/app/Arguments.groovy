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

/**
 * Class to manage processing and validating of the command line arguments.
 * It throws RuntimeExceptions on validation failures. It collects values and
 * provides convenient wrapper access.
 */
class Arguments
{
    private final INPUT = "-input"
    private final RULES = "-rules"
    private final DEBUG = "-debug"
    private final SNAPSHOT = "-snapshot"
    private final NOTHROW = "-nothrow"

    private def inputs = []
    private def rules = []
    private def snapshots = []
    private def debug = false
    private def nothrow = false // switch for easier testing

    Arguments()
    {
    }

    Arguments(String... args)
    {
        processAndValidate(args)
    }

    def process (String... args)
    {
        def isInput = false
        def isRules = false
        def isSnapShot = false

        args.each {
            switch(it)
            {
                case INPUT:
                    isInput = true
                    break
                case RULES:
                    isRules = true
                    break
                case DEBUG:
                    debug = true
                    break
                case SNAPSHOT:
                    isSnapShot = true
                    break
                case NOTHROW:
                    nothrow = true
                    break
                default:
                    if (isInput)
                        inputs.add(it)
                    else if (isRules)
                        rules.add(it)
                    else if (isSnapShot)
                        snapshots.add(it)
                    isInput = isRules = isSnapShot = false
                    break
            }
        }

        this
    }

    def reset()
    {
        inputs = []
        rules = []
        snapshots = []
        debug = false
        nothrow = false
    }

    def validate()
    {
        if (inputs.isEmpty())
            throw new RuntimeException("No input supplied. Please use -input on the command line.")

        inputs.each {
            def f = new File(it)
            if (!f.exists())
                throw new RuntimeException("The following file does not exist: $it")
        }

        if (!isSnapshot() && rules.isEmpty())
            throw new RuntimeException("No rules supplied. Please use -rules on the command line.")

        if (isSnapshot() && !rules.isEmpty())
            throw new RuntimeException("Cannot use explicit rules with the -snapshot option.")

        rules.each {
            def f = new File(it)
            if (f.exists() && !f.canWrite())
                throw new RuntimeException("The following file exists but is not writable: $it")
        }

        this
    }

    def processAndValidate (String... args)
    {
        process(args)
        validate()
        this
    }

    def inputSummaryNames()
    {
        inputs.clone()
    }

    def inputSummaryName(int i)
    {
        inputs[i]
    }

    def inputRulesNames()
    {
        rules.clone()
    }

    def inputRulesName(int i)
    {
        rules[i]
    }

    def snapShotNames()
    {
        snapshots.clone()
    }

    def snapShotName(int i)
    {
        snapshots[i]
    }

    def isSnapshot()
    {
        !snapshots.isEmpty()
    }

    def isDebug()
    {
        debug
    }

    def isNoThrow()
    {
        nothrow
    }

    String toString()
    {
        "Inputs: $inputs, Rules: $rules, Debug: $debug, Snapshot: $snapshots"
    }
}
