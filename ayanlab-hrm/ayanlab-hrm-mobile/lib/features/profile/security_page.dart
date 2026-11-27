import 'package:flutter/material.dart';

import '../../core/l10n/app_localizations.dart';
import '../staffing/account_pages.dart';

/// Security & privacy: mock change-password, 2FA toggle, sessions list.
class SecurityPage extends StatefulWidget {
  const SecurityPage({super.key});

  @override
  State<SecurityPage> createState() => _SecurityPageState();
}

class _SecurityPageState extends State<SecurityPage> {
  bool _twoFactor = false;

  @override
  Widget build(BuildContext context) {
    final loc = AppLocalizations.of(context)!;

    return Scaffold(
      appBar: AppBar(title: Text(loc.securityTitle)),
      body: ListView(
        children: [
          ListTile(
            leading: const Icon(Icons.password_rounded),
            title: Text(loc.changePassword),
            trailing: const Icon(Icons.chevron_right_rounded),
            onTap: () => Navigator.of(context).push(
              MaterialPageRoute<void>(builder: (_) => const ChangePasswordPage()),
            ),
          ),
          SwitchListTile(
            secondary: const Icon(Icons.verified_user_rounded),
            value: _twoFactor,
            onChanged: (v) => setState(() => _twoFactor = v),
            title: Text(loc.twoFactor),
          ),
          const Divider(),
          Padding(
            padding: const EdgeInsets.all(16),
            child: Text(loc.activeSessions,
                style: Theme.of(context).textTheme.titleMedium),
          ),
          for (final (device, place) in [
            ('iPhone 15', 'Istanbul, TR'),
            ('MacBook Pro', 'Istanbul, TR'),
          ])
            ListTile(
              leading: const Icon(Icons.devices_rounded),
              title: Text(device),
              subtitle: Text(place),
              trailing: IconButton(
                icon: const Icon(Icons.logout_rounded),
                onPressed: () {
                  ScaffoldMessenger.of(context).showSnackBar(
                    SnackBar(content: Text('$device (mock)')),
                  );
                },
              ),
            ),
        ],
      ),
    );
  }
}
