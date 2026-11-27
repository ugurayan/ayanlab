import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import 'staffing_chrome.dart';
import 'staffing_store.dart';
import 'staffing_theme.dart';

class ShiftsPage extends StatelessWidget {
  const ShiftsPage({super.key, this.finishPops = false});

  final bool finishPops;

  @override
  Widget build(BuildContext context) {
    final store = context.watch<StaffingStore>();
    final shifts = store.draft.shifts;
    final countLabel = shifts.length == 1 ? '1 shift' : '${shifts.length} shifts';
    return FlowPage(
      title: 'Shifts',
      body: ListView(
        padding: const EdgeInsets.fromLTRB(16, 4, 16, 24),
        children: [
          AddRow(
            label: 'Add shift',
            onTap: () => Navigator.of(context).push(
              MaterialPageRoute<void>(builder: (_) => const NewShiftPage()),
            ),
          ),
          const Hairline(),
          for (final shift in shifts) ...[
            Padding(
              padding: const EdgeInsets.symmetric(vertical: 12),
              child: Row(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Expanded(
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Text(
                          '${weekdayShort(shift.date)}, ${dayMonth(shift.date)} ${shift.date.year}',
                          style: S.body(context),
                        ),
                        Text(
                          '${hhmm(shift.startMin)} – ${hhmm(shift.endMin)}, ${breakLabel(shift.breakMin)}',
                          style: S.caption(context),
                        ),
                        const SizedBox(height: 4),
                        Row(
                          children: [
                            Icon(Icons.person_outline, size: 16, color: S.muted(context)),
                            const SizedBox(width: 4),
                            Text(
                              '${shift.workers} workers needed',
                              style: S.caption(context),
                            ),
                          ],
                        ),
                      ],
                    ),
                  ),
                  IconButton(
                    onPressed: () => store.removeShift(shift.id),
                    icon: Icon(Icons.close, color: S.muted(context)),
                  ),
                ],
              ),
            ),
            const Hairline(),
          ],
        ],
      ),
      footer: StaffFooter(
        label: 'CONTINUE',
        caption: shifts.isEmpty ? null : countLabel,
        onPressed: shifts.isEmpty
            ? null
            : () {
                if (finishPops) {
                  if (store.editingJobId != null) store.applyDraftToJob();
                  Navigator.pop(context);
                } else {
                  Navigator.of(context).push(
                    MaterialPageRoute<void>(builder: (_) => const ContactsPage()),
                  );
                }
              },
      ),
    );
  }
}

class NewShiftPage extends StatefulWidget {
  const NewShiftPage({super.key});

  @override
  State<NewShiftPage> createState() => _NewShiftPageState();
}

class _NewShiftPageState extends State<NewShiftPage> {
  late DateTime _date;
  int _start = 9 * 60;
  int _end = 18 * 60;
  int _break = 60;
  int _workers = 1;
  var _poOn = false;
  final _po = TextEditingController();

  @override
  void initState() {
    super.initState();
    final shifts = context.read<StaffingStore>().draft.shifts;
    _date = shifts.isEmpty
        ? dateOnly(DateTime.now()).add(const Duration(days: 1))
        : shifts.last.date.add(const Duration(days: 1));
  }

  @override
  void dispose() {
    _po.dispose();
    super.dispose();
  }

  int get _work => _end - _start - _break;

  void _step(void Function() change) => setState(change);

