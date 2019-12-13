#!/bin/sh

h=docs/examples.html
echo "<!doctype html><html><body>" >$h
for a in docs/*.afl ; do
    ./gradlew run --args="--output=$a.dot $a"
    dot $a.dot -Tsvg -o $a.svg
    {
        echo "<hr><pre><code>"
        cat $a
        echo "</code></pre>"
        echo "<img src=\"${a#*/}.svg\">"
    }>>$h
done
echo "</body></html>" >>$h
