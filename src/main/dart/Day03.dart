import 'Util.dart';

void main() {
  var day = "Day03";

  assert(part1("${day}_test") == 157);
  assert(part1("${day}") == 7917);

  assert(part2("${day}_test") == 70);
  assert(part2("${day}") == 2585);
}

int part1(String input) {
  return readInput(input)
      .map((e) => splitInHalf(e))
      .map((e) => toCharSets(e))
      .map((e) => findDuplicate(e[0], e[1]))
      .map((e) => priority(e))
      .reduce((value, element) => value + element);
}

int part2(String input) {
  return 0;
}

List<String> splitInHalf(String s) {
  return [s.substring(0, s.length ~/ 2), s.substring(s.length ~/ 2, s.length)];
}

List<Set<int>> toCharSets(List<String> groups) {
  return groups.map((e) => e.codeUnits.toSet()).toList();
}

int findDuplicate(Set<int> a, Set<int> b) {
  return a.where((element) => b.contains(element)).first;
}

int priority(int char) {
  if (char < 97) {
    return char - 64 + 26;
  } else {
    return char - 96;
  }
}

bool contained(List<int> a, List<int> b) {
  return a[0] <= b[0] && b[1] <= a[1];
}

bool overlaps(List<int> a, List<int> b) {
  return a[0] <= b[0] && b[0] <= a[1] || a[0] <= b[1] && b[1] <= a[1];
}