  @override
  Widget build(BuildContext context) {
    final store = context.read<StaffingStore>();
    final valid = _work > 0 && _workers > 0;
    final hours = _work <= 0 ? 0 : _work / 60;
    return FlowPage(
      title: 'New shift',
      closeIcon: true,
      body: ListView(
        padding: const EdgeInsets.fromLTRB(16, 8, 16, 24),
        children: [
          Text('Date', style: S.body(context)),
          const SizedBox(height: 8),
          InkWell(
            onTap: () async {
              final picked = await showDatePicker(
                context: context,
                initialDate: _date,
                firstDate: dateOnly(DateTime.now()),
                lastDate: dateOnly(DateTime.now()).add(const Duration(days: 365)),
              );
              if (picked != null) setState(() => _date = dateOnly(picked));
            },
            child: Container(
              width: double.infinity,
              padding: const EdgeInsets.symmetric(horizontal: 14, vertical: 14),
              decoration: BoxDecoration(
                borderRadius: BorderRadius.circular(10),
                border: Border.all(color: S.line(context)),
              ),
              child: Text(longDate(_date), style: S.body(context)),
            ),
          ),
          const SizedBox(height: 20),
          Row(
            children: [
              _Spinner(
                label: 'Start',
                value: hhmm(_start),
                onUp: () => _step(() => _start = (_start + 15).clamp(0, 23 * 60 + 45)),
                onDown: () => _step(() => _start = (_start - 15).clamp(0, 23 * 60 + 45)),
              ),
              _Spinner(
                label: 'Finish',
                value: hhmm(_end),
                onUp: () => _step(() => _end = (_end + 15).clamp(15, 24 * 60)),
                onDown: () => _step(() => _end = (_end - 15).clamp(15, 24 * 60)),
              ),
              _Spinner(
                label: 'Break',
                value: hhmm(_break),
                onUp: () => _step(() => _break = (_break + 15).clamp(0, 180)),
                onDown: () => _step(() => _break = (_break - 15).clamp(0, 180)),
              ),
            ],
          ),
          const SizedBox(height: 20),
          Text('Workers', style: S.body(context)),
          const SizedBox(height: 8),
          Row(
            children: [
              Expanded(
                child: Container(
                  padding: const EdgeInsets.symmetric(horizontal: 14, vertical: 12),
                  decoration: BoxDecoration(
                    borderRadius: BorderRadius.circular(10),
                    border: Border.all(color: S.line(context)),
                  ),
                  child: Text('$_workers', style: S.body(context)),
                ),
              ),
              IconButton(
                onPressed: _workers <= 1 ? null : () => _step(() => _workers -= 1),
                icon: const Icon(Icons.remove),
              ),
              IconButton(
                onPressed: () => _step(() => _workers += 1),
                icon: const Icon(Icons.add),
              ),
            ],
          ),
          const SizedBox(height: 12),
          Row(
            children: [
              Expanded(child: Text('Add a purchase order number', style: S.body(context))),
              StaffSwitch(value: _poOn, onChanged: (value) => setState(() => _poOn = value)),
            ],
          ),
          if (_poOn) ...[
            const SizedBox(height: 8),
            TextField(
              controller: _po,
              decoration: staffField(context, hint: 'PO number'),
            ),
          ],
        ],
      ),
      footer: StaffFooter(
        label: 'CREATE SHIFT',
        filled: true,
        caption: valid
            ? '${hours.toStringAsFixed(hours == hours.roundToDouble() ? 0 : 1)} h total work time'
            : 'Finish must be after the break',
        onPressed: valid
            ? () {
                store.addShift(
                  store.newSlot(
                    date: _date,
                    startMin: _start,
                    endMin: _end,
                    breakMin: _break,
                    workers: _workers,
                    po: _poOn && _po.text.trim().isNotEmpty ? _po.text.trim() : null,
                  ),
                );
                Navigator.pop(context);
              }
            : null,
      ),
    );
  }
}

class _Spinner extends StatelessWidget {
  const _Spinner({
    required this.label,
    required this.value,
    required this.onUp,
    required this.onDown,
  });

  final String label;
  final String value;
  final VoidCallback onUp;
  final VoidCallback onDown;

  @override
  Widget build(BuildContext context) {
    return Expanded(
      child: Column(
        children: [
          IconButton(
            onPressed: onUp,
            icon: Icon(Icons.keyboard_arrow_up, color: S.muted(context)),
          ),
          Text(label, style: S.caption(context)),
          const SizedBox(height: 6),
          Container(
            padding: const EdgeInsets.symmetric(horizontal: 14, vertical: 10),
            decoration: BoxDecoration(
              color: S.field(context),
              borderRadius: BorderRadius.circular(10),
            ),
            child: Text(value, style: S.body(context).copyWith(fontWeight: FontWeight.w600)),
          ),
          IconButton(
            onPressed: onDown,
            icon: Icon(Icons.keyboard_arrow_down, color: S.muted(context)),
          ),
        ],
      ),
    );
  }
}

class ContactsPage extends StatelessWidget {
  const ContactsPage({super.key});

