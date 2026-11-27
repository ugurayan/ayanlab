import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import 'staffing_chrome.dart';
import 'staffing_store.dart';
import 'staffing_theme.dart';

class HoursPage extends StatelessWidget {
  const HoursPage({super.key});

  @override
  Widget build(BuildContext context) {
    final store = context.watch<StaffingStore>();
    final pending = store.hours.where((entry) => !entry.approved).toList();
    final approved = store.hours.where((entry) => entry.approved).toList();
    final pendingHours = pending.fold<double>(0, (sum, entry) => sum + entry.hours);
    final approvedHours = approved.fold<double>(0, (sum, entry) => sum + entry.hours);

    return ColoredBox(
      color: S.bg(context),
      child: SafeArea(
        bottom: false,
        child: ListView(
          padding: const EdgeInsets.fromLTRB(16, 8, 16, 24),
          children: [
            Row(
              children: [
                Expanded(child: Text('Hours', style: S.title(context))),
                if (pending.isNotEmpty)
                  TextButton(
                    onPressed: store.approvePending,
                    child: const Text('Approve all'),
                  ),
              ],
            ),
            const SizedBox(height: 8),
            Row(
              children: [
                _Stat(label: 'Approved', value: '${approvedHours.toStringAsFixed(1)} h'),
                const SizedBox(width: 8),
                _Stat(label: 'Pending', value: '${pendingHours.toStringAsFixed(1)} h'),
              ],
            ),
            const SizedBox(height: 20),
            Text('Needs approval', style: S.section(context)),
            const SizedBox(height: 8),
            if (pending.isEmpty)
              Text('Nothing waiting.', style: S.caption(context))
            else
              for (final entry in pending) _HourRow(entry: entry),
            const SizedBox(height: 20),
            Text('Approved', style: S.section(context)),
            const SizedBox(height: 8),
            for (final entry in approved) _HourRow(entry: entry),
          ],
        ),
      ),
    );
  }
}

class _Stat extends StatelessWidget {
  const _Stat({required this.label, required this.value});

  final String label;
  final String value;

  @override
  Widget build(BuildContext context) {
    return Expanded(
      child: Container(
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
            Text(value, style: S.section(context).copyWith(fontSize: 20)),
            const SizedBox(height: 2),
            Text(label, style: S.caption(context)),
          ],
        ),
      ),
    );
  }
}

class _HourRow extends StatelessWidget {
  const _HourRow({required this.entry});

  final HourEntry entry;

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        Padding(
          padding: const EdgeInsets.symmetric(vertical: 12),
          child: Row(
            children: [
              Initials(name: entry.worker, radius: 20),
              const SizedBox(width: 12),
              Expanded(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(entry.worker, style: S.body(context).copyWith(fontWeight: FontWeight.w600)),
                    Text(
                      '${weekdayShort(entry.date)} ${entry.date.day} ${monthShort(entry.date)} · ${entry.role}',
                      style: S.caption(context),
                    ),
                    Text('${entry.time} · ${entry.hours.toStringAsFixed(1)} h', style: S.caption(context)),
                  ],
                ),
              ),
              if (!entry.approved)
                TextButton(
                  onPressed: () => context.read<StaffingStore>().approveHour(entry.id),
                  child: const Text('Approve'),
                )
              else
                Icon(Icons.check_circle, color: S.green, size: 20),
            ],
          ),
        ),
        const Hairline(),
      ],
    );
  }
}
