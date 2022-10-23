#!/bin/sh

here="$(dirname "$(readlink -f "$0")")"
cd "$here" || exit 1

h=docs/examples.html
if [ -e $h ] ; then
    echo "ERROR: file $h exists; will not overwrite."
    exit 1
fi

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