  @override
  Widget build(BuildContext context) {
    final store = context.watch<StaffingStore>();
    return FlowPage(
      title: 'Contact person',
      actions: [
        IconButton(
          onPressed: () => _add(context),
          icon: const Icon(Icons.add),
          color: S.ink(context),
        ),
      ],
      body: ListView(
        children: [
          for (final contact in store.contacts)
            InkWell(
              onTap: () {
                store.draft.contactId = contact.id;
                store.touch();
              },
              child: Padding(
                padding: const EdgeInsets.fromLTRB(16, 14, 16, 14),
                child: Row(
                  children: [
                    Expanded(
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          Text(contact.name, style: S.body(context).copyWith(fontWeight: FontWeight.w700)),
                          Text(contact.email, style: S.caption(context)),
                        ],
                      ),
                    ),
                    RadioDot(selected: store.draft.contactId == contact.id),
                  ],
                ),
              ),
            ),
        ],
      ),
      footer: StaffFooter(
        label: 'CONTINUE',
        onPressed: store.draft.contactId.isEmpty
            ? null
            : () => Navigator.of(context).push(
                  MaterialPageRoute<void>(builder: (_) => const AboutPage()),
                ),
      ),
    );
  }

  Future<void> _add(BuildContext context) async {
    final name = TextEditingController();
    final email = TextEditingController();
    final store = context.read<StaffingStore>();
    await showDialog<void>(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('New contact'),
        content: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            TextField(controller: name, decoration: const InputDecoration(labelText: 'Name')),
            TextField(controller: email, decoration: const InputDecoration(labelText: 'Email')),
          ],
        ),
        actions: [
          TextButton(onPressed: () => Navigator.pop(context), child: const Text('Cancel')),
          TextButton(
            onPressed: () {
              if (name.text.trim().isEmpty || !email.text.contains('@')) return;
              store.addContact(name.text.trim(), email.text.trim());
              Navigator.pop(context);
            },
            child: const Text('Add'),
          ),
        ],
      ),
    );
    name.dispose();
    email.dispose();
  }
}

class AboutPage extends StatefulWidget {
  const AboutPage({super.key, this.finishPops = false});

  final bool finishPops;

  @override
  State<AboutPage> createState() => _AboutPageState();
}

class _AboutPageState extends State<AboutPage> {
  late final TextEditingController _name;
  late final TextEditingController _summary;
  late final TextEditingController _safety;

  @override
  void initState() {
    super.initState();
    final draft = context.read<StaffingStore>().draft;
    _name = TextEditingController(text: draft.name);
    _summary = TextEditingController(text: draft.summary);
    _safety = TextEditingController(text: draft.safety);
  }

  @override
  void dispose() {
    _name.dispose();
    _summary.dispose();
    _safety.dispose();
    super.dispose();
  }

  bool get _ready => _name.text.trim().isNotEmpty && _safety.text.trim().isNotEmpty;

  @override
  Widget build(BuildContext context) {
    final store = context.watch<StaffingStore>();
    return FlowPage(
      title: 'Tell us about the job',
      body: ListView(
        padding: const EdgeInsets.fromLTRB(16, 8, 16, 24),
        children: [
          Text('Job name', style: S.body(context)),
          const SizedBox(height: 8),
          TextField(
            controller: _name,
            decoration: staffField(context),
            onChanged: (value) {
              store.draft.name = value;
              setState(() {});
            },
          ),
          const SizedBox(height: 16),
          Row(
            children: [
              Expanded(child: Text('Job description', style: S.body(context))),
              Text('Optional', style: S.caption(context)),
            ],
          ),
          const SizedBox(height: 8),
          TextField(
            controller: _summary,
            minLines: 2,
            maxLines: 4,
            decoration: staffField(context),
            onChanged: (value) => store.draft.summary = value,
          ),
          const SizedBox(height: 16),
          Text('Health & safety risks', style: S.body(context)),
          const SizedBox(height: 8),
          TextField(
            controller: _safety,
            minLines: 2,
            maxLines: 4,
            decoration: staffField(context),
            onChanged: (value) {
              store.draft.safety = value;
              setState(() {});
            },
          ),
          AddRow(
            label: store.draft.pdfAttached ? 'Brief attached' : 'Attach PDF instructions',
            onTap: () {
              store.draft.pdfAttached = !store.draft.pdfAttached;
              store.touch();
            },
          ),
          Text(
            'A short brief helps hired people prepare. This demo stores the attachment flag only.',
            style: S.caption(context),
          ),
        ],
      ),
      footer: StaffFooter(
        label: 'CONTINUE',
        onPressed: _ready
            ? () {
                store.draft
                  ..name = _name.text.trim()
                  ..summary = _summary.text.trim()
                  ..safety = _safety.text.trim();
                if (widget.finishPops) {
                  if (store.editingJobId != null) store.applyDraftToJob();
                  Navigator.pop(context);
                } else {
                  Navigator.of(context).push(
                    MaterialPageRoute<void>(builder: (_) => const AutoHirePage()),
                  );
                }
              }
            : null,
      ),
    );
  }
}

