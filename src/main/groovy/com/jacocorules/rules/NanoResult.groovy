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

import groovy.transform.EqualsAndHashCode

/**
 * The result of applying a coverage report against a set of rules is a series
 * of NanoResults. This is primarily a convenience for testability but makes the
 * code a little easier to understand.
 */
@EqualsAndHashCode
class NanoResult
{
    private String pkg = "UnknownPackage"
    private String clazz = "UnknownClass"
    private metrics = []
    private Double candidate = 0
    private Double limit = 0
    private Boolean violation = false

    NanoResult()
    {
    }

    def forPackage(p)
    {
        pkg = p
        this
    }

    def forClass(c)
    {
        clazz = c
        this
    }

    def withMetric(String name, Double value)
    {
        metrics << [name, value]
        this
    }

    def hitting (Double c, Double l)
    {
        candidate = c
        limit = l
        this
    }

    def isViolation(v)
    {
        violation = v
        this
    }

    def violated()
    {
        violation
    }

    boolean equals(other)
    {
        if (!(other instanceof NanoResult))
            return false

        if (pkg != other.pkg)
            return false

        if (clazz != other.clazz)
            return false

        if (metrics.size() != other.metrics.size())
            return false

        if (!closeEnough(candidate, other.candidate))
            return false

        if (limit != other.limit)
            return false

        if (violation != other.violation)
            return false

        for (int i = 0; i< metrics.size(); i++)
        {
            if (metrics[i][0] != other.metrics[i][0])
                return false

            if (!closeEnough(metrics[i][1], other.metrics[i][1]))
                return false
        }

        return true
    }

    private boolean closeEnough(double d1, double d2)
    {
        final double epsilon = 0.00001
        return Math.abs(d1-d2) < epsilon
    }

    String toString()
    {
        String msg = (violation) ? "Coverage Failure" : "Debug Coverage"
        msg += " - \n"
        msg += "\tPackage: $pkg\n"
        msg += "\tClass: $clazz\n"
        msg += "\tDetails: ("

        String phrase = "";
        metrics.each { phrase += it[0] + " = " + it[1] + " "}
        msg += phrase.trim()
        msg += ")\n"
        msg += "\t" + sprintf("%.3f", candidate) + " >= $limit\n"
        msg
    }
}
