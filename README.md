Introduction
============

JaCoCoRules is a Groovy application that can be used to enforce multiple coverage limits
against a given code base. Conventional build systems have a single, global limit enforcement
for any given coverage metric. But JaCoCoRules allows the writing of specific rules that allow
different coverage limits to apply per package or per class. This handles a vexing use
case of dealing with legacy code, which usually has low coverage, but requiring that all
new code meet higher coverage requirements.

Rules
=====

JaCoCoRules takes a JaCoCo coverage report (CSV) and a rules file as input. It applies those
rules to the given coverage measurements and fails with an error if any of the rules are
violated. 

The rules are applied in order, so the first rule that matches is applied. A given coverage
row will never contribute to coverage calculations of more than one rule. This is very
important and supports the main use case of allowing legacy code to be isolated.

In the examples that follow, we will focus on LINE missed and covered, but the
discussion applies to BRANCH as well. (A future version of JaCoCoRules will handle other
metrics as well)

Rules Example: Single, Global Coverage Limit
--------------------------------------------

An example rules file contents, a comma-separated-values (CSV) format, is shown below:

    PACKAGE,CLASS,ABC_MISSED,ABC_COVERED,LIMIT
    *,*,LINE_MISSED,LINE_COVERED,0.5

The headers are:

- PACKAGE - the name of the target classes package
- CLASS - the simple name of the target class
- ABC_MISSED - the name of the "missed" metric, like LINE_MISSED or BRANCH_MISSED
- ABC_COVERED - the name of the "covered" metric, like LINE_COVERED or BRANCH_COVERED
- LIMIT - a number from 0.0 up to 1.0, where 1.0 means 100% coverage

This content has a single rule:

- \* - this package value matches all packages
- \* - this class value matches all classes
- LINE_MISSED - this is the name for the missed metric
- LINE_COVERED - this is the name for the covered metric
- 0.5 - this indicates 50% coverage required

So the example line means that all packages and all classes taken in aggregate need to
satisfy a 50% coverage limit when applied to any JaCoCo coverage report.

Rules Example: Specific Package Limit
-------------------------------------

Consider:

    PACKAGE,CLASS,ABC_MISSED,ABC_COVERED,LIMIT
    com.acme,*,LINE_MISSED,LINE_COVERED,0.2
    *,*,LINE_MISSED,LINE_COVERED,0.5

For these rules, all of the classes in the package com.acme when taken in aggregate need
to have only 20% coverage. All other classes taken in aggregate must have 50% coverage.
So this example shows a single package that is allowed to have lower coverage than the
rest of the code base.

Consider:

    PACKAGE,CLASS,ABC_MISSED,ABC_COVERED,LIMIT
    com.acme,*,LINE_MISSED,LINE_COVERED,0.2
    com.apex,*,LINE_MISSED,LINE_COVERED,0.33
    *,*,LINE_MISSED,LINE_COVERED,0.5

These rules are like the previous except that a second package com.acme has an aggregate
coverage requirement of 33%

Rules Example: Each Class Limit
-------------------------------

Consider:

    PACKAGE,CLASS,ABC_MISSED,ABC_COVERED,LIMIT
    com.acme,[],LINE_MISSED,LINE_COVERED,0.2
    *,*,LINE_MISSED,LINE_COVERED,0.5

**We have introduced a new syntax using [].** For these rules, each class within the com.acme
package needs to have at least a 20% coverage. If we had used a * here, then all the classes
in that package in aggregate would need to meet coverage.

Rules Example: Each Package Limit
---------------------------------

Consider:

    PACKAGE,CLASS,ABC_MISSED,ABC_COVERED,LIMIT
    [],*,LINE_MISSED,LINE_COVERED,0.2

Each package must have all the classes in aggregate meeting a coverage limit of at least 20%.

Rules Semantics
---------------

All the combinations of package and class values that can be used as a rule specification are
shown below. A P indicates a literal package name, and a C indicates a literal class name.

<table border=0 cellspacing=1>
<tr>
<th>Package Name</th><th>Class Name</th><th>Matches</th>
</tr>
<tr>
<td>P</td><td>C</td><td>package P and class C</td>
</tr>
<tr>
<td>P</td><td>[]</td><td>package P and each class in that package</td>
</tr>
<tr>
<td>P</td><td>*</td><td>package P and all classes in P in aggregate</td>
</tr>
<tr>
<td>*</td><td>C</td><td>undefined/not implemented</td>
</tr>
<tr>
<td>*</td><td>[]</td><td>undefined/not implemented</td>
</tr>
<tr>
<td>*</td><td>*</td><td>all classes across all packages in aggregate</td>
</tr>
<tr>
<td>[]</td><td>C</td><td>undefined/not implemented</td>
</tr>
<tr>
<td>[]</td><td>[]</td><td>each class of each package</td>
</tr>
<tr>
<td>[]</td><td>*</td><td>all classes in aggregate of each package</td>
</tr>
</table>

Snapshots
=========

To help get started with existing code bases, JaCoCoRules can create a body of rules that
represent the current coverage state of your code base. By using the "snapshot" option, JaCoCoRules
takes the incoming coverage report and creates an output rules file that can be tweaked and then
used to enforce coverage limits.