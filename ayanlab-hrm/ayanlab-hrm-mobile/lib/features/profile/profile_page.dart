import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import '../../core/l10n/app_localizations.dart';
import '../../core/state/app_state.dart';
import '../../core/widgets/language_sheet.dart';
import '../notifications/notifications_page.dart';
import '../staffing/staffing_chrome.dart';
import '../staffing/staffing_store.dart';
import '../staffing/staffing_theme.dart';

/// Account tab for the person posting shifts.
class ProfilePage extends StatelessWidget {
  const ProfilePage({super.key});

  @override
  Widget build(BuildContext context) {
    final loc = AppLocalizations.of(context)!;
    final session = context.watch<SessionNotifier>();
    final initial = session.name.isNotEmpty ? session.name[0].toUpperCase() : 'A';

    return ColoredBox(
      color: S.bg(context),
      child: SafeArea(
        bottom: false,
        child: ListView(
          children: [
            Padding(
              padding: const EdgeInsets.fromLTRB(16, 16, 16, 8),
              child: Text('Profile', style: S.title(context)),
            ),
            Padding(
              padding: const EdgeInsets.fromLTRB(16, 8, 16, 16),
              child: Row(
                children: [
                  CircleAvatar(
                    radius: 28,
                    backgroundColor: S.accent.withValues(alpha: 0.12),
                    child: Text(initial, style: const TextStyle(color: S.accent, fontWeight: FontWeight.w700, fontSize: 20)),
                  ),
                  const SizedBox(width: 12),
                  Expanded(
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Text(session.name, style: S.section(context)),
                        Text(session.email, style: S.caption(context)),
                        Text(kCompanyName, style: S.caption(context)),
                      ],
                    ),
                  ),
                ],
              ),
            ),
            const Hairline(),
            _row(context, loc.profileEdit, '/profile/edit'),
            _row(context, loc.tabNotifications, null, onTap: () {
              Navigator.of(context).push(
                MaterialPageRoute<void>(
                  builder: (_) => Scaffold(
                    appBar: AppBar(title: Text(loc.tabNotifications)),
                    body: const NotificationsPage(),
                  ),
                ),
              );
            }),
            _row(context, loc.changePassword, '/account/password'),
            _row(context, loc.settingsTitle, '/settings'),
            _row(context, loc.language, null, onTap: () => showLanguageSheet(context)),
            _row(context, loc.securityTitle, '/security'),
            _row(context, loc.helpTitle, '/help'),
            _row(context, loc.aboutTitle, '/about'),
            _row(context, loc.termsTitle, '/terms'),
            _row(context, 'Delete account', '/account/delete'),
            ListTile(
              title: Text(loc.logout, style: const TextStyle(color: S.danger, fontSize: 16)),
              onTap: () {
                context.read<SessionNotifier>().logout();
                Navigator.of(context).pushNamedAndRemoveUntil('/login', (_) => false);
              },
            ),
          ],
        ),
      ),
    );
  }

  Widget _row(BuildContext context, String title, String? route, {VoidCallback? onTap}) {
    return ListTile(
      title: Text(title, style: S.body(context)),
      trailing: Icon(Icons.chevron_right, color: S.muted(context)),
      onTap: onTap ?? () => Navigator.of(context).pushNamed(route!),
    );
  }
}