class AutoHirePage extends StatelessWidget {
  const AutoHirePage({super.key, this.finishPops = false});

  final bool finishPops;

  @override
  Widget build(BuildContext context) {
    final store = context.watch<StaffingStore>();
    final draft = store.draft;
    return FlowPage(
      title: 'Auto-hire',
      body: ListView(
        padding: const EdgeInsets.fromLTRB(16, 12, 16, 24),
        children: [
          Row(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Expanded(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Wrap(
                      crossAxisAlignment: WrapCrossAlignment.center,
                      spacing: 8,
                      children: [
                        Text('Auto-hire', style: S.body(context).copyWith(fontWeight: FontWeight.w700)),
                        Container(
                          padding: const EdgeInsets.symmetric(horizontal: 6, vertical: 2),
                          decoration: BoxDecoration(
                            border: Border.all(color: const Color(0xFF2563EB)),
                            borderRadius: BorderRadius.circular(4),
                          ),
                          child: const Text(
                            'RECOMMENDED',
                            style: TextStyle(color: Color(0xFF2563EB), fontSize: 11, fontWeight: FontWeight.w700),
                          ),
                        ),
                      ],
                    ),
                    const SizedBox(height: 4),
                    Text(
                      'Hire suitable people automatically so the shift fills without a manual review.',
                      style: S.caption(context),
                    ),
                  ],
                ),
              ),
              StaffSwitch(
                value: draft.autoHire,
                onChanged: (value) {
                  draft.autoHire = value;
                  store.touch();
                },
              ),
            ],
          ),
          const SizedBox(height: 20),
          _PoolTile(
            icon: Icons.verified_user_outlined,
            title: 'Trusted workers',
            detail: 'Favourites and people with strong ratings for this role.',
            selected: draft.pool == 'trusted',
            onTap: draft.autoHire
                ? () {
                    draft.pool = 'trusted';
                    store.touch();
                  }
                : null,
          ),
          _PoolTile(
            icon: Icons.work_outline,
            title: 'Selected workers only',
            detail: 'Only people you have worked with before can be auto-hired.',
            selected: draft.pool == 'selected',
            onTap: draft.autoHire
                ? () {
                    draft.pool = 'selected';
                    store.touch();
                  }
                : null,
          ),
        ],
      ),
      footer: StaffFooter(
        label: 'CONTINUE',
        onPressed: () {
          if (finishPops) {
            if (store.editingJobId != null) {
              store.applyDraftToJob();
            } else {
              store.touch();
              Navigator.pop(context);
              return;
            }
            Navigator.pop(context);
          } else {
            Navigator.of(context).push(
              MaterialPageRoute<void>(builder: (_) => const OverviewPage()),
            );
          }
        },
      ),
    );
  }
}

class _PoolTile extends StatelessWidget {
  const _PoolTile({
    required this.icon,
    required this.title,
    required this.detail,
    required this.selected,
    required this.onTap,
  });

  final IconData icon;
  final String title;
  final String detail;
  final bool selected;
  final VoidCallback? onTap;

  @override
  Widget build(BuildContext context) {
    return InkWell(
      onTap: onTap,
      child: Padding(
        padding: const EdgeInsets.symmetric(vertical: 12),
        child: Row(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Icon(icon, color: S.ink(context)),
            const SizedBox(width: 12),
            Expanded(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(title, style: S.body(context).copyWith(fontWeight: FontWeight.w600)),
                  Text(detail, style: S.caption(context)),
                ],
              ),
            ),
            RadioDot(selected: selected),
          ],
        ),
      ),
    );
  }
}

