import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import 'staffing_chrome.dart';
import 'staffing_store.dart';
import 'staffing_theme.dart';

class ApplicantPage extends StatelessWidget {
  const ApplicantPage({super.key, required this.jobId, required this.applicantId});

  final String jobId;
  final String applicantId;

  @override
  Widget build(BuildContext context) {
    final store = context.watch<StaffingStore>();
    final job = store.jobById(jobId);
    Applicant? person;
    if (job != null) {
      for (final applicant in job.applicants) {
        if (applicant.id == applicantId) person = applicant;
      }
    }
    if (job == null || person == null) {
      return const FlowPage(title: 'Applied', body: SizedBox.shrink());
    }
    final applicant = person;
    final canDecide = applicant.status == 'applied' && job.hired < job.needed;

    return FlowPage(
      title: 'Applied',
      actions: [
        IconButton(
          onPressed: () => store.toggleSaved(job.id, applicant.id),
          icon: Icon(applicant.saved ? Icons.bookmark : Icons.bookmark_border),
          color: S.ink(context),
        ),
        IconButton(
          onPressed: () => showStaffSheet(
            context,
            SheetAction(
              label: applicant.saved ? 'Remove bookmark' : 'Bookmark',
              onTap: () {
                store.toggleSaved(job.id, applicant.id);
                Navigator.pop(context);
              },
            ),
          ),
          icon: const Icon(Icons.more_horiz),
          color: S.ink(context),
        ),
      ],
      body: ListView(
        padding: const EdgeInsets.fromLTRB(16, 8, 16, 24),
        children: [
          Row(
            children: [
              Initials(name: applicant.name, radius: 36),
              const SizedBox(width: 14),
              Expanded(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(applicant.name, style: S.body(context).copyWith(fontWeight: FontWeight.w700, fontSize: 18)),
                    Text(applicant.role, style: S.caption(context)),
                    const SizedBox(height: 4),
                    Row(
                      children: [
                        StarRow(rating: applicant.rating),
                        const SizedBox(width: 6),
                        Text(
                          '${applicant.rating.toStringAsFixed(1)} (${applicant.reviews})',
                          style: S.caption(context),
                        ),
                      ],
                    ),
                  ],
                ),
              ),
            ],
          ),
          const SizedBox(height: 16),
          OutlinedButton(
            onPressed: () => Navigator.of(context).push(
              MaterialPageRoute<void>(builder: (_) => CvPage(applicant: applicant)),
            ),
            style: OutlinedButton.styleFrom(
              minimumSize: const Size.fromHeight(48),
              foregroundColor: S.ink(context),
              side: BorderSide(color: S.line(context)),
              shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
            ),
            child: const Text('VIEW CV', style: TextStyle(fontWeight: FontWeight.w700, letterSpacing: 0.6)),
          ),
          const SizedBox(height: 16),
          _Fact(icon: Icons.payments_outlined, text: applicant.wageLabel),
          _Fact(icon: Icons.star_outline, text: 'Favourite of ${applicant.favouriteOf} companies'),
          _Fact(icon: Icons.directions_transit_outlined, text: applicant.travel),
          const SizedBox(height: 16),
          Text('Contacts', style: S.section(context)),
          const SizedBox(height: 8),
          _Fact(icon: Icons.mail_outline, text: applicant.email),
          _Fact(icon: Icons.phone_outlined, text: applicant.phone),
          _Fact(icon: Icons.place_outlined, text: applicant.area),
          const SizedBox(height: 16),
          Text('About', style: S.section(context)),
          const SizedBox(height: 6),
          Text(applicant.about, style: S.body(context)),
          const SizedBox(height: 18),
          Text('Soft skills', style: S.section(context)),
          Text(
            'The number is how many workplaces confirmed the skill.',
            style: S.caption(context),
          ),
          for (final (label, count) in applicant.softSkills)
            Padding(
              padding: const EdgeInsets.only(top: 10),
              child: Text('$label · $count', style: S.body(context)),
            ),
          const SizedBox(height: 18),
          Text('Skills and expertise', style: S.section(context)),
          for (final (label, count) in applicant.skills)
            Padding(
              padding: const EdgeInsets.only(top: 10),
              child: Text('$label · $count', style: S.body(context)),
            ),
          const SizedBox(height: 18),
          Text('Languages', style: S.section(context)),
          for (final (name, spoken, written) in applicant.languages) ...[
            const SizedBox(height: 10),
            Text(name, style: S.body(context).copyWith(fontWeight: FontWeight.w600)),
            Text('Spoken: $spoken, Written: $written', style: S.caption(context)),
          ],
          const SizedBox(height: 18),
          Text('Recent shifts', style: S.section(context)),
          for (final (date, venue, role) in applicant.history)
            Padding(
              padding: const EdgeInsets.only(top: 12),
              child: Row(
                children: [
                  IconThumb(icon: Icons.storefront_outlined),
                  const SizedBox(width: 12),
                  Expanded(
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Text(date, style: S.caption(context)),
                        Text(venue, style: S.body(context).copyWith(fontWeight: FontWeight.w600)),
                        Text(role, style: S.caption(context)),
                      ],
                    ),
                  ),
                ],
              ),
            ),
          const SizedBox(height: 18),
          Text('Linked documents', style: S.section(context)),
          for (final doc in applicant.documents)
            Align(
              alignment: Alignment.centerLeft,
              child: TextButton(
                onPressed: () => Navigator.of(context).push(
                  MaterialPageRoute<void>(builder: (_) => DocumentPage(title: doc)),
                ),
                child: Text(doc.toUpperCase()),
              ),
            ),
        ],
      ),
      footer: applicant.status != 'applied'
          ? StaffFooter(
              label: applicant.status == 'hired' ? 'HIRED' : 'DECLINED',
              onPressed: null,
            )
          : StaffFooter(
              label: 'HIRE',
              filled: true,
              onPressed: canDecide
                  ? () {
                      final messenger = ScaffoldMessenger.of(context);
                      store.hire(job.id, applicant.id);
                      Navigator.pop(context);
                      messenger.showSnackBar(
                        SnackBar(content: Text('${applicant.name} is hired for ${job.name}.')),
                      );
                    }
                  : null,
              secondary: SizedBox(
                width: double.infinity,
                height: 48,
                child: OutlinedButton(
                  onPressed: () async {
                    final confirmed = await showDialog<bool>(
                      context: context,
                      builder: (context) => AlertDialog(
                        title: const Text('Decline application?'),
                        content: Text('${applicant.name} will not be hired for this job.'),
                        actions: [
                          TextButton(onPressed: () => Navigator.pop(context, false), child: const Text('Back')),
                          TextButton(onPressed: () => Navigator.pop(context, true), child: const Text('Decline')),
                        ],
                      ),
                    );
                    if (confirmed == true && context.mounted) {
                      store.decline(job.id, applicant.id);
                      Navigator.pop(context);
                    }
                  },
                  style: OutlinedButton.styleFrom(
                    foregroundColor: S.ink(context),
                    side: BorderSide(color: S.line(context)),
                    shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
                  ),
                  child: const Text('DECLINE', style: TextStyle(fontWeight: FontWeight.w700, letterSpacing: 0.6)),
                ),
              ),
            ),
    );
  }
}

