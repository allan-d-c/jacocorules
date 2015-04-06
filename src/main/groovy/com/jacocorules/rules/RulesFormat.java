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

package com.jacocorules.rules;

/**
 * This encapsulates the exact format expected by a rules report, both column headings
 * and coverage metric names.
 */
public interface RulesFormat
{
    String PACKAGE = "PACKAGE";
    String CLASS   = "CLASS";
    String MISSED  = "ABC_MISSED";
    String COVERED = "ABC_COVERED";
    String LIMIT   = "LIMIT";

    String[] EXPECTED_COLUMNS = { PACKAGE, CLASS, MISSED, COVERED, LIMIT };

    interface Metric
    {
        interface Prefix
        {
            String LINE    = "LINE";
            String BRANCH  = "BRANCH";
        }

        interface Suffix
        {
            String MISSED  = "_MISSED";
            String COVERED = "_COVERED";
        }
    }
}
