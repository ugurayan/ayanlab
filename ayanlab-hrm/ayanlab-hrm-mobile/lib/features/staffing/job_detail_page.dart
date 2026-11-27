import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import 'applicant_page.dart';
import 'schedule_flow.dart';
import 'staffing_chrome.dart';
import 'staffing_store.dart';
import 'staffing_theme.dart';

void openJob(BuildContext context, String id) {
  Navigator.of(context).pushNamed(kJobRoute, arguments: id);
}

class JobDetailPage extends StatefulWidget {
  const JobDetailPage({super.key, required this.jobId});

  final String jobId;

  @override
  State<JobDetailPage> createState() => _JobDetailPageState();
}

class _JobDetailPageState extends State<JobDetailPage> {
  int _selected = 0;

  @override
  Widget build(BuildContext context) {
    final store = context.watch<StaffingStore>();
    final job = store.jobById(widget.jobId);
    if (job == null) {
      return FlowPage(
        title: 'Job',
        body: Center(child: Text('This job is no longer available.', style: S.body(context))),
      );
    }
    final shifts = [...job.shifts]..sort((a, b) => a.date.compareTo(b.date));
    final index = shifts.isEmpty ? 0 : _selected.clamp(0, shifts.length);
    final shift = index == 0 ? (shifts.isEmpty ? null : shifts.first) : shifts[index - 1];
    final applied = job.applicants.where((person) => person.status == 'applied').toList();
    final hired = job.applicants.where((person) => person.status == 'hired').toList();
    final range = _range(shifts);

    return FlowPage(
      title: '',
      actions: [
        IconButton(
          onPressed: () => _menu(context, job),
          icon: const Icon(Icons.more_horiz),
          color: S.ink(context),
        ),
      ],
      body: ListView(
        padding: const EdgeInsets.fromLTRB(16, 0, 16, 32),
        children: [
          Text(job.name, style: S.section(context).copyWith(fontSize: 28)),
          const SizedBox(height: 4),
          Text('Job ID: ${job.publicId}', style: S.caption(context)),
          const SizedBox(height: 8),
          Text('$range · ${shifts.length} ${shifts.length == 1 ? 'shift' : 'shifts'}', style: S.body(context)),
          Text(job.locationName, style: S.caption(context)),
          const SizedBox(height: 14),
          SizedBox(
            height: 78,
            child: ListView(
              scrollDirection: Axis.horizontal,
              children: [
                _DateCard(
                  title: 'All shifts',
                  selected: index == 0,
                  onTap: () => setState(() => _selected = 0),
                ),
                for (var i = 0; i < shifts.length; i++)
                  _DateCard(
                    title: dayMonth(shifts[i].date),
                    subtitle: '${hhmm(shifts[i].startMin)} – ${hhmm(shifts[i].endMin)}',
                    meta: '${job.hired}/${shifts[i].workers}',
                    selected: index == i + 1,
                    onTap: () => setState(() => _selected = i + 1),
                  ),
              ],
            ),
          ),
          if (shift != null) ...[
            const SizedBox(height: 16),
            Text(
              '${_when(shift.date)} · ${hhmm(shift.startMin)} – ${hhmm(shift.endMin)}, ${breakLabel(shift.breakMin)}',
              style: S.body(context).copyWith(fontWeight: FontWeight.w700),
            ),
          ],
          const SizedBox(height: 8),
          Text(
            job.appliedCount == 0 && job.hired == 0
                ? ''
                : '${job.appliedCount + job.hired} ${job.appliedCount + job.hired == 1 ? 'person' : 'people'}',
            style: S.caption(context),
          ),
          if (applied.isEmpty && hired.isEmpty) ...[
            const SizedBox(height: 36),
            const Center(child: _EmptyMark()),
            const SizedBox(height: 16),
            Text(
              'No applicants yet',
              textAlign: TextAlign.center,
              style: S.section(context).copyWith(fontSize: 20),
            ),
            const SizedBox(height: 8),
            Text(
              'You will see applications here. Auto-hire fills the shift from people who already match.',
              textAlign: TextAlign.center,
              style: S.caption(context),
            ),
            const SizedBox(height: 16),
            Center(
              child: TextButton(
                onPressed: job.autoHire
                    ? null
                    : () {
                        store.setAutoHire(job.id, true, 'trusted');
                      },
                child: Text(job.autoHire ? 'AUTO-HIRE IS ON' : 'ENABLE AUTO-HIRE'),
              ),
            ),
          ],
          if (applied.isNotEmpty) ...[
            const SizedBox(height: 18),
            Text('Applied', style: S.section(context)),
            for (final person in applied)
              _PersonRow(job: job, person: person),
          ],
          if (hired.isNotEmpty) ...[
            const SizedBox(height: 18),
            Text('Hired', style: S.section(context)),
            for (final person in hired) _PersonRow(job: job, person: person),
          ],
        ],
      ),
    );
  }