class OverviewPage extends StatelessWidget {
  const OverviewPage({super.key, this.readOnly = false});

  final bool readOnly;

  @override
  Widget build(BuildContext context) {
    final store = context.watch<StaffingStore>();
    final draft = store.draft;
    final dress = store.dressById(draft.dressId);
    final site = store.worksiteById(draft.locationId);
    return FlowPage(
      title: 'Overview',
      closeIcon: true,
      onClose: () => _close(context),
      actions: [
        if (!readOnly)
          TextButton(
            onPressed: () => _close(context),
            child: const Text('Discard'),
          ),
      ],
      body: ListView(
        padding: const EdgeInsets.fromLTRB(16, 8, 16, 24),
        children: [
          IconThumb(icon: dress.icon),
          const SizedBox(height: 12),
          Text(dress.title, style: S.section(context)),
          Text(dress.detail, style: S.caption(context)),
          if (draft.clothing.isNotEmpty) ...[
            const SizedBox(height: 8),
            Text(draft.clothing.join(', '), style: S.body(context)),
          ],
          if (draft.clothingNote.trim().isNotEmpty) ...[
            const SizedBox(height: 12),
            Text(draft.clothingNote.trim(), style: S.body(context)),
          ],
          const SizedBox(height: 16),
          Text(draft.name.isEmpty ? draft.roles.join(', ') : draft.name, style: S.section(context).copyWith(fontSize: 22)),
          const SizedBox(height: 4),
          Text(draft.roles.join(' · '), style: S.body(context)),
          Text('${money(draft.wage)}/h wage · ${money(draft.cost)}/h cost', style: S.caption(context)),
          if (site != null) ...[
            const SizedBox(height: 8),
            Text(site.name, style: S.body(context).copyWith(fontWeight: FontWeight.w600)),
            Text(site.address, style: S.caption(context)),
          ],
          if (draft.locationNotes.trim().isNotEmpty)
            Text(draft.locationNotes.trim(), style: S.caption(context)),
          if (draft.certificates.isNotEmpty) ...[
            const SizedBox(height: 8),
            Text('Certificates: ${draft.certificates.join(', ')}', style: S.caption(context)),
          ],
          const SizedBox(height: 18),
          Row(
            children: [
              Expanded(
                child: Text(
                  'Schedule (${draft.shifts.length} ${draft.shifts.length == 1 ? 'shift' : 'shifts'})',
                  style: S.section(context),
                ),
              ),
              TextButton(
                onPressed: () => Navigator.of(context).push(
                  MaterialPageRoute<void>(
                    builder: (_) => const ShiftsPage(finishPops: true),
                  ),
                ),
                child: const Text('Edit'),
              ),
            ],
          ),
          for (final shift in draft.shifts)
            Padding(
              padding: const EdgeInsets.only(bottom: 12),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    '${weekdayShort(shift.date)}, ${dayMonth(shift.date)} ${shift.date.year}',
                    style: S.body(context),
                  ),
                  Text(
                    '${hhmm(shift.startMin)} – ${hhmm(shift.endMin)}, ${breakLabel(shift.breakMin)}',
                    style: S.caption(context),
                  ),
                  Text('${shift.workers} workers needed', style: S.caption(context)),
                ],
              ),
            ),
        ],
      ),
      footer: readOnly
          ? null
          : StaffFooter(
              label: 'PUBLISH JOB',
              filled: true,
              caption: money(draft.estimatedTotal),
              secondary: Text(
                draft.estimatedTotal == 0 ? 'Add a shift to estimate the cost' : 'Estimated total cost',
                style: S.caption(context),
              ),
              onPressed: draft.shifts.isEmpty || draft.roles.isEmpty
                  ? null
                  : () {
                      final nav = Navigator.of(context);
                      final id = store.publish();
                      nav.popUntil((route) => route.isFirst);
                      nav.pushNamed(kJobRoute, arguments: id);
                    },
            ),
    );
  }

  void _close(BuildContext context) {
    if (readOnly) {
      Navigator.pop(context);
    } else {
      Navigator.of(context).popUntil((route) => route.isFirst);
    }
  }
}
