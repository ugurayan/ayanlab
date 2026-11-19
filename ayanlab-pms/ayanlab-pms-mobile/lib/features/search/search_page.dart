import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import '../../core/mock/pms_mock.dart';
import '../../core/state/app_state.dart';

/// Modules tab: searchable PMS routes for the active persona (mock).
class SearchPage extends StatefulWidget {
  const SearchPage({super.key});

  @override
  State<SearchPage> createState() => _SearchPageState();
}

class _SearchPageState extends State<SearchPage> {
  String _query = '';

  @override
  Widget build(BuildContext context) {
    final persona = context.watch<SessionNotifier>().persona;
    final modules = pmsModules
        .where((m) => m.personas.contains(persona))
        .where((m) {
          if (_query.isEmpty) return true;
          final q = _query.toLowerCase();
          return m.title.toLowerCase().contains(q) ||
              m.subtitle.toLowerCase().contains(q);
        })
        .toList();

    return Column(
      children: [
        Padding(
          padding: const EdgeInsets.fromLTRB(16, 8, 16, 0),
          child: SearchBar(
            hintText: 'Modül ara (aidat, tur, kargo…)',
            leading: const Icon(Icons.search_rounded),
            onChanged: (v) => setState(() => _query = v),
          ),
        ),
        Expanded(
          child: ListView(
            padding: const EdgeInsets.all(16),
            children: [
              for (final m in modules)
                Card(
                  child: ListTile(
                    leading: Icon(m.icon),
                    title: Text(m.title),
                    subtitle: Text(m.subtitle),
                    trailing: const Icon(Icons.chevron_right_rounded),
                    onTap: () => Navigator.of(context).pushNamed(m.route),
                  ),
                ),
            ],
          ),
        ),
      ],
    );
  }
}