import 'package:flutter/material.dart';

import '../../core/l10n/app_localizations.dart';

/// About us: version and description (mock).
class AboutPage extends StatelessWidget {
  const AboutPage({super.key});

  @override
  Widget build(BuildContext context) {
    final loc = AppLocalizations.of(context)!;

    return Scaffold(
      appBar: AppBar(title: Text(loc.aboutTitle)),
      body: Center(
        child: Padding(
          padding: const EdgeInsets.all(32),
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              Icon(
                Icons.apps_rounded,
                size: 80,
                color: Theme.of(context).colorScheme.primary,
              ),
              const SizedBox(height: 16),
              Text(loc.appName, style: Theme.of(context).textTheme.headlineSmall),
              const SizedBox(height: 8),
              Text(loc.aboutBody, textAlign: TextAlign.center),
              const SizedBox(height: 16),
              Text(
                '${loc.version}: 0.1.0',
                style: Theme.of(context).textTheme.bodySmall,
              ),
              const SizedBox(height: 32),
              Text(
                loc.mockNotice,
                style: Theme.of(context).textTheme.bodySmall,
              ),
            ],
          ),
        ),
      ),
    );
  }
}
