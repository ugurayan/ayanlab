import 'package:flutter/material.dart';

import '../../core/l10n/app_localizations.dart';

/// Terms of service & privacy policy (mock legal text).
class TermsPage extends StatelessWidget {
  const TermsPage({super.key});

  @override
  Widget build(BuildContext context) {
    final loc = AppLocalizations.of(context)!;

    return Scaffold(
      appBar: AppBar(title: Text(loc.termsTitle)),
      body: ListView(
        padding: const EdgeInsets.all(24),
        children: [
          Text(loc.termsTitle, style: Theme.of(context).textTheme.titleLarge),
          const SizedBox(height: 8),
          Text(loc.termsBody),
          const SizedBox(height: 24),
          Text(loc.privacyTitle, style: Theme.of(context).textTheme.titleLarge),
          const SizedBox(height: 8),
          Text(loc.termsBody),
        ],
      ),
    );
  }
}
