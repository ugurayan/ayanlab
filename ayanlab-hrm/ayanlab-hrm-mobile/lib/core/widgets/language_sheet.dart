import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import '../../core/state/app_state.dart';
import '../l10n/app_localizations.dart';

/// Bottom sheet listing the 11 supported languages.
Future<void> showLanguageSheet(BuildContext context) {
  final loc = AppLocalizations.of(context)!;
  final localeNotifier = context.read<LocaleNotifier>();

  return showModalBottomSheet<void>(
    context: context,
    builder: (sheetContext) => SafeArea(
      child: ListView(
        shrinkWrap: true,
        children: [
          ListTile(
            title: Text(loc.language),
            leading: const Icon(Icons.language),
          ),
          const Divider(height: 1),
          for (final tag in LocaleNotifier.supportedTags)
            ListTile(
              title: Text(LocaleNotifier.displayNames[tag] ?? tag),
              trailing: localeNotifier.tag == tag
                  ? const Icon(Icons.check)
                  : null,
              onTap: () {
                localeNotifier.setTag(tag);
                Navigator.of(sheetContext).pop();
              },
            ),
          ListTile(
            title: Text(loc.systemDefault),
            trailing: localeNotifier.tag == null
                ? const Icon(Icons.check)
                : null,
            onTap: () {
              localeNotifier.setTag(null);
              Navigator.of(sheetContext).pop();
            },
          ),
        ],
      ),
    ),
  );
}
