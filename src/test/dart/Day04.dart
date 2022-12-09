import 'Util.dart';

void main() {
  var day = "Day04";

  assert(part1("${day}_test") == 2);
  print("part 1: ${part1(day)}");

  assert(part2("${day}_test") == 4);
  print("part 2: ${part2(day)}");
}

int part1(String input) {
  return readInput(input).where((e) {
    var groups = e.split(",");
    var g1 = groups[0].split("-").map((i) => int.parse(i)).toList();
    var g2 = groups[1].split("-").map((i) => int.parse(i)).toList();

    return contained(g1, g2) || contained(g2, g1);
  }).length;
}

int part2(String input) {
  return readInput(input).where((e) {
    var groups = e.split(",");
    var g1 = groups[0].split("-").map((i) => int.parse(i)).toList();
    var g2 = groups[1].split("-").map((i) => int.parse(i)).toList();
    return overlaps(g1, g2) || overlaps(g2, g1);
  }).length;
}

bool contained(List<int> a, List<int> b) {
  return a[0] <= b[0] && b[1] <= a[1];
}

bool overlaps(List<int> a, List<int> b) {
  return a[0] <= b[0] && b[0] <= a[1] || a[0] <= b[1] && b[1] <= a[1];
}
