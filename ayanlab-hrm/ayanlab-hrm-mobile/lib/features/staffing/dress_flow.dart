import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import 'schedule_flow.dart';
import 'staffing_chrome.dart';
import 'staffing_store.dart';
import 'staffing_theme.dart';

class DressPage extends StatelessWidget {
  const DressPage({super.key});

  @override
  Widget build(BuildContext context) {
    final store = context.read<StaffingStore>();
    return FlowPage(
      title: 'Dress code',
      body: ListView(
        children: [
          for (final option in dressOptions) ...[
            InkWell(
              onTap: () {
                store.draft.dressId = option.id;
                store.touch();
                final next = option.id == 'custom'
                    ? const ClothingPage()
                    : const ClothingNotesPage();
                Navigator.of(context).push(MaterialPageRoute<void>(builder: (_) => next));
              },
              child: Padding(
                padding: const EdgeInsets.fromLTRB(16, 12, 16, 12),
                child: Row(
                  children: [
                    IconThumb(icon: option.icon),
                    const SizedBox(width: 12),
                    Expanded(
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          Text(option.title, style: S.body(context).copyWith(fontWeight: FontWeight.w700)),
                          const SizedBox(height: 4),
                          Text(option.detail, style: S.caption(context)),
                        ],
                      ),
                    ),
                  ],
                ),
              ),
            ),
            const Hairline(),
          ],
        ],
      ),
    );
  }
}

class ClothingPage extends StatefulWidget {
  const ClothingPage({super.key});

  @override
  State<ClothingPage> createState() => _ClothingPageState();
}

class _ClothingPageState extends State<ClothingPage> {
  var _query = '';

  @override
  Widget build(BuildContext context) {
    final store = context.watch<StaffingStore>();
    final query = _query.trim().toLowerCase();
    final items = [
      for (final item in clothingItems)
        if (query.isEmpty || item.name.toLowerCase().contains(query)) item,
    ];
    return FlowPage(
      title: 'Choose clothing items',
      body: Column(
        children: [
          Padding(
            padding: const EdgeInsets.fromLTRB(16, 8, 16, 8),
            child: SearchBox(onChanged: (value) => setState(() => _query = value)),
          ),
          const Hairline(),
          Expanded(
            child: ListView(
              children: [
                for (final item in items) ...[
                  InkWell(
                    onTap: () {
                      store.draft.clothing.contains(item.name)
                          ? store.draft.clothing.remove(item.name)
                          : store.draft.clothing.add(item.name);
                      store.touch();
                    },
                    child: Padding(
                      padding: const EdgeInsets.fromLTRB(16, 12, 16, 12),
                      child: Row(
                        children: [
                          IconThumb(icon: item.icon),
                          const SizedBox(width: 12),
                          Expanded(
                            child: Column(
                              crossAxisAlignment: CrossAxisAlignment.start,
                              children: [
                                Text(item.name, style: S.body(context).copyWith(fontWeight: FontWeight.w700)),
                                Text(item.detail, style: S.caption(context)),
                              ],
                            ),
                          ),
                          CheckBox(selected: store.draft.clothing.contains(item.name)),
                        ],
                      ),
                    ),
                  ),
                  const Hairline(),
                ],
              ],
            ),
          ),
        ],
      ),
      footer: StaffFooter(
        label: 'CONTINUE',
        onPressed: () => Navigator.of(context).push(
          MaterialPageRoute<void>(builder: (_) => const ClothingNotesPage()),
        ),
      ),
    );
  }
}

class ClothingNotesPage extends StatefulWidget {
  const ClothingNotesPage({super.key});

  @override
  State<ClothingNotesPage> createState() => _ClothingNotesPageState();
}

class _ClothingNotesPageState extends State<ClothingNotesPage> {
  late final TextEditingController _notes;

  @override
  void initState() {
    super.initState();
    _notes = TextEditingController(text: context.read<StaffingStore>().draft.clothingNote);
  }

  @override
  void dispose() {
    _notes.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final store = context.watch<StaffingStore>();
    return FlowPage(
      title: 'Dress code',
      body: ListView(
        padding: const EdgeInsets.fromLTRB(16, 8, 16, 24),
        children: [
          Row(
            children: [
              Expanded(child: Text('Additional clothing requirements', style: S.body(context))),
              Text('Optional', style: S.caption(context)),
            ],
          ),
          const SizedBox(height: 8),
          TextField(
            controller: _notes,
            minLines: 2,
            maxLines: 4,
            decoration: staffField(context),
            onChanged: (value) => store.draft.clothingNote = value,
          ),
          const SizedBox(height: 24),
          Text('Grooming', style: S.section(context)),
          const SizedBox(height: 10),
          Wrap(
            spacing: 8,
            runSpacing: 8,
            children: [
              for (final option in groomingOptions)
                SelectChip(
                  label: option,
                  selected: store.draft.grooming == option,
                  onTap: () {
                    store.draft.grooming = option;
                    store.touch();
                  },
                ),
            ],
          ),
        ],
      ),
      footer: StaffFooter(
        label: 'CONTINUE',
        onPressed: () => Navigator.of(context).push(
          MaterialPageRoute<void>(builder: (_) => const ShiftsPage()),
        ),
      ),
    );
  }
}
