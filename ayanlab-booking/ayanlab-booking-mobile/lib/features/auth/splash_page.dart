import 'dart:async';

import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import '../../core/l10n/app_localizations.dart';
import '../../core/state/app_state.dart';

/// Splash screen: brand placeholder, then routes based on persisted state.
class SplashPage extends StatefulWidget {
  const SplashPage({super.key, required this.onboardingSeen});

  final bool onboardingSeen;

  @override
  State<SplashPage> createState() => _SplashPageState();
}

class _SplashPageState extends State<SplashPage> {
  @override
  void initState() {
    super.initState();
    Timer(const Duration(milliseconds: 1200), _route);
  }

  void _route() {
    if (!mounted) return;
    if (!widget.onboardingSeen) {
      Navigator.of(context).pushReplacementNamed('/onboarding');
    } else if (context.read<SessionNotifier>().isLoggedIn) {
      Navigator.of(context).pushReplacementNamed('/shell');
    } else {
      Navigator.of(context).pushReplacementNamed('/login');
    }
  }

  @override
  Widget build(BuildContext context) {
    final loc = AppLocalizations.of(context)!;
    return Scaffold(
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Icon(
              Icons.apps_rounded,
              size: 96,
              color: Theme.of(context).colorScheme.primary,
            ),
            const SizedBox(height: 16),
            Text(loc.appName, style: Theme.of(context).textTheme.headlineMedium),
            const SizedBox(height: 32),
            const CircularProgressIndicator(),
          ],
        ),
      ),
    );
  }
}
