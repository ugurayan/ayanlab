import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import 'create_flow.dart';
import 'job_detail_page.dart';
import 'staffing_chrome.dart';
import 'staffing_store.dart';
import 'staffing_theme.dart';

class JobsPage extends StatefulWidget {
  const JobsPage({super.key});

  @override
  State<JobsPage> createState() => _JobsPageState();
}

class _JobsPageState extends State<JobsPage> {
  var _filled = false;

  @override
  Widget build(BuildContext context) {
    final store = context.watch<StaffingStore>();
    final jobs = [
      for (final job in store.jobs)
        if (job.open && job.filled == _filled) job,
    ];

    return ColoredBox(
      color: S.bg(context),
      child: SafeArea(
        bottom: false,
        child: Column(
          children: [
            SizedBox(
              height: 52,
              child: Row(
                children: [
                  const SizedBox(width: 48),
                  Expanded(
                    child: Text(
                      'My jobs',
                      textAlign: TextAlign.center,
                      style: S.title(context),
                    ),
                  ),
                  IconButton(
                    onPressed: () => openJobComposer(context),
                    icon: const Icon(Icons.add),
                    color: S.ink(context),
                  ),
                ],
              ),
            ),
            Row(
              children: [
                _Tab(
                  label: 'Understaffed',
                  selected: !_filled,
                  onTap: () => setState(() => _filled = false),
                ),
                _Tab(
                  label: 'Fully staffed',
                  selected: _filled,
                  onTap: () => setState(() => _filled = true),
                ),
              ],
            ),
            const Hairline(),
            Expanded(
              child: jobs.isEmpty
                  ? Center(
                      child: Padding(
                        padding: const EdgeInsets.all(32),
                        child: Text(
                          _filled
                              ? 'No job is fully staffed yet.'
                              : 'No open jobs. Tap + to post one.',
                          textAlign: TextAlign.center,
                          style: S.caption(context),
                        ),
                      ),
                    )
                  : ListView.separated(
                      itemCount: jobs.length,
                      separatorBuilder: (_, index) => const Hairline(),
                      itemBuilder: (context, index) => _JobRow(job: jobs[index]),
                    ),
            ),
          ],
        ),
      ),
    );
  }
}

class _Tab extends StatelessWidget {
  const _Tab({required this.label, required this.selected, required this.onTap});

  final String label;
  final bool selected;
  final VoidCallback onTap;

  @override
  Widget build(BuildContext context) {
    return Expanded(
      child: InkWell(
        onTap: onTap,
        child: Column(
          children: [
            const SizedBox(height: 12),
            Text(
              label,
              style: TextStyle(
                color: selected ? S.ink(context) : S.muted(context),
                fontWeight: selected ? FontWeight.w700 : FontWeight.w500,
              ),
            ),
            const SizedBox(height: 10),
            Container(
              height: 3,
              color: selected ? S.accent : Colors.transparent,
            ),
          ],
        ),
      ),
    );
  }
}

class _JobRow extends StatelessWidget {
  const _JobRow({required this.job});

  final StaffJob job;

  @override
  Widget build(BuildContext context) {
    final shift = job.firstShift;
    final when = shift == null ? '' : weekdayShort(shift.date);
    final applicants = job.appliedCount == 1
        ? '1 applicant'
        : '${job.appliedCount} applicants';
    return InkWell(
      onTap: () => openJob(context, job.id),
      child: Padding(
        padding: const EdgeInsets.fromLTRB(16, 14, 16, 14),
        child: Row(
          children: [
            Expanded(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  if (when.isNotEmpty)
                    Text(when, style: S.caption(context)),
                  Text(job.name, style: S.section(context)),
                  const SizedBox(height: 2),
                  Text(applicants, style: S.caption(context)),
                ],
              ),
            ),
            RadioDot(selected: job.hired > 0, active: job.filled),
            const SizedBox(width: 8),
            Text(
              '${job.hired}/${job.needed}',
              style: S.body(context).copyWith(fontWeight: FontWeight.w600),
            ),
          ],
        ),
      ),
    );
  }
}
