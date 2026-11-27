import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import 'dress_flow.dart';
import 'schedule_flow.dart';
import 'staffing_chrome.dart';
import 'staffing_store.dart';
import 'staffing_theme.dart';

void openJobComposer(BuildContext context) {
  context.read<StaffingStore>().startBlank();
  Navigator.of(context).push(
    MaterialPageRoute<void>(builder: (_) => const CreateJobPage()),
  );
}

class CreateJobPage extends StatefulWidget {
  const CreateJobPage({super.key});

  @override
  State<CreateJobPage> createState() => _CreateJobPageState();
}

class _CreateJobPageState extends State<CreateJobPage> {
  var _query = '';
  var _templatesOpen = false;
  var _recentOpen = true;

  @override
  Widget build(BuildContext context) {
    final store = context.watch<StaffingStore>();
    final query = _query.trim().toLowerCase();
    final templates = [
      for (final profile in jobProfiles)
        if (query.isEmpty || profile.name.toLowerCase().contains(query)) profile,
    ].take(6);
    final recent = [
      for (final job in store.jobs)
        if (query.isEmpty || job.name.toLowerCase().contains(query)) job,
    ];

    return FlowPage(
      title: 'Create job',
      closeIcon: true,
      body: ListView(
        padding: const EdgeInsets.fromLTRB(16, 8, 16, 24),
        children: [
          SearchBox(onChanged: (value) => setState(() => _query = value)),
          AddRow(
            label: 'Create new job',
            onTap: () => Navigator.of(context).push(
              MaterialPageRoute<void>(builder: (_) => const JobProfilesPage()),
            ),
          ),
          const Hairline(),
          _Fold(
            title: 'Job templates',
            open: _templatesOpen,
            onTap: () => setState(() => _templatesOpen = !_templatesOpen),
          ),
          if (_templatesOpen)
            for (final profile in templates)
              ListTile(
                contentPadding: EdgeInsets.zero,
                title: Text(profile.name, style: S.body(context)),
                subtitle: Text(
                  profile.summary,
                  maxLines: 2,
                  overflow: TextOverflow.ellipsis,
                  style: S.caption(context),
                ),
                onTap: () {
                  store.applyTemplate(profile.name);
                  Navigator.of(context).push(
                    MaterialPageRoute<void>(builder: (_) => const ShiftsPage()),
                  );
                },
              ),
          const Hairline(),
          _Fold(
            title: 'Recently created jobs',
            open: _recentOpen,
            onTap: () => setState(() => _recentOpen = !_recentOpen),
          ),
          if (_recentOpen)
            for (final job in recent)
              ListTile(
                contentPadding: EdgeInsets.zero,
                title: Text(job.name, style: S.body(context).copyWith(fontWeight: FontWeight.w600)),
                subtitle: Text(job.roles.join(', '), style: S.caption(context)),
                onTap: () {
                  store.cloneJob(job);
                  Navigator.of(context).push(
                    MaterialPageRoute<void>(builder: (_) => const OverviewPage()),
                  );
                },
              ),
        ],
      ),
    );
  }
}

class _Fold extends StatelessWidget {
  const _Fold({required this.title, required this.open, required this.onTap});

  final String title;
  final bool open;
  final VoidCallback onTap;

  @override
  Widget build(BuildContext context) {
    return InkWell(
      onTap: onTap,
      child: Padding(
        padding: const EdgeInsets.symmetric(vertical: 14),
        child: Row(
          children: [
            Expanded(child: Text(title, style: S.body(context))),
            Icon(open ? Icons.expand_less : Icons.expand_more, color: S.muted(context)),
          ],
        ),
      ),
    );
  }
}

class JobProfilesPage extends StatefulWidget {
  const JobProfilesPage({super.key, this.pickOnly = false});

  final bool pickOnly;

  @override
  State<JobProfilesPage> createState() => _JobProfilesPageState();
}

class _JobProfilesPageState extends State<JobProfilesPage> {
  var _query = '';