  String _range(List<ShiftSlot> shifts) {
    if (shifts.isEmpty) return 'No dates';
    if (shifts.length == 1) return '${dayMonth(shifts.first.date)} ${shifts.first.date.year}';
    return '${dayMonth(shifts.first.date)} – ${dayMonth(shifts.last.date)}';
  }

  String _when(DateTime date) {
    final today = dateOnly(DateTime.now());
    if (sameDay(date, today)) return 'Today';
    if (sameDay(date, today.add(const Duration(days: 1)))) return 'Tomorrow';
    return '${weekdayName(date)}, ${dayMonth(date)}';
  }

  Future<void> _menu(BuildContext context, StaffJob job) async {
    final store = context.read<StaffingStore>();
    await showStaffSheet(
      context,
      Column(
        mainAxisSize: MainAxisSize.min,
        children: [
          SheetAction(
            label: 'Job details',
            onTap: () {
              Navigator.pop(context);
              store.beginEdit(job.id);
              Navigator.of(context).push(
                MaterialPageRoute<void>(builder: (_) => const OverviewPage(readOnly: true)),
              );
            },
          ),
          SheetAction(
            label: 'Edit shifts',
            onTap: () {
              Navigator.pop(context);
              store.beginEdit(job.id);
              Navigator.of(context).push(
                MaterialPageRoute<void>(builder: (_) => const ShiftsPage(finishPops: true)),
              );
            },
          ),
          SheetAction(
            label: 'Edit job text',
            onTap: () {
              Navigator.pop(context);
              store.beginEdit(job.id);
              Navigator.of(context).push(
                MaterialPageRoute<void>(builder: (_) => const AboutPage(finishPops: true)),
              );
            },
          ),
          SheetAction(
            label: 'Manage auto-hire',
            onTap: () {
              Navigator.pop(context);
              store.beginEdit(job.id);
              Navigator.of(context).push(
                MaterialPageRoute<void>(builder: (_) => const AutoHirePage(finishPops: true)),
              );
            },
          ),
          SheetAction(
            label: 'Stop hiring',
            danger: true,
            onTap: () {
              Navigator.pop(context);
              Navigator.of(context).push(
                MaterialPageRoute<void>(builder: (_) => StopHiringPage(jobId: job.id)),
              );
            },
          ),
        ],
      ),
    );
  }
}

class _DateCard extends StatelessWidget {
  const _DateCard({
    required this.title,
    required this.selected,
    required this.onTap,
    this.subtitle,
    this.meta,
  });

  final String title;
  final String? subtitle;
  final String? meta;
  final bool selected;
  final VoidCallback onTap;

