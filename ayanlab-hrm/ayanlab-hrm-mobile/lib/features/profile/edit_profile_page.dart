import 'package:flutter/material.dart';

import '../../core/l10n/app_localizations.dart';
import '../../core/mock/mock_data.dart';

/// Edit profile: mock name/email/phone form.
class EditProfilePage extends StatefulWidget {
  const EditProfilePage({super.key});

  @override
  State<EditProfilePage> createState() => _EditProfilePageState();
}

class _EditProfilePageState extends State<EditProfilePage> {
  late final TextEditingController _name;
  late final TextEditingController _email;
  late final TextEditingController _phone;

  @override
  void initState() {
    super.initState();
    _name = TextEditingController(text: mockUser.name);
    _email = TextEditingController(text: mockUser.email);
    _phone = TextEditingController(text: mockUser.phone);
  }

  @override
  void dispose() {
    _name.dispose();
    _email.dispose();
    _phone.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final loc = AppLocalizations.of(context)!;
    return Scaffold(
      appBar: AppBar(title: Text(loc.editProfileTitle)),
      body: ListView(
        padding: const EdgeInsets.all(24),
        children: [
          Center(
            child: Stack(
              children: [
                CircleAvatar(
                  radius: 48,
                  child: Text(
                    mockUser.name[0],
                    style: Theme.of(context).textTheme.headlineMedium,
                  ),
                ),
                PositionedDirectional(
                  bottom: 0,
                  end: 0,
                  child: CircleAvatar(
                    radius: 18,
                    child: Icon(
                      Icons.camera_alt_rounded,
                      size: 18,
                      color: Theme.of(context).colorScheme.onPrimary,
                    ),
                  ),
                ),
              ],
            ),
          ),
          TextButton(
            onPressed: () {
              ScaffoldMessenger.of(context).showSnackBar(
                SnackBar(content: Text('${loc.changePhoto} (mock)')),
              );
            },
            child: Text(loc.changePhoto),
          ),
          const SizedBox(height: 8),
          TextField(
            controller: _name,
            decoration: InputDecoration(
              labelText: loc.nameHint,
              border: const OutlineInputBorder(),
            ),
          ),
          const SizedBox(height: 16),
          TextField(
            controller: _email,
            decoration: InputDecoration(
              labelText: loc.emailHint,
              border: const OutlineInputBorder(),
            ),
            keyboardType: TextInputType.emailAddress,
          ),
          const SizedBox(height: 16),
          TextField(
            controller: _phone,
            decoration: InputDecoration(
              labelText: loc.phoneHint,
              border: const OutlineInputBorder(),
            ),
            keyboardType: TextInputType.phone,
          ),
          const SizedBox(height: 24),
          FilledButton(
            onPressed: () {
              ScaffoldMessenger.of(context).showSnackBar(
                SnackBar(content: Text('${loc.save} (mock)')),
              );
              Navigator.of(context).pop();
            },
            child: Text(loc.save),
          ),
        ],
      ),
    );
  }
}