  @override
  Widget build(BuildContext context) {
    final store = context.read<StaffingStore>();
    final query = _query.trim().toLowerCase();
    final profiles = [
      for (final profile in jobProfiles)
        if (query.isEmpty ||
            profile.name.toLowerCase().contains(query) ||
            profile.summary.toLowerCase().contains(query))
          profile,
    ];

    return FlowPage(
      title: 'Job profiles',
      closeIcon: true,
      body: Column(
        children: [
          Padding(
            padding: const EdgeInsets.fromLTRB(16, 8, 16, 8),
            child: SearchBox(onChanged: (value) => setState(() => _query = value)),
          ),
          const Hairline(),
          Expanded(
            child: ListView(
              padding: const EdgeInsets.fromLTRB(16, 8, 16, 24),
              children: [
                Padding(
                  padding: const EdgeInsets.symmetric(vertical: 8),
                  child: Text('All (${profiles.length})', style: S.body(context).copyWith(fontWeight: FontWeight.w700)),
                ),
                for (final profile in profiles) ...[
                  InkWell(
                    onTap: () {
                      store.applyProfile(profile, append: widget.pickOnly);
                      if (widget.pickOnly) {
                        Navigator.pop(context);
                      } else {
                        Navigator.of(context).push(
                          MaterialPageRoute<void>(builder: (_) => const RoleWagePage()),
                        );
                      }
                    },
                    child: Padding(
                      padding: const EdgeInsets.symmetric(vertical: 12),
                      child: Row(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          Expanded(
                            child: Column(
                              crossAxisAlignment: CrossAxisAlignment.start,
                              children: [
                                Text(profile.name, style: S.body(context).copyWith(fontWeight: FontWeight.w700)),
                                const SizedBox(height: 4),
                                Text(profile.summary, style: S.caption(context)),
                              ],
                            ),
                          ),
                          Icon(Icons.chevron_right, color: S.muted(context)),
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
    );
  }
}

class RoleWagePage extends StatefulWidget {
  const RoleWagePage({super.key});

  @override
  State<RoleWagePage> createState() => _RoleWagePageState();
}

class _RoleWagePageState extends State<RoleWagePage> {
  late final TextEditingController _wage;

  @override
  void initState() {
    super.initState();
    _wage = TextEditingController(
      text: context.read<StaffingStore>().draft.wage.toStringAsFixed(2),
    );
  }

  @override
  void dispose() {
    _wage.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final store = context.watch<StaffingStore>();
    final draft = store.draft;
    return FlowPage(
      title: 'Who do you need?',
      closeIcon: true,
      body: ListView(
        padding: const EdgeInsets.fromLTRB(16, 8, 16, 24),
        children: [
          Row(
            children: [
              Expanded(child: Text('Wage (CHF/h)', style: S.body(context).copyWith(fontWeight: FontWeight.w700))),
              Expanded(child: Text('Cost (CHF/h)', style: S.body(context).copyWith(fontWeight: FontWeight.w700))),
            ],
          ),
          const SizedBox(height: 8),
          Row(
            children: [
              Expanded(
                child: TextField(
                  controller: _wage,
                  keyboardType: const TextInputType.numberWithOptions(decimal: true),
                  decoration: staffField(context),
                  onChanged: (value) {
                    final parsed = double.tryParse(value.replaceAll(',', '.'));
                    if (parsed != null && parsed > 0) {
                      draft.wage = parsed;
                      store.touch();
                    }
                  },
                ),
              ),
              const SizedBox(width: 12),
              Expanded(
                child: Container(
                  padding: const EdgeInsets.symmetric(horizontal: 14, vertical: 14),
                  decoration: BoxDecoration(
                    borderRadius: BorderRadius.circular(10),
                    border: Border.all(color: S.line(context)),
                  ),
                  child: Text(draft.cost.toStringAsFixed(2), style: S.body(context)),
                ),
              ),
            ],
          ),
          const SizedBox(height: 8),
          Text(
            'Cost includes the staffing fee on top of the wage.',
            style: S.caption(context),
          ),
          const SizedBox(height: 16),
          for (final role in draft.roles)
            Row(
              children: [
                Expanded(child: Text(role, style: S.body(context))),
                IconButton(
                  onPressed: () {
                    draft.roles.remove(role);
                    store.touch();
                  },
                  icon: Icon(Icons.close, color: S.muted(context)),
                ),
              ],
            ),
          AddRow(
            label: 'Add job role',
            onTap: () => Navigator.of(context).push(
              MaterialPageRoute<void>(
                builder: (_) => const JobProfilesPage(pickOnly: true),
              ),
            ),
          ),
        ],
      ),
      footer: StaffFooter(
        label: 'CONTINUE',
        onPressed: draft.roles.isEmpty
            ? null
            : () => Navigator.of(context).push(
                  MaterialPageRoute<void>(builder: (_) => const LocationPage()),
                ),
      ),
    );
  }
}

class LocationPage extends StatefulWidget {
  const LocationPage({super.key});

  @override
  State<LocationPage> createState() => _LocationPageState();
}

class _LocationPageState extends State<LocationPage> {
  late final TextEditingController _notes;

  @override
  void initState() {
    super.initState();
    _notes = TextEditingController(text: context.read<StaffingStore>().draft.locationNotes);
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
      title: 'Worksite',
      body: ListView(
        padding: const EdgeInsets.fromLTRB(16, 8, 16, 24),
        children: [
          Text(
            'Where should people meet?',
            style: S.caption(context),
          ),
          const SizedBox(height: 8),
          for (final site in worksites)
            InkWell(
              onTap: () {
                store.draft.locationId = site.id;
                store.touch();
              },
              child: Padding(
                padding: const EdgeInsets.symmetric(vertical: 12),
                child: Row(
                  children: [
                    Expanded(
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          Text(site.name, style: S.body(context).copyWith(fontWeight: FontWeight.w600)),
                          Text(site.address, style: S.caption(context)),
                        ],
                      ),
                    ),
                    RadioDot(selected: store.draft.locationId == site.id),
                  ],
                ),
              ),
            ),
          const SizedBox(height: 12),
          Row(
            children: [
              Expanded(child: Text('Arrival notes', style: S.body(context))),
              Text('Optional', style: S.caption(context)),
            ],
          ),
          const SizedBox(height: 8),
          TextField(
            controller: _notes,
            minLines: 2,
            maxLines: 4,
            decoration: staffField(context, hint: 'Door, parking, who to ask for'),
            onChanged: (value) => store.draft.locationNotes = value,
          ),
        ],
      ),
      footer: StaffFooter(
        label: 'CONTINUE',
        onPressed: store.draft.locationId.isEmpty
            ? null
            : () => Navigator.of(context).push(
                  MaterialPageRoute<void>(builder: (_) => const SkillsPage()),
                ),
      ),
    );
  }
}

class SkillsPage extends StatelessWidget {
  const SkillsPage({super.key});

  @override
  Widget build(BuildContext context) {
    final store = context.watch<StaffingStore>();
    final draft = store.draft;
    return FlowPage(
      title: 'Required skills',
      body: ListView(
        padding: const EdgeInsets.fromLTRB(16, 8, 16, 24),
        children: [
          Text('Expertise', style: S.section(context)),
          const SizedBox(height: 10),
          Wrap(
            spacing: 8,
            runSpacing: 8,
            children: [
              for (final item in expertiseOptions)
                SelectChip(
                  label: item,
                  selected: draft.expertise.contains(item),
                  onTap: () {
                    draft.expertise.contains(item)
                        ? draft.expertise.remove(item)
                        : draft.expertise.add(item);
                    store.touch();
                  },
                ),
            ],
          ),
          const SizedBox(height: 22),
          Text('Skills', style: S.section(context)),
          const SizedBox(height: 10),
          Wrap(
            spacing: 8,
            runSpacing: 8,
            children: [
              for (final item in skillOptions)
                SelectChip(
                  label: item,
                  selected: draft.skills.contains(item),
                  onTap: () {
                    draft.skills.contains(item)
                        ? draft.skills.remove(item)
                        : draft.skills.add(item);
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
          MaterialPageRoute<void>(builder: (_) => const CertificatesPage()),
        ),
      ),
    );
  }
}

class CertificatesPage extends StatelessWidget {
  const CertificatesPage({super.key});

  @override
  Widget build(BuildContext context) {
    final store = context.watch<StaffingStore>();
    return FlowPage(
      title: 'Certificates',
      body: ListView(
        padding: const EdgeInsets.fromLTRB(16, 8, 16, 24),
        children: [
          Text(
            'Optional. People without these can still apply, and you will see who holds them.',
            style: S.caption(context),
          ),
          const SizedBox(height: 16),
          for (final item in certificateOptions)
            InkWell(
              onTap: () {
                store.draft.certificates.contains(item)
                    ? store.draft.certificates.remove(item)
                    : store.draft.certificates.add(item);
                store.touch();
              },
              child: Padding(
                padding: const EdgeInsets.symmetric(vertical: 14),
                child: Row(
                  children: [
                    Expanded(child: Text(item, style: S.body(context))),
                    CheckBox(selected: store.draft.certificates.contains(item)),
                  ],
                ),
              ),
            ),
        ],
      ),
      footer: StaffFooter(
        label: 'CONTINUE',
        onPressed: () => Navigator.of(context).push(
          MaterialPageRoute<void>(builder: (_) => const DressPage()),
        ),
      ),
    );
  }
}
