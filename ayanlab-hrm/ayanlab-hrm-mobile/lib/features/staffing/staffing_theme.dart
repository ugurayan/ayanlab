import 'package:flutter/material.dart';

/// Visual language shared by the staffing screens.
/// White canvas, hairline lists, and a single accent for actions.
class S {
  S._();

  static const accent = Color(0xFFE2186F);
  static const green = Color(0xFF34C759);
  static const danger = Color(0xFFB42318);

  static bool dark(BuildContext context) =>
      Theme.of(context).brightness == Brightness.dark;

  static Color bg(BuildContext context) =>
      dark(context) ? const Color(0xFF111113) : Colors.white;

  static Color ink(BuildContext context) =>
      dark(context) ? const Color(0xFFF4F4F5) : const Color(0xFF161616);

  static Color muted(BuildContext context) =>
      dark(context) ? const Color(0xFFA1A1AA) : const Color(0xFF8E8E93);

  static Color line(BuildContext context) =>
      dark(context) ? const Color(0xFF2C2C2E) : const Color(0xFFE6E6EA);

  static Color field(BuildContext context) =>
      dark(context) ? const Color(0xFF1C1C1E) : const Color(0xFFF2F2F4);

  static Color chip(BuildContext context) =>
      dark(context) ? const Color(0xFF2A2A2E) : const Color(0xFFE9E9ED);

  static Color surface(BuildContext context) =>
      dark(context) ? const Color(0xFF1A1A1C) : const Color(0xFFF6F6F8);

  static List<BoxShadow> cardShadow(BuildContext context) => [
        BoxShadow(
          color: ink(context).withValues(alpha: dark(context) ? 0.35 : 0.06),
          blurRadius: 14,
          offset: const Offset(0, 4),
        ),
        BoxShadow(
          color: ink(context).withValues(alpha: dark(context) ? 0.2 : 0.04),
          blurRadius: 2,
          offset: const Offset(0, 1),
        ),
      ];

  static TextStyle title(BuildContext context) => TextStyle(
        color: ink(context),
        fontSize: 17,
        fontWeight: FontWeight.w600,
        letterSpacing: -0.2,
      );

  static TextStyle section(BuildContext context) => TextStyle(
        color: ink(context),
        fontSize: 16,
        fontWeight: FontWeight.w700,
      );

  static TextStyle body(BuildContext context) => TextStyle(
        color: ink(context),
        fontSize: 15,
        height: 1.35,
      );

  static TextStyle caption(BuildContext context) => TextStyle(
        color: muted(context),
        fontSize: 13,
        height: 1.35,
      );
}

InputDecoration staffField(
  BuildContext context, {
  String? hint,
  bool filled = false,
}) {
  final border = OutlineInputBorder(
    borderRadius: BorderRadius.circular(10),
    borderSide: BorderSide(color: S.line(context)),
  );
  return InputDecoration(
    hintText: hint,
    hintStyle: TextStyle(color: S.muted(context), fontSize: 15),
    filled: filled,
    fillColor: filled ? S.field(context) : S.bg(context),
    isDense: true,
    contentPadding: const EdgeInsets.symmetric(horizontal: 14, vertical: 14),
    border: border,
    enabledBorder: border,
    focusedBorder: OutlineInputBorder(
      borderRadius: BorderRadius.circular(10),
      borderSide: BorderSide(color: S.ink(context), width: 1.4),
    ),
  );
}

String money(double value) {
  final negative = value < 0;
  final fixed = value.abs().toStringAsFixed(2).split('.');
  final whole = fixed[0];
  final buf = StringBuffer();
  for (var i = 0; i < whole.length; i++) {
    if (i > 0 && (whole.length - i) % 3 == 0) buf.write("'");
    buf.write(whole[i]);
  }
  return "${negative ? '-' : ''}CHF $buf.${fixed[1]}";
}

String hhmm(int minutes) {
  final m = minutes.clamp(0, 24 * 60);
  final h = (m ~/ 60).toString().padLeft(2, '0');
  final min = (m % 60).toString().padLeft(2, '0');
  return '$h:$min';
}

String breakLabel(int minutes) {
  if (minutes <= 0) return 'no break';
  if (minutes % 60 == 0) {
    final h = minutes ~/ 60;
    return '$h h break';
  }
  return '$minutes min break';
}

const _months = [
  'January',
  'February',
  'March',
  'April',
  'May',
  'June',
  'July',
  'August',
  'September',
  'October',
  'November',
  'December',
];

const _monthsShort = [
  'Jan',
  'Feb',
  'Mar',
  'Apr',
  'May',
  'Jun',
  'Jul',
  'Aug',
  'Sep',
  'Oct',
  'Nov',
  'Dec',
];

const _weekdays = [
  'Monday',
  'Tuesday',
  'Wednesday',
  'Thursday',
  'Friday',
  'Saturday',
  'Sunday',
];

const _weekdaysShort = ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'];

String monthName(DateTime date) => _months[date.month - 1];

String monthShort(DateTime date) => _monthsShort[date.month - 1];

String weekdayName(DateTime date) => _weekdays[date.weekday - 1];

String weekdayShort(DateTime date) => _weekdaysShort[date.weekday - 1];

String longDate(DateTime date) =>
    '${weekdayName(date)}, ${date.day} ${monthName(date)}';

String dayMonth(DateTime date) => '${date.day} ${monthShort(date)}';
