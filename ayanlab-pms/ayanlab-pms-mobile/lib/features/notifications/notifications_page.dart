import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import '../../core/l10n/app_localizations.dart';
import '../../core/mock/pms_mock.dart';
import '../../core/state/app_state.dart';

/// Notifications tab: persona-specific mock list.
class NotificationsPage extends StatefulWidget {
  const NotificationsPage({super.key});

  @override
  State<NotificationsPage> createState() => _NotificationsPageState();
}

class _NotificationsPageState extends State<NotificationsPage> {
  PmsPersona? _persona;
  List<MockNotification> _items = [];

  void _sync(PmsPersona persona) {
    if (_persona != persona) {
      _persona = persona;
      _items = List.of(PmsMock.instance.notificationsFor(persona));
    }
  }

  @override
  Widget build(BuildContext context) {
    final loc = AppLocalizations.of(context)!;
    final persona = context.watch<SessionNotifier>().persona;
    _sync(persona);

    return Column(
      children: [
        Padding(
          padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
          child: Row(
            children: [
              Expanded(
                child: Text(
                  '${loc.tabNotifications} · ${persona.label}',
                  style: Theme.of(context).textTheme.titleMedium,
                ),
              ),
              TextButton(
                onPressed: () => setState(() {
                  _items = [
                    for (final n in _items)
                      MockNotification(
                        title: n.title,
                        body: n.body,
                        time: n.time,
                        unread: false,
                      ),
                  ];
                }),
                child: Text(loc.markAllRead),
              ),
            ],
          ),
        ),
        Expanded(
          child: _items.isEmpty
              ? Center(child: Text(loc.notificationsEmpty))
              : ListView(
                  padding: const EdgeInsets.symmetric(horizontal: 16),
                  children: [
                    for (final n in _items)
                      Card(
                        child: ListTile(
                          leading: Badge(
                            isLabelVisible: n.unread,
                            child: const Icon(Icons.circle_notifications_rounded),
                          ),
                          title: Text(n.title),
                          subtitle: Text('${n.body} · ${n.time}'),
                        ),
                      ),
                  ],
                ),
        ),
      ],
    );
  }
}