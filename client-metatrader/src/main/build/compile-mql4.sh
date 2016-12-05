#!/bin/bash
METAEDITOR="$1"

BASE_DIR="`dirname $0`/../../.."
TARGET="$BASE_DIR/target"
MQL_SOURCE="$BASE_DIR/src/main/mq4"
MQL_BUILD="$TARGET/mq4"

echo compiling mq4 files to "$MQL_BUILD"

mkdir $TARGET
rm -r $MQL_BUILD
cp -r $MQL_SOURCE $TARGET
wine "$METAEDITOR" /compile:"`winepath -w $MQL_BUILD`"

# return status of metaeditor seams to be allways non-zero, at least when executed with wine
exit 0