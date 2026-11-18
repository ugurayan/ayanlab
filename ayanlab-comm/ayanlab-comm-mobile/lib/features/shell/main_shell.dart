import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import '../../core/l10n/app_localizations.dart';
import '../../core/state/app_state.dart';
import '../../core/widgets/language_sheet.dart';
import '../home/home_page.dart';
import '../notifications/notifications_page.dart';
import '../profile/profile_page.dart';
import '../search/search_page.dart';

/// Main app shell: AppBar + Drawer + bottom NavigationBar (4 tabs) + FAB.
class MainShell extends StatefulWidget {
  const MainShell({super.key});

  @override
  State<MainShell> createState() => _MainShellState();
}

class _MainShellState extends State<MainShell> {
  int _index = 0;

  @override
  Widget build(BuildContext context) {
    final loc = AppLocalizations.of(context)!;
    final session = context.watch<SessionNotifier>();

    final tabs = <Widget>[
      HomePage(),
      SearchPage(),
      NotificationsPage(),
      ProfilePage(),
    ];

    final titles = <String>[
      loc.tabHome,
      loc.tabSearch,
      loc.tabNotifications,
      loc.tabProfile,
    ];

    return Scaffold(
      appBar: AppBar(
        title: Text(titles[_index]),
        actions: [
          IconButton(
            icon: const Icon(Icons.search_rounded),
            tooltip: loc.tabSearch,
            onPressed: () => setState(() => _index = 1),
          ),
          IconButton(
            icon: const Icon(Icons.notifications_rounded),
            tooltip: loc.tabNotifications,
            onPressed: () => setState(() => _index = 2),
          ),
        ],
      ),
      drawer: _buildDrawer(context, loc, session),
      body: IndexedStack(index: _index, children: tabs),
      floatingActionButton: _index == 0
          ? FloatingActionButton.extended(
              onPressed: () {
                ScaffoldMessenger.of(context).showSnackBar(
                  SnackBar(content: Text('${loc.fabLabel} (mock)')),
                );
              },
              icon: const Icon(Icons.add),
              label: Text(loc.fabLabel),
            )
          : null,
      bottomNavigationBar: NavigationBar(
        selectedIndex: _index,
        onDestinationSelected: (i) => setState(() => _index = i),
        destinations: [
          NavigationDestination(
            icon: const Icon(Icons.home_outlined),
            selectedIcon: const Icon(Icons.home_rounded),
            label: loc.tabHome,
          ),
          NavigationDestination(
            icon: const Icon(Icons.search_outlined),
            selectedIcon: const Icon(Icons.search_rounded),
            label: loc.tabSearch,
          ),
          NavigationDestination(
            icon: const Icon(Icons.notifications_outlined),
            selectedIcon: const Icon(Icons.notifications_rounded),
            label: loc.tabNotifications,
          ),
          NavigationDestination(
            icon: const Icon(Icons.person_outline_rounded),
            selectedIcon: const Icon(Icons.person_rounded),
            label: loc.tabProfile,
          ),
        ],
      ),
    );
  }

  Widget _buildDrawer(
    BuildContext context,
    AppLocalizations loc,
    SessionNotifier session,
  ) {
    return Drawer(
      child: ListView(
        padding: EdgeInsets.zero,
        children: [
          UserAccountsDrawerHeader(
            accountName: Text(session.name),
            accountEmail: Text(session.email),
            currentAccountPicture: CircleAvatar(
              child: Text(
                session.name.isNotEmpty ? session.name[0].toUpperCase() : 'A',
              ),
            ),
            decoration: BoxDecoration(
              color: Theme.of(context).colorScheme.primary,
            ),
          ),
          _tile(context, Icons.edit_rounded, loc.profileEdit, '/profile/edit'),
          _tile(context, Icons.settings_rounded, loc.settingsTitle, '/settings'),
          _tile(context, Icons.security_rounded, loc.securityTitle, '/security'),
          _tile(context, Icons.help_rounded, loc.helpTitle, '/help'),
          _tile(context, Icons.info_rounded, loc.aboutTitle, '/about'),
          _tile(context, Icons.gavel_rounded, loc.termsTitle, '/terms'),
          const Divider(),
          ListTile(
            leading: const Icon(Icons.language),
            title: Text(loc.language),
            subtitle: Text(
              context.watch<LocaleNotifier>().tag == null
                  ? loc.systemDefault
                  : LocaleNotifier.displayNames[
                          context.watch<LocaleNotifier>().tag ?? 'en'] ??
                      '',
            ),
            onTap: () => showLanguageSheet(context),
          ),
          ListTile(
            leading: const Icon(Icons.contrast_rounded),
            title: Text(loc.theme),
            trailing: Switch(
              value: context.watch<ThemeNotifier>().mode == ThemeMode.dark,
              onChanged: (dark) =>
                  context.read<ThemeNotifier>().setMode(dark ? 'dark' : 'light'),
            ),
          ),
          const Divider(),
          ListTile(
            leading: const Icon(Icons.logout_rounded),
            title: Text(loc.logout),
            onTap: () {
              context.read<SessionNotifier>().logout();
              Navigator.of(context)
                  .pushNamedAndRemoveUntil('/login', (_) => false);
            },
          ),
        ],
      ),
    );
  }

  Widget _tile(BuildContext context, IconData icon, String title, String route) {
    return ListTile(
      leading: Icon(icon),
      title: Text(title),
      onTap: () {
        Navigator.of(context).pop(); // close drawer
        Navigator.of(context).pushNamed(route);
      },
    );
  }
}