class _Fact extends StatelessWidget {
  const _Fact({required this.icon, required this.text});

  final IconData icon;
  final String text;

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 6),
      child: Row(
        children: [
          Icon(icon, size: 20, color: S.ink(context)),
          const SizedBox(width: 10),
          Expanded(child: Text(text, style: S.body(context))),
        ],
      ),
    );
  }
}

class CvPage extends StatelessWidget {
  const CvPage({super.key, required this.applicant});

  final Applicant applicant;

  @override
  Widget build(BuildContext context) {
    return FlowPage(
      title: 'CV',
      body: ListView(
        padding: const EdgeInsets.all(16),
        children: [
          Text(applicant.name, style: S.section(context).copyWith(fontSize: 24)),
          Text(applicant.role, style: S.caption(context)),
          const SizedBox(height: 16),
          Text(applicant.about, style: S.body(context)),
          const SizedBox(height: 16),
          Text('Experience on AyanLab', style: S.section(context)),
          for (final (date, venue, role) in applicant.history)
            Padding(
              padding: const EdgeInsets.only(top: 10),
              child: Text('$date · $venue · $role', style: S.body(context)),
            ),
          const SizedBox(height: 16),
          Text(
            'This is the profile summary stored with the application. A full file download is not connected.',
            style: S.caption(context),
          ),
        ],
      ),
    );
  }
}

class DocumentPage extends StatelessWidget {
  const DocumentPage({super.key, required this.title});

  final String title;

  @override
  Widget build(BuildContext context) {
    return FlowPage(
      title: title,
      body: Padding(
        padding: const EdgeInsets.all(16),
        child: Text(
          'A verified copy is on file. Opening the original document needs the worker-document service, which this screen does not call yet.',
          style: S.body(context),
        ),
      ),
    );
  }
}
