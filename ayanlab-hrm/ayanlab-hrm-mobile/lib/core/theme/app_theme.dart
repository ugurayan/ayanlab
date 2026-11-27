import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

import '../../features/staffing/staffing_theme.dart';

/// App-wide light and dark themes (Material 3), aligned with HRM web tokens.
class AppTheme {
  AppTheme._();

  static const Color accent = S.accent;
  static const Color ink = Color(0xFF161616);
  static const Color muted = Color(0xFF8E8E93);
  static const Color line = Color(0xFFE6E6EA);
  static const Color surface = Color(0xFFF6F6F8);

  static ThemeData get light => _theme(Brightness.light);

  static ThemeData get dark => _theme(Brightness.dark);

  static ThemeData _theme(Brightness brightness) {
    final dark = brightness == Brightness.dark;
    final scheme = ColorScheme.fromSeed(
      seedColor: accent,
      brightness: brightness,
      primary: accent,
      surface: dark ? const Color(0xFF111113) : Colors.white,
    );

    final base = dark ? const Color(0xFFF4F4F5) : ink;
    final canvas = dark ? const Color(0xFF111113) : Colors.white;
    final field = dark ? const Color(0xFF1C1C1E) : surface;

    final textTheme = TextTheme(
      headlineSmall: TextStyle(
        color: base,
        fontWeight: FontWeight.w700,
        letterSpacing: -0.3,
        fontSize: 22,
      ),
      titleLarge: TextStyle(
        color: base,
        fontWeight: FontWeight.w600,
        letterSpacing: -0.2,
        fontSize: 17,
      ),
      titleMedium: TextStyle(
        color: base,
        fontWeight: FontWeight.w600,
        fontSize: 16,
      ),
      bodyLarge: TextStyle(color: base, fontSize: 15, height: 1.4),
      bodyMedium: TextStyle(color: base, fontSize: 14, height: 1.4),
      labelLarge: TextStyle(
        color: base,
        fontWeight: FontWeight.w700,
        letterSpacing: 0.4,
        fontSize: 14,
      ),
    );

    return ThemeData(
      useMaterial3: true,
      brightness: brightness,
      colorScheme: scheme,
      scaffoldBackgroundColor: canvas,
      textTheme: textTheme,
      dividerColor: dark ? const Color(0xFF2C2C2E) : line,
      appBarTheme: AppBarTheme(
        centerTitle: true,
        backgroundColor: canvas,
        foregroundColor: base,
        elevation: 0,
        scrolledUnderElevation: 0,
        titleTextStyle: textTheme.titleLarge,
        systemOverlayStyle:
            dark ? SystemUiOverlayStyle.light : SystemUiOverlayStyle.dark,
      ),
      navigationBarTheme: NavigationBarThemeData(
        height: 64,
        backgroundColor: canvas,
        elevation: 0,
        surfaceTintColor: Colors.transparent,
        indicatorColor: accent.withValues(alpha: 0.12),
        labelTextStyle: WidgetStateProperty.resolveWith((states) {
          final selected = states.contains(WidgetState.selected);
          return TextStyle(
            fontSize: 12,
            fontWeight: selected ? FontWeight.w600 : FontWeight.w500,
            color: selected ? accent : muted,
          );
        }),
        iconTheme: WidgetStateProperty.resolveWith((states) {
          final selected = states.contains(WidgetState.selected);
          return IconThemeData(
            color: selected ? accent : muted,
            size: 22,
          );
        }),
      ),
      inputDecorationTheme: InputDecorationTheme(
        filled: true,
        fillColor: field,
        contentPadding: const EdgeInsets.symmetric(horizontal: 14, vertical: 14),
        border: OutlineInputBorder(
          borderRadius: BorderRadius.circular(12),
          borderSide: BorderSide(color: dark ? const Color(0xFF2C2C2E) : line),
        ),
        enabledBorder: OutlineInputBorder(
          borderRadius: BorderRadius.circular(12),
          borderSide: BorderSide(color: dark ? const Color(0xFF2C2C2E) : line),
        ),
        focusedBorder: OutlineInputBorder(
          borderRadius: BorderRadius.circular(12),
          borderSide: BorderSide(color: base, width: 1.4),
        ),
        hintStyle: TextStyle(color: dark ? const Color(0xFFA1A1AA) : muted, fontSize: 15),
      ),
      textButtonTheme: TextButtonThemeData(
        style: TextButton.styleFrom(
          foregroundColor: accent,
          textStyle: const TextStyle(fontWeight: FontWeight.w600),
        ),
      ),
      filledButtonTheme: FilledButtonThemeData(
        style: FilledButton.styleFrom(
          backgroundColor: accent,
          foregroundColor: Colors.white,
          minimumSize: const Size.fromHeight(48),
          shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
          textStyle: const TextStyle(fontWeight: FontWeight.w700, letterSpacing: 0.4),
        ),
      ),
      outlinedButtonTheme: OutlinedButtonThemeData(
        style: OutlinedButton.styleFrom(
          foregroundColor: base,
          minimumSize: const Size.fromHeight(48),
          side: BorderSide(color: dark ? const Color(0xFF2C2C2E) : line),
          shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
          textStyle: const TextStyle(fontWeight: FontWeight.w700, letterSpacing: 0.4),
        ),
      ),
      cardTheme: CardThemeData(
        color: canvas,
        elevation: 0,
        shape: RoundedRectangleBorder(
          borderRadius: BorderRadius.circular(12),
          side: BorderSide(color: dark ? const Color(0xFF2C2C2E) : line),
        ),
        margin: EdgeInsets.zero,
      ),
      listTileTheme: ListTileThemeData(
        contentPadding: const EdgeInsets.symmetric(horizontal: 16),
        iconColor: base,
      ),
    );
  }
}