import 'dart:io';

List<String> readInput(String day) {
  return File("/Users/ranil/Workspaces/aoc-2022/src/test/resources/$day.txt").readAsLinesSync();
}
