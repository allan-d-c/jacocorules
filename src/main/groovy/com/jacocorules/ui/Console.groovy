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

package com.jacocorules.ui

import com.jacocorules.rules.NanoResult

/**
 * This is a way to keep from littering the code with println calls.
 * By using type-specific methods, even if they are resolved to println
 * calls, we can get flexibility in testing.
 */
class Console
{
    static Console ui = new Console()

    void show(Object s)
    {
    }

    void showResult(NanoResult r)
    {
        show(r)
    }
}
