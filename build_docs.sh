#!/bin/bash -x

date
whoami
pwd
git --version
java -version

git config --global user.email "git@github.com"
git config --global user.name "build.sh"

cid=$(git rev-parse HEAD)

git checkout docs
git rebase main
mkdir -p docs

./gradlew clean

h=./docs/examples.html

ls -l docs

echo "<!doctype html>" >$h
echo "<html>" >>$h
echo "<body>" >>$h
for a in ./examples/*.afl ; do
    n=$(basename $a)
    ./gradlew run --args="--output=./docs/$n.dot $a"
    dot "./docs/$n.dot" -Tsvg -o "./docs/$n.svg"
    {
        echo "<hr><pre><code>"
        cat $a
        echo "</code></pre>"
        echo "<img src=\"$n.svg\">"
    }>>$h
done
echo "</body>" >>$h
echo "</html>" >>$h

ls -l docs

git add docs
git status
git diff --cached
git commit -m "auto build of $cid"
git push -f
