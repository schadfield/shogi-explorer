#!/bin/sh
cp /Users/stephen/Projects/shogi-explorer/target/ShogiExplorer*-jar-with-dependencies.jar \
   /Users/stephen/Projects/Releases/Files/V2/ShogiExplorer.jar
launch4j /Users/stephen/Projects/shogi-explorer/Shogi\ Explorer.xml
cp /Users/stephen/Projects/Releases/Files/V2/ShogiExplorer.jar \
  /Users/stephen/Projects/Releases/Templates/V2/Shogi\ Explorer.app/Contents/Java/ShogiExplorer.jar
cp -pr /Users/stephen/Projects/Releases/Templates/V2/Shogi\ Explorer.app \
  /Users/stephen/Projects/Releases/Files/V2
  touch /Users/stephen/Projects/Releases/Files/V2/Shogi\ Explorer.app