import 'package:flutter/material.dart';

import '../../core/l10n/app_localizations.dart';
import '../../core/mock/mock_data.dart';

/// Search/Explore tab: mock search field, filter chips and result list.
class SearchPage extends StatefulWidget {
  const SearchPage({super.key});

  @override
  State<SearchPage> createState() => _SearchPageState();
}

class _SearchPageState extends State<SearchPage> {
  String _query = '';
  int _filter = 0;

  @override
  Widget build(BuildContext context) {
    final loc = AppLocalizations.of(context)!;

    final results = _query.isEmpty
        ? mockRecentItems
        : mockRecentItems
            .where((i) =>
                i.title.toLowerCase().contains(_query.toLowerCase()) ||
                i.subtitle.toLowerCase().contains(_query.toLowerCase()))
            .toList();

    return Column(
      children: [
        Padding(
          padding: const EdgeInsets.all(16),
          child: TextField(
            onChanged: (v) => setState(() => _query = v),
            decoration: InputDecoration(
              hintText: loc.searchHint,
              prefixIcon: const Icon(Icons.search_rounded),
              border: const OutlineInputBorder(),
            ),
          ),
        ),
        SizedBox(
          height: 40,
          child: ListView(
            scrollDirection: Axis.horizontal,
            padding: const EdgeInsets.symmetric(horizontal: 16),
            children: [
              for (var i = 0; i < 4; i++)
                Padding(
                  padding: const EdgeInsetsDirectional.only(end: 8),
                  child: FilterChip(
                    label: Text('${loc.searchFilters} ${i + 1}'),
                    selected: _filter == i,
                    onSelected: (_) => setState(() => _filter = i),
                  ),
                ),
            ],
          ),
        ),
        Expanded(
          child: results.isEmpty
              ? Center(child: Text(loc.searchEmpty))
              : ListView(
                  padding: const EdgeInsets.all(16),
                  children: [
                    for (final item in results)
                      Card(
                        child: ListTile(
                          leading: Icon(item.icon),
                          title: Text(item.title),
                          subtitle: Text(item.subtitle),
                          trailing: const Icon(Icons.chevron_right_rounded),
                          onTap: () {
                            ScaffoldMessenger.of(context).showSnackBar(
                              SnackBar(content: Text('${item.title} (mock)')),
                            );
                          },
                        ),
                      ),
                  ],
                ),
        ),
      ],
    );
  }
}
