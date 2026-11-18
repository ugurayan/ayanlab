import 'package:flutter/material.dart';

/// App-wide light and dark themes (Material 3).
class AppTheme {
  AppTheme._();

  static const Color _seed = Color(0xFF4F6DF5);

  static ThemeData get light => ThemeData(
        useMaterial3: true,
        colorScheme: ColorScheme.fromSeed(seedColor: _seed),
      );

  static ThemeData get dark => ThemeData(
        useMaterial3: true,
        colorScheme: ColorScheme.fromSeed(
          seedColor: _seed,
          brightness: Brightness.dark,
        ),
      );
}
