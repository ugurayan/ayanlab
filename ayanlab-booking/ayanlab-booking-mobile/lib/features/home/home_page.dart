import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import '../../core/l10n/app_localizations.dart';
import '../../core/mock/mock_data.dart';
import '../../core/state/app_state.dart';

/// Home tab: greeting, quick actions, recent items, FAB target.
class HomePage extends StatelessWidget {
  const HomePage({super.key});

  @override
  Widget build(BuildContext context) {
    final loc = AppLocalizations.of(context)!;
    final session = context.watch<SessionNotifier>();

    final quickActions = [
      (Icons.add_circle_rounded, loc.fabLabel),
      (Icons.search_rounded, loc.tabSearch),
      (Icons.notifications_rounded, loc.tabNotifications),
      (Icons.settings_rounded, loc.settingsTitle),
    ];

    return ListView(
      padding: const EdgeInsets.all(16),
      children: [
        Card(
          child: Padding(
            padding: const EdgeInsets.all(16),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  loc.homeGreeting(session.name),
                  style: Theme.of(context).textTheme.titleLarge,
                ),
                const SizedBox(height: 4),
                Text(loc.mockNotice,
                    style: Theme.of(context).textTheme.bodySmall),
              ],
            ),
          ),
        ),
        const SizedBox(height: 16),
        Text(loc.homeQuickActions,
            style: Theme.of(context).textTheme.titleMedium),
        const SizedBox(height: 8),
        GridView.count(
          crossAxisCount: 2,
          shrinkWrap: true,
          physics: const NeverScrollableScrollPhysics(),
          mainAxisSpacing: 8,
          crossAxisSpacing: 8,
          childAspectRatio: 2.4,
          children: [
            for (final (icon, label) in quickActions)
              Card(
                child: InkWell(
                  borderRadius: BorderRadius.circular(12),
                  onTap: () {
                    ScaffoldMessenger.of(context).showSnackBar(
                      SnackBar(content: Text('$label (mock)')),
                    );
                  },
                  child: Center(
                    child: Row(
                      mainAxisSize: MainAxisSize.min,
                      children: [
                        Icon(icon),
                        const SizedBox(width: 8),
                        Flexible(child: Text(label)),
                      ],
                    ),
                  ),
                ),
              ),
          ],
        ),
        const SizedBox(height: 16),
        Text(loc.homeRecent, style: Theme.of(context).textTheme.titleMedium),
        const SizedBox(height: 8),
        for (final item in mockRecentItems)
          Card(
            child: ListTile(
              leading: Icon(item.icon),
              title: Text(item.title),
              subtitle: Text(item.subtitle),
              trailing: const Icon(Icons.chevron_right_rounded),
              onTap: () {
                ScaffoldMessenger.of(context).showSnackBar(
                  SnackBar(content: Text('${item.title} (mock)')),
                );
              },
            ),
          ),
      ],
    );
  }
}
