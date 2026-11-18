import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import '../../core/l10n/app_localizations.dart';
import '../../core/mock/mock_data.dart';
import '../../core/state/app_state.dart';

/// Profile tab: avatar, info, stats and links to secondary pages.
class ProfilePage extends StatelessWidget {
  const ProfilePage({super.key});

  @override
  Widget build(BuildContext context) {
    final loc = AppLocalizations.of(context)!;
    final session = context.watch<SessionNotifier>();

    return ListView(
      padding: const EdgeInsets.all(16),
      children: [
        Column(
          children: [
            CircleAvatar(
              radius: 48,
              child: Text(
                session.name.isNotEmpty ? session.name[0].toUpperCase() : 'A',
                style: Theme.of(context).textTheme.headlineMedium,
              ),
            ),
            const SizedBox(height: 12),
            Text(session.name,
                style: Theme.of(context).textTheme.titleLarge),
            Text(session.email,
                style: Theme.of(context).textTheme.bodyMedium),
            Text(
              '${loc.profileMemberSince}: ${mockUser.memberSince}',
              style: Theme.of(context).textTheme.bodySmall,
            ),
          ],
        ),
        const SizedBox(height: 24),
        Row(
          mainAxisAlignment: MainAxisAlignment.spaceEvenly,
          children: [
            for (final (value, label) in [
              ('12', loc.tabNotifications),
              ('3', loc.homeQuickActions),
              ('8', loc.tabSearch),
            ])
              Column(
                children: [
                  Text(value, style: Theme.of(context).textTheme.titleLarge),
                  Text(label, style: Theme.of(context).textTheme.bodySmall),
                ],
              ),
          ],
        ),
        const SizedBox(height: 24),
        _tile(context, Icons.edit_rounded, loc.profileEdit, '/profile/edit'),
        _tile(context, Icons.settings_rounded, loc.settingsTitle, '/settings'),
        _tile(context, Icons.security_rounded, loc.securityTitle, '/security'),
        _tile(context, Icons.help_rounded, loc.helpTitle, '/help'),
        _tile(context, Icons.info_rounded, loc.aboutTitle, '/about'),
        _tile(context, Icons.gavel_rounded, loc.termsTitle, '/terms'),
      ],
    );
  }

  Widget _tile(BuildContext context, IconData icon, String title, String route) {
    return Card(
      child: ListTile(
        leading: Icon(icon),
        title: Text(title),
        trailing: const Icon(Icons.chevron_right_rounded),
        onTap: () => Navigator.of(context).pushNamed(route),
      ),
    );
  }
}
