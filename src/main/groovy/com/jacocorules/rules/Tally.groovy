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

/**
 * A Tally is a low level accumulator for keeping track of how many covered and uncovered
 * lines have been seen. It also carries along a supplied limit (the ratio of covered to
 * the total lines), which can be recalculated on demand.
 */
class Tally
{
    static def zero()
    {
        new Tally(0.0, 0.0, 0.0)
    }

    private Double missd
    private Double covd
    private Double lim

    Tally()
    {
        missd = covd = lim = 0
    }

    Tally(Double miss, Double cov, Double limit)
    {
        missd = miss
        covd = cov
        lim = limit
    }

    def missed()
    {
        missd
    }

    def covered()
    {
        covd
    }

    def limit()
    {
        lim
    }

    def add(Tally other)
    {
        missd += other.missd
        covd += other.covd
        if (lim == 0)
            lim = other.lim
    }

    Tally renew()
    {
        def limit = (missd+covd) ? covd/(missd+covd) : 0
        new Tally(missd, covd, limit)
    }

    def limitAsString()
    {
        sprintf("%.2f", 1.0*lim)
    }

    String toString()
    {
        "missed: $missd, covered: $covd, limit: $lim"
    }
}
