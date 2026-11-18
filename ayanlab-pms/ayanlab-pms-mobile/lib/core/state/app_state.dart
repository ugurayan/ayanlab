import 'dart:async';

import 'package:flutter/material.dart';
import 'package:shared_preferences/shared_preferences.dart';

/// Mock session: no API, login just flips a flag and stores a display name.
class SessionNotifier extends ChangeNotifier {
  SessionNotifier(this._prefs);

  final SharedPreferences _prefs;

  static const _keyLoggedIn = 'mock.logged_in';
  static const _keyName = 'mock.user_name';
  static const _keyEmail = 'mock.user_email';

  bool _loggedIn = false;
  String _name = '';
  String _email = '';

  bool get isLoggedIn => _loggedIn;
  String get name => _name;
  String get email => _email;

  void restore() {
    _loggedIn = _prefs.getBool(_keyLoggedIn) ?? false;
    _name = _prefs.getString(_keyName) ?? 'Alex Demo';
    _email = _prefs.getString(_keyEmail) ?? 'alex.demo@example.com';
    notifyListeners();
  }

  void login({String email = 'alex.demo@example.com'}) {
    _loggedIn = true;
    _email = email;
    _name = email.split('@').first;
    _prefs.setBool(_keyLoggedIn, true);
    _prefs.setString(_keyName, _name);
    _prefs.setString(_keyEmail, _email);
    notifyListeners();
  }

  void logout() {
    _loggedIn = false;
    _prefs.setBool(_keyLoggedIn, false);
    notifyListeners();
  }
}

/// Active app locale; null means "follow the system".
class LocaleNotifier extends ChangeNotifier {
  LocaleNotifier(this._prefs);

  final SharedPreferences _prefs;

  static const _keyLanguage = 'app.language';

  /// Supported language tags (matches the ARB files).
  static const supportedTags = <String>[
    'en', 'zh', 'hi', 'es', 'fr', 'ar', 'bn', 'pt', 'ru', 'tr', 'ur',
  ];

  static const displayNames = <String, String>{
    'en': 'English',
    'zh': '中文 (Chinese)',
    'hi': 'हिन्दी (Hindi)',
    'es': 'Español',
    'fr': 'Français',
    'ar': 'العربية (Arabic)',
    'bn': 'বাংলা (Bengali)',
    'pt': 'Português',
    'ru': 'Русский',
    'tr': 'Türkçe',
    'ur': 'اردو (Urdu)',
  };

  String? _tag;

  String? get tag => _tag;

  Locale? get locale => _tag == null ? null : Locale(_tag!);

  void restore() {
    _tag = _prefs.getString(_keyLanguage);
    notifyListeners();
  }

  void setTag(String? tag) {
    _tag = tag;
    if (tag == null) {
      _prefs.remove(_keyLanguage);
    } else {
      _prefs.setString(_keyLanguage, tag);
    }
    notifyListeners();
  }
}

/// Light/dark theme selection; null means "follow the system".
class ThemeNotifier extends ChangeNotifier {
  ThemeNotifier(this._prefs);

  final SharedPreferences _prefs;

  static const _keyTheme = 'app.theme';

  String? _mode; // 'light' | 'dark' | null (system)

  ThemeMode get mode {
    switch (_mode) {
      case 'light':
        return ThemeMode.light;
      case 'dark':
        return ThemeMode.dark;
      default:
        return ThemeMode.system;
    }
  }

  String? get modeTag => _mode;

  void restore() {
    _mode = _prefs.getString(_keyTheme);
    notifyListeners();
  }

  void setMode(String? mode) {
    _mode = mode == 'system' ? null : mode;
    if (_mode == null) {
      _prefs.remove(_keyTheme);
    } else {
      _prefs.setString(_keyTheme, _mode!);
    }
    notifyListeners();
  }
}

/// Restores persisted state once at startup.
Future<void> restoreAppState({
  required SessionNotifier session,
  required LocaleNotifier locale,
  required ThemeNotifier theme,
}) async {
  session.restore();
  locale.restore();
  theme.restore();
}
