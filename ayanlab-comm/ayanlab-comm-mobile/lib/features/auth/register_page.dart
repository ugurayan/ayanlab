import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import '../../core/l10n/app_localizations.dart';
import '../../core/state/app_state.dart';

/// Mock registration: creates the account locally and signs in.
class RegisterPage extends StatefulWidget {
  const RegisterPage({super.key});

  @override
  State<RegisterPage> createState() => _RegisterPageState();
}

class _RegisterPageState extends State<RegisterPage> {
  final _formKey = GlobalKey<FormState>();
  final _name = TextEditingController();
  final _email = TextEditingController();
  final _password = TextEditingController();
  final _confirm = TextEditingController();
  bool _acceptedTerms = false;

  @override
  void dispose() {
    _name.dispose();
    _email.dispose();
    _password.dispose();
    _confirm.dispose();
    super.dispose();
  }

  void _submit() {
    if (!_formKey.currentState!.validate() || !_acceptedTerms) return;
    context.read<SessionNotifier>().login(email: _email.text.trim());
    Navigator.of(context).pushNamedAndRemoveUntil('/shell', (_) => false);
  }

  @override
  Widget build(BuildContext context) {
    final loc = AppLocalizations.of(context)!;
    return Scaffold(
      appBar: AppBar(title: Text(loc.signUp)),
      body: SafeArea(
        child: Center(
          child: SingleChildScrollView(
            padding: const EdgeInsets.all(24),
            child: Form(
              key: _formKey,
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.stretch,
                children: [
                  TextFormField(
                    controller: _name,
                    decoration: InputDecoration(
                      labelText: loc.nameHint,
                      prefixIcon: const Icon(Icons.person_rounded),
                      border: const OutlineInputBorder(),
                    ),
                    validator: (v) =>
                        (v == null || v.isEmpty) ? loc.nameHint : null,
                  ),
                  const SizedBox(height: 16),
                  TextFormField(
                    controller: _email,
                    decoration: InputDecoration(
                      labelText: loc.emailHint,
                      prefixIcon: const Icon(Icons.email_rounded),
                      border: const OutlineInputBorder(),
                    ),
                    keyboardType: TextInputType.emailAddress,
                    validator: (v) =>
                        (v == null || !v.contains('@')) ? loc.emailHint : null,
                  ),
                  const SizedBox(height: 16),
                  TextFormField(
                    controller: _password,
                    obscureText: true,
                    decoration: InputDecoration(
                      labelText: loc.passwordHint,
                      prefixIcon: const Icon(Icons.lock_rounded),
                      border: const OutlineInputBorder(),
                    ),
                    validator: (v) =>
                        (v == null || v.isEmpty) ? loc.passwordHint : null,
                  ),
                  const SizedBox(height: 16),
                  TextFormField(
                    controller: _confirm,
                    obscureText: true,
                    decoration: InputDecoration(
                      labelText: loc.confirmPasswordHint,
                      prefixIcon: const Icon(Icons.lock_rounded),
                      border: const OutlineInputBorder(),
                    ),
                    validator: (v) => (v == null || v != _password.text)
                        ? loc.confirmPasswordHint
                        : null,
                  ),
                  const SizedBox(height: 8),
                  CheckboxListTile(
                    value: _acceptedTerms,
                    onChanged: (v) => setState(() => _acceptedTerms = v ?? false),
                    controlAffinity: ListTileControlAffinity.leading,
                    contentPadding: EdgeInsets.zero,
                    title: GestureDetector(
                      onTap: () => Navigator.of(context).pushNamed('/terms'),
                      child: Text(loc.termsTitle),
                    ),
                  ),
                  const SizedBox(height: 8),
                  FilledButton(
                    onPressed: _submit,
                    child: Text(loc.signUp),
                  ),
                  const SizedBox(height: 16),
                  Row(
                    mainAxisAlignment: MainAxisAlignment.center,
                    children: [
                      Text(loc.haveAccountPrompt),
                      TextButton(
                        onPressed: () =>
                            Navigator.of(context).pushReplacementNamed('/login'),
                        child: Text(loc.signIn),
                      ),
                    ],
                  ),
                ],
              ),
            ),
          ),
        ),
      ),
    );
  }
}
