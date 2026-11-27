import 'package:flutter/material.dart';

import '../../core/l10n/app_localizations.dart';
import '../staffing/staffing_chrome.dart';
import '../staffing/staffing_theme.dart';

/// Legal links for the staffing account.
class TermsPage extends StatelessWidget {
  const TermsPage({super.key});

  @override
  Widget build(BuildContext context) {
    final loc = AppLocalizations.of(context)!;
    return FlowPage(
      title: loc.termsTitle,
      body: ListView(
        padding: const EdgeInsets.fromLTRB(16, 12, 16, 24),
        children: [
          _link(context, loc.termsTitle, loc.termsBody),
          _link(context, loc.privacyTitle, loc.termsBody),
          _link(
            context,
            'Third-party services',
            'Maps, payments, and messaging providers process data under their own terms when those features are connected.',
          ),
          const SizedBox(height: 16),
          Text('© AyanLab. All rights reserved.', style: S.caption(context)),
        ],
      ),
    );
  }

  Widget _link(BuildContext context, String title, String body) {
    return Align(
      alignment: Alignment.centerLeft,
      child: TextButton(
        onPressed: () => Navigator.of(context).push(
          MaterialPageRoute<void>(
            builder: (_) => FlowPage(
              title: title,
              body: ListView(
                padding: const EdgeInsets.all(16),
                children: [Text(body, style: S.body(context))],
              ),
            ),
          ),
        ),
        child: Text(title),
      ),
    );
  }
}
