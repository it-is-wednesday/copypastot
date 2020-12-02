#!/bin/bash
for f in $(egrep -o -R "defn?-? [^ ]*" * --include '*.clj' | cut -d \  -f 2 | sort | uniq); do
    echo $f $(grep -R --include '*.clj' -- "$f" * | wc -l);
done | grep " 1$"