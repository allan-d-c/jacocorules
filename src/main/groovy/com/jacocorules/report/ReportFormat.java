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

package com.jacocorules.report;

/**
 * Convenient place to park expected format values for a legal JaCoCo coverage report.
 * Allows a place for changes to coverage report format to be handled.
 */
public interface ReportFormat
{
    static String[] EXPECTED_COLUMNS = {"GROUP", "PACKAGE", "CLASS", "INSTRUCTION_MISSED", "INSTRUCTION_COVERED", "BRANCH_MISSED", "BRANCH_COVERED", "LINE_MISSED", "LINE_COVERED", "COMPLEXITY_MISSED", "COMPLEXITY_COVERED", "METHOD_MISSED", "METHOD_COVERED"};
}
