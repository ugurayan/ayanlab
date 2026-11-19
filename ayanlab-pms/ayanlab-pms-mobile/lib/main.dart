import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:shared_preferences/shared_preferences.dart';

import 'core/l10n/app_localizations.dart';
import 'core/state/app_state.dart';
import 'core/theme/app_theme.dart';
import 'features/auth/forgot_password_page.dart';
import 'features/auth/login_page.dart';
import 'features/auth/onboarding_page.dart';
import 'features/auth/register_page.dart';
import 'features/auth/splash_page.dart';
import 'features/profile/about_page.dart';
import 'features/profile/edit_profile_page.dart';
import 'features/profile/help_page.dart';
import 'features/profile/security_page.dart';
import 'features/profile/settings_page.dart';
import 'features/profile/terms_page.dart';
import 'features/pms/pms_pages.dart';
import 'features/shell/main_shell.dart';

Future<void> main() async {
  WidgetsFlutterBinding.ensureInitialized();
  final prefs = await SharedPreferences.getInstance();

  final session = SessionNotifier(prefs);
  final locale = LocaleNotifier(prefs);
  final theme = ThemeNotifier(prefs);
  await restoreAppState(session: session, locale: locale, theme: theme);

  runApp(
    MultiProvider(
      providers: [
        ChangeNotifierProvider.value(value: session),
        ChangeNotifierProvider.value(value: locale),
        ChangeNotifierProvider.value(value: theme),
      ],
      child: const AyanLabApp(),
    ),
  );
}

class AyanLabApp extends StatelessWidget {
  const AyanLabApp({super.key});

  @override
  Widget build(BuildContext context) {
    final locale = context.watch<LocaleNotifier>();
    final theme = context.watch<ThemeNotifier>();

    return MaterialApp(
      onGenerateTitle: (context) => AppLocalizations.of(context)!.appName,
      localizationsDelegates: AppLocalizations.localizationsDelegates,
      supportedLocales: AppLocalizations.supportedLocales,
      locale: locale.locale,
      theme: AppTheme.light,
      darkTheme: AppTheme.dark,
      themeMode: theme.mode,
      debugShowCheckedModeBanner: false,
      home: const _SplashGate(),
      routes: {
        '/onboarding': (_) => const OnboardingPage(),
        '/login': (_) => const LoginPage(),
        '/register': (_) => const RegisterPage(),
        '/forgot': (_) => const ForgotPasswordPage(),
        '/shell': (_) => const MainShell(),
        '/profile/edit': (_) => const EditProfilePage(),
        '/settings': (_) => const SettingsPage(),
        '/security': (_) => const SecurityPage(),
        '/help': (_) => const HelpPage(),
        '/about': (_) => const AboutPage(),
        '/terms': (_) => const TermsPage(),
        '/pms/dues': (_) => const DuesPage(),
        '/pms/maintenance': (_) => const MaintenancePage(),
        '/pms/violations': (_) => const ViolationsPage(),
        '/pms/approvals': (_) => const ApprovalsPage(),
        '/pms/patrol': (_) => const PatrolPage(),
        '/pms/parcels': (_) => const ParcelsPage(),
        '/pms/ats': (_) => const AtsPage(),
        '/pms/announcements': (_) => const AnnouncementsPage(),
      },
    );
  }
}

/// Waits for the first frame so SharedPreferences-backed state is restored,
/// then shows the splash with the onboarding flag.
class _SplashGate extends StatelessWidget {
  const _SplashGate();

  @override
  Widget build(BuildContext context) {
    return FutureBuilder<bool>(
      future: SharedPreferences.getInstance()
          .then((p) => p.getBool('onboarding.seen') ?? false),
      builder: (context, snapshot) {
        return SplashPage(onboardingSeen: snapshot.data ?? false);
      },
    );
  }
}