  @override
  Widget build(BuildContext context) {
    final fg = selected ? Colors.white : S.ink(context);
    return Padding(
      padding: const EdgeInsets.only(right: 8),
      child: Material(
        color: selected ? const Color(0xFF2A2A2E) : S.field(context),
        borderRadius: BorderRadius.circular(10),
        child: InkWell(
          borderRadius: BorderRadius.circular(10),
          onTap: onTap,
          child: Container(
            width: 108,
            padding: const EdgeInsets.all(10),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                Text(title, style: TextStyle(color: fg, fontWeight: FontWeight.w700, fontSize: 13)),
                if (subtitle != null)
                  Text(subtitle!, style: TextStyle(color: fg.withValues(alpha: 0.8), fontSize: 11)),
                if (meta != null)
                  Text(meta!, style: TextStyle(color: fg.withValues(alpha: 0.8), fontSize: 12)),
              ],
            ),
          ),
        ),
      ),
    );
  }
}

class _PersonRow extends StatelessWidget {
  const _PersonRow({required this.job, required this.person});

  final StaffJob job;
  final Applicant person;

  @override
  Widget build(BuildContext context) {
    return InkWell(
      onTap: () => Navigator.of(context).push(
        MaterialPageRoute<void>(
          builder: (_) => ApplicantPage(jobId: job.id, applicantId: person.id),
        ),
      ),
      child: Padding(
        padding: const EdgeInsets.symmetric(vertical: 12),
        child: Row(
          children: [
            Initials(name: person.name),
            const SizedBox(width: 12),
            Expanded(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(person.name, style: S.body(context).copyWith(fontWeight: FontWeight.w600)),
                  const SizedBox(height: 2),
                  Row(
                    children: [
                      StarRow(rating: person.rating),
                      const SizedBox(width: 6),
                      Text(
                        '${person.rating.toStringAsFixed(1)} (${person.reviews})',
                        style: S.caption(context),
                      ),
                    ],
                  ),
                ],
              ),
            ),
            if (person.status == 'hired')
              Text('Hired', style: S.caption(context).copyWith(color: S.green)),
          ],
        ),
      ),
    );
  }
}

class _EmptyMark extends StatelessWidget {
  const _EmptyMark();

  @override
  Widget build(BuildContext context) {
    return Container(
      width: 120,
      height: 120,
      alignment: Alignment.center,
      decoration: BoxDecoration(
        shape: BoxShape.circle,
        border: Border.all(color: S.ink(context), width: 1.4),
      ),
      child: Icon(Icons.flash_off_outlined, size: 48, color: S.ink(context)),
    );
  }
}

class StopHiringPage extends StatelessWidget {
  const StopHiringPage({super.key, required this.jobId});

  final String jobId;

  @override
  Widget build(BuildContext context) {
    final store = context.read<StaffingStore>();
    final job = store.jobById(jobId);
    return FlowPage(
      title: '',
      closeIcon: true,
      body: ListView(
        padding: const EdgeInsets.fromLTRB(16, 8, 16, 24),
        children: [
          Text('Stop hiring', style: S.section(context).copyWith(fontSize: 28)),
          const SizedBox(height: 8),
          Text(
            'The job leaves the marketplace and open applications are declined.',
            style: S.caption(context),
          ),
          const SizedBox(height: 18),
          Text('Why do you want to stop hiring?', style: S.caption(context)),
          for (final reason in stopReasons)
            InkWell(
              onTap: job == null
                  ? null
                  : () async {
                      final confirmed = await showDialog<bool>(
                        context: context,
                        builder: (context) => AlertDialog(
                          title: const Text('Stop hiring?'),
                          content: Text('$reason\n\n${job.name} will close.'),
                          actions: [
                            TextButton(onPressed: () => Navigator.pop(context, false), child: const Text('Back')),
                            TextButton(onPressed: () => Navigator.pop(context, true), child: const Text('Stop hiring')),
                          ],
                        ),
                      );
                      if (confirmed == true && context.mounted) {
                        store.stopHiring(job.id);
                        Navigator.of(context).popUntil((route) => route.isFirst);
                      }
                    },
              child: Container(
                width: double.infinity,
                padding: const EdgeInsets.symmetric(vertical: 16),
                decoration: BoxDecoration(
                  border: Border(bottom: BorderSide(color: S.line(context))),
                ),
                child: Text(reason, style: S.body(context)),
              ),
            ),
        ],
      ),
    );
  }
}
