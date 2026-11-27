import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import 'create_flow.dart';
import 'job_detail_page.dart';
import 'staffing_chrome.dart';
import 'staffing_store.dart';
import 'staffing_theme.dart';

class SchedulePage extends StatelessWidget {
  const SchedulePage({super.key});

  @override
  Widget build(BuildContext context) {
    final store = context.watch<StaffingStore>();
    final start = startOfWeek(store.selectedDay);
    final days = [for (var i = 0; i < 7; i++) start.add(Duration(days: i))];
    final visible = [
      for (final day in days)
        if (!day.isBefore(dateOnly(DateTime.now())) || sameDay(day, store.selectedDay))
          day,
    ];

    return ColoredBox(
      color: S.bg(context),
      child: SafeArea(
        bottom: false,
        child: ListView(
          padding: const EdgeInsets.fromLTRB(16, 8, 16, 24),
          children: [
            Row(
              children: [
                TextButton(
                  onPressed: () => store.selectDay(DateTime.now()),
                  child: const Text('Today'),
                ),
                Expanded(
                  child: Text(
                    monthName(store.selectedDay),
                    textAlign: TextAlign.center,
                    style: S.title(context),
                  ),
                ),
                IconButton(
                  onPressed: () => openJobComposer(context),
                  icon: const Icon(Icons.add),
                  color: S.ink(context),
                ),
                IconButton(
                  onPressed: () => showStaffSheet(
                    context,
                    SheetAction(
                      label: 'Post a job',
                      onTap: () {
                        Navigator.pop(context);
                        openJobComposer(context);
                      },
                    ),
                  ),
                  icon: const Icon(Icons.more_horiz),
                  color: S.ink(context),
                ),
              ],
            ),
            const SizedBox(height: 8),
            Row(
              children: [
                for (final day in days)
                  Expanded(
                    child: _DayChip(
                      day: day,
                      selected: sameDay(day, store.selectedDay),
                      marked: store.shiftsOn(day).isNotEmpty,
                      onTap: () => store.selectDay(day),
                    ),
                  ),
              ],
            ),
            const SizedBox(height: 12),
            const Hairline(),
            for (final day in visible) ...[
              const SizedBox(height: 18),
              _DayHeader(day: day),
              const SizedBox(height: 10),
              _DayBody(day: day, jobs: store.shiftsOn(day)),
            ],
          ],
        ),
      ),
    );
  }
}

class _DayChip extends StatelessWidget {
  const _DayChip({
    required this.day,
    required this.selected,
    required this.marked,
    required this.onTap,
  });

  final DateTime day;
  final bool selected;
  final bool marked;
  final VoidCallback onTap;

  @override
  Widget build(BuildContext context) {
    const letters = ['S', 'M', 'T', 'W', 'T', 'F', 'S'];
    return InkWell(
      onTap: onTap,
      child: Column(
        children: [
          Text(letters[day.weekday % 7], style: S.caption(context)),
          const SizedBox(height: 6),
          Container(
            width: 36,
            height: 36,
            alignment: Alignment.center,
            decoration: BoxDecoration(
              color: selected ? S.accent : Colors.transparent,
              shape: BoxShape.circle,
            ),
            child: Text(
              '${day.day}',
              style: TextStyle(
                color: selected ? Colors.white : S.ink(context),
                fontWeight: FontWeight.w600,
              ),
            ),
          ),
          const SizedBox(height: 4),
          Container(
            width: 4,
            height: 4,
            decoration: BoxDecoration(
              color: marked ? S.accent : Colors.transparent,
              shape: BoxShape.circle,
            ),
          ),
        ],
      ),
    );
  }
}

class _DayHeader extends StatelessWidget {
  const _DayHeader({required this.day});

  final DateTime day;

  @override
  Widget build(BuildContext context) {
    final today = dateOnly(DateTime.now());
    final suffix = sameDay(day, today)
        ? ' · Today'
        : sameDay(day, today.add(const Duration(days: 1)))
            ? ' · Tomorrow'
            : '';
    return Row(
      children: [
        Text(
          '${longDate(day)}$suffix',
          style: S.body(context).copyWith(fontWeight: FontWeight.w700),
        ),
        const SizedBox(width: 10),
        Expanded(
          child: Align(
            alignment: Alignment.center,
            child: Container(height: 1, color: S.line(context)),
          ),
        ),
      ],
    );
  }
}

class _DayBody extends StatelessWidget {
  const _DayBody({required this.day, required this.jobs});

  final DateTime day;
  final List<StaffJob> jobs;

  @override
  Widget build(BuildContext context) {
    if (jobs.isEmpty) {
      return Container(
        width: double.infinity,
        padding: const EdgeInsets.all(14),
        decoration: BoxDecoration(
          borderRadius: BorderRadius.circular(12),
          color: S.surface(context),
          border: Border.all(color: S.line(context)),
        ),
        child: Text('No shifts', style: S.caption(context)),
      );
    }
    return Column(
      children: [
        for (final job in jobs)
          Padding(
            padding: const EdgeInsets.only(bottom: 8),
            child: _ShiftCard(job: job, day: day),
          ),
      ],
    );
  }
}

class _ShiftCard extends StatelessWidget {
  const _ShiftCard({required this.job, required this.day});

  final StaffJob job;
  final DateTime day;

  @override
  Widget build(BuildContext context) {
    final shift = job.shifts.firstWhere((item) => sameDay(item.date, day));
    return Material(
      color: S.bg(context),
      borderRadius: BorderRadius.circular(12),
      child: InkWell(
        borderRadius: BorderRadius.circular(12),
        onTap: () => openJob(context, job.id),
        child: Container(
          width: double.infinity,
          padding: const EdgeInsets.all(14),
          decoration: BoxDecoration(
            borderRadius: BorderRadius.circular(12),
            color: S.bg(context),
            border: Border.all(color: S.line(context)),
            boxShadow: S.cardShadow(context),
          ),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Text(
                '${hhmm(shift.startMin)} – ${hhmm(shift.endMin)}',
                style: S.body(context),
              ),
              const SizedBox(height: 4),
              Text(job.name, style: S.section(context)),
              const SizedBox(height: 6),
              Row(
                children: [
                  Icon(Icons.person_outline, size: 16, color: S.muted(context)),
                  const SizedBox(width: 4),
                  Text(
                    '${job.hired} of ${shift.workers} hired',
                    style: S.caption(context),
                  ),
                ],
              ),
            ],
          ),
        ),
      ),
    );
  }
}
