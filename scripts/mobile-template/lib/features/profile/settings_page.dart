import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import '../../core/l10n/app_localizations.dart';
import '../../core/state/app_state.dart';
import '../../core/widgets/language_sheet.dart';

/// Settings: language, theme and (mock) notification preferences.
class SettingsPage extends StatelessWidget {
  const SettingsPage({super.key});

  @override
  Widget build(BuildContext context) {
    final loc = AppLocalizations.of(context)!;
    final locale = context.watch<LocaleNotifier>();
    final theme = context.watch<ThemeNotifier>();

    return Scaffold(
      appBar: AppBar(title: Text(loc.settingsTitle)),
      body: ListView(
        children: [
          const SizedBox(height: 8),
          _header(context, loc.language),
          ListTile(
            leading: const Icon(Icons.language),
            title: Text(loc.language),
            subtitle: Text(
              locale.tag == null
                  ? loc.systemDefault
                  : LocaleNotifier.displayNames[locale.tag ?? 'en'] ?? '',
            ),
            trailing: const Icon(Icons.chevron_right_rounded),
            onTap: () => showLanguageSheet(context),
          ),
          const Divider(),
          _header(context, loc.theme),
          RadioGroup<String>(
            groupValue: theme.modeTag,
            onChanged: (v) {
              if (v != null) context.read<ThemeNotifier>().setMode(v);
            },
            child: Column(
              children: [
                RadioListTile<String>(
                  value: 'light',
                  title: Text(loc.themeLight),
                ),
                RadioListTile<String>(
                  value: 'dark',
                  title: Text(loc.themeDark),
                ),
                RadioListTile<String>(
                  value: 'system',
                  title: Text(loc.systemDefault),
                ),
              ],
            ),
          ),
          const Divider(),
          _header(context, loc.notificationsPrefs),
          SwitchListTile(
            value: true,
            onChanged: (_) {
              ScaffoldMessenger.of(context).showSnackBar(
                SnackBar(content: Text('${loc.notificationsPrefs} (mock)')),
              );
            },
            title: Text(loc.notificationsPrefs),
          ),
        ],
      ),
    );
  }

  Widget _header(BuildContext context, String title) {
    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
      child: Text(
        title,
        style: Theme.of(context)
            .textTheme
            .titleSmall
            ?.copyWith(color: Theme.of(context).colorScheme.primary),
      ),
    );
  }
}
