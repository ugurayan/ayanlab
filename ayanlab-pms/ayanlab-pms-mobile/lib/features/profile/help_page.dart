import 'package:flutter/material.dart';

import '../../core/l10n/app_localizations.dart';

/// Help & support: mock FAQ list and a contact action.
class HelpPage extends StatelessWidget {
  const HelpPage({super.key});

  @override
  Widget build(BuildContext context) {
    final loc = AppLocalizations.of(context)!;
    final faqs = [
      (loc.ob1Title, loc.ob1Body),
      (loc.ob2Title, loc.ob2Body),
      (loc.ob3Title, loc.ob3Body),
    ];

    return Scaffold(
      appBar: AppBar(title: Text(loc.helpTitle)),
      body: ListView(
        padding: const EdgeInsets.all(16),
        children: [
          Text(loc.faq, style: Theme.of(context).textTheme.titleMedium),
          const SizedBox(height: 8),
          for (final (q, a) in faqs)
            Card(
              child: ExpansionTile(
                title: Text(q),
                children: [
                  Padding(
                    padding: const EdgeInsets.all(16),
                    child: Text(a),
                  ),
                ],
              ),
            ),
          const SizedBox(height: 24),
          FilledButton.icon(
            onPressed: () {
              ScaffoldMessenger.of(context).showSnackBar(
                SnackBar(content: Text('${loc.contactSupport} (mock)')),
              );
            },
            icon: const Icon(Icons.support_agent_rounded),
            label: Text(loc.contactSupport),
          ),
        ],
      ),
    );
  }
}
