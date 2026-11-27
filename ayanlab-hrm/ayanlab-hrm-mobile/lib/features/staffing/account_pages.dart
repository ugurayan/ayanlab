import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import '../../core/state/app_state.dart';
import 'staffing_chrome.dart';
import 'staffing_theme.dart';

class ChangePasswordPage extends StatefulWidget {
  const ChangePasswordPage({super.key});

  @override
  State<ChangePasswordPage> createState() => _ChangePasswordPageState();
}

class _ChangePasswordPageState extends State<ChangePasswordPage> {
  final _old = TextEditingController();
  final _next = TextEditingController();
  final _repeat = TextEditingController();
  var _hideOld = true;
  var _hideNext = true;
  var _hideRepeat = true;

  @override
  void dispose() {
    _old.dispose();
    _next.dispose();
    _repeat.dispose();
    super.dispose();
  }

  bool get _ready =>
      _old.text.length >= 8 &&
      _next.text.length >= 8 &&
      _next.text == _repeat.text;

  @override
  Widget build(BuildContext context) {
    return FlowPage(
      title: 'Change password',
      body: ListView(
        padding: const EdgeInsets.fromLTRB(16, 12, 16, 24),
        children: [
          _field('Old password', _old, _hideOld, () => setState(() => _hideOld = !_hideOld)),
          const SizedBox(height: 16),
          _field('New password', _next, _hideNext, () => setState(() => _hideNext = !_hideNext)),
          const SizedBox(height: 16),
          _field(
            'Repeat new password',
            _repeat,
            _hideRepeat,
            () => setState(() => _hideRepeat = !_hideRepeat),
          ),
          const SizedBox(height: 8),
          Text('Use at least 8 characters. This demo does not change a real password.', style: S.caption(context)),
        ],
      ),
      footer: StaffFooter(
        label: 'APPLY',
        onPressed: _ready
            ? () {
                Navigator.pop(context);
                ScaffoldMessenger.of(context).showSnackBar(
                  const SnackBar(content: Text('Password updated for this demo session.')),
                );
              }
            : null,
      ),
    );
  }

  Widget _field(String label, TextEditingController controller, bool hidden, VoidCallback toggle) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text(label, style: S.body(context)),
        const SizedBox(height: 8),
        TextField(
          controller: controller,
          obscureText: hidden,
          onChanged: (_) => setState(() {}),
          decoration: staffField(context).copyWith(
            suffixIcon: IconButton(
              onPressed: toggle,
              icon: Icon(hidden ? Icons.visibility_outlined : Icons.visibility_off_outlined),
            ),
          ),
        ),
      ],
    );
  }
}

class DeleteAccountPage extends StatefulWidget {
  const DeleteAccountPage({super.key});

  @override
  State<DeleteAccountPage> createState() => _DeleteAccountPageState();
}

class _DeleteAccountPageState extends State<DeleteAccountPage> {
  final _confirm = TextEditingController();

  @override
  void dispose() {
    _confirm.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final ready = _confirm.text.trim() == 'DELETE';
    return FlowPage(
      title: 'Delete account',
      closeIcon: true,
      body: ListView(
        padding: const EdgeInsets.fromLTRB(16, 12, 16, 24),
        children: [
          Text(
            'Permanently delete account?',
            textAlign: TextAlign.center,
            style: S.section(context).copyWith(fontSize: 20),
          ),
          const SizedBox(height: 8),
          Text(
            'Demo data on this device will be signed out. A live account service is not called.',
            textAlign: TextAlign.center,
            style: S.caption(context),
          ),
          const SizedBox(height: 20),
          Text('Type "DELETE" to confirm', style: S.body(context)),
          const SizedBox(height: 8),
          TextField(
            controller: _confirm,
            onChanged: (_) => setState(() {}),
            decoration: staffField(context),
          ),
        ],
      ),
      footer: StaffFooter(
        label: 'KEEP ACCOUNT',
        onPressed: () => Navigator.pop(context),
        secondary: SizedBox(
          width: double.infinity,
          height: 48,
          child: TextButton(
            onPressed: ready
                ? () {
                    context.read<SessionNotifier>().logout();
                    Navigator.of(context).pushNamedAndRemoveUntil('/login', (_) => false);
                  }
                : null,
            child: Text(
              'DELETE ACCOUNT',
              style: TextStyle(
                color: ready ? S.danger : S.muted(context),
                fontWeight: FontWeight.w700,
                letterSpacing: 0.6,
              ),
            ),
          ),
        ),
      ),
    );
  }
}
