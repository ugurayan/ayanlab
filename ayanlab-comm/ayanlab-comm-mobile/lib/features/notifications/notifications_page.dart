import 'package:flutter/material.dart';

import '../../core/l10n/app_localizations.dart';
import '../../core/mock/mock_data.dart';

/// Notifications tab: mock list with read state and mark-all action.
class NotificationsPage extends StatefulWidget {
  const NotificationsPage({super.key});

  @override
  State<NotificationsPage> createState() => _NotificationsPageState();
}

class _NotificationsPageState extends State<NotificationsPage> {
  late List<MockNotification> _items;

  @override
  void initState() {
    super.initState();
    _items = List.of(mockNotifications);
  }

  @override
  Widget build(BuildContext context) {
    final loc = AppLocalizations.of(context)!;

    return Column(
      children: [
        Padding(
          padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
          child: Row(
            children: [
              Expanded(child: Text(loc.tabNotifications,
                  style: Theme.of(context).textTheme.titleMedium)),
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
