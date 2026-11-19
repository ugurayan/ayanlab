import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import '../../core/l10n/app_localizations.dart';
import '../../core/mock/pms_mock.dart';
import '../../core/state/app_state.dart';

/// Home tab: persona-specific PMS dashboard (mock).
class HomePage extends StatelessWidget {
  const HomePage({super.key});

  @override
  Widget build(BuildContext context) {
    final loc = AppLocalizations.of(context)!;
    final session = context.watch<SessionNotifier>();
    final persona = session.persona;

    return ListView(
      padding: const EdgeInsets.all(16),
      children: [
        Card(
          child: Padding(
            padding: const EdgeInsets.all(16),
            child: Row(
              children: [
                CircleAvatar(
                  radius: 28,
                  child: Icon(persona.icon),
                ),
                const SizedBox(width: 12),
                Expanded(
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text(
                        loc.homeGreeting(session.name),
                        style: Theme.of(context).textTheme.titleLarge,
                      ),
                      Text(
                        '${persona.label} · ${session.email}',
                        style: Theme.of(context).textTheme.bodySmall,
                      ),
                    ],
                  ),
                ),
                Chip(
                  label: Text('MOCK'),
                  visualDensity: VisualDensity.compact,
                ),
              ],
            ),
          ),
        ),
        const SizedBox(height: 12),
        ...switch (persona) {
          PmsPersona.manager => _managerCards(context),
          PmsPersona.resident => _residentCards(context),
          PmsPersona.field => _fieldCards(context),
        },
        const SizedBox(height: 8),
        Text('Modüller', style: Theme.of(context).textTheme.titleMedium),
        const SizedBox(height: 8),
        for (final m in pmsModules.where((x) => x.personas.contains(persona)))
          Card(
            child: ListTile(
              leading: Icon(m.icon),
              title: Text(m.title),
              subtitle: Text(m.subtitle),
              trailing: const Icon(Icons.chevron_right_rounded),
              onTap: () => Navigator.of(context).pushNamed(m.route),
            ),
          ),
        const SizedBox(height: 8),
        Text(loc.mockNotice, style: Theme.of(context).textTheme.bodySmall),
      ],
    );
  }

  List<Widget> _managerCards(BuildContext context) {
    return [
      _statRow(context, [
        _StatCard(
          label: 'Onay',
          value: '${PmsMock.instance.approvals.length}',
          icon: Icons.fact_check_rounded,
          route: '/pms/approvals',
        ),
        _StatCard(
          label: 'Geciken WO',
          value: '1',
          icon: Icons.warning_amber_rounded,
          route: '/pms/maintenance',
        ),
      ]),
      const SizedBox(height: 8),
      _statRow(context, [
        _StatCard(
          label: 'Açık ihtar',
          value: '${PmsMock.instance.violations.length}',
          icon: Icons.gavel_rounded,
          route: '/pms/violations',
        ),
        _StatCard(
          label: 'Banka',
          value: '4',
          icon: Icons.account_balance_rounded,
          route: null,
        ),
      ]),
    ];
  }

  List<Widget> _residentCards(BuildContext context) {
    final due = PmsMock.instance.dues.first;
    return [
      Card(
        color: Theme.of(context).colorScheme.errorContainer.withValues(alpha: 0.4),
        child: ListTile(
          leading: const Icon(Icons.payments_rounded),
          title: const Text('Güncel borç'),
          subtitle: Text(due.breakdown),
          trailing: Text(
            '₺${due.amountTry}',
            style: Theme.of(context).textTheme.titleLarge,
          ),
          onTap: () => Navigator.of(context).pushNamed('/pms/dues'),
        ),
      ),
      const SizedBox(height: 8),
      _statRow(context, [
        _StatCard(
          label: 'Kargo',
          value: '${PmsMock.instance.parcels.length}',
          icon: Icons.local_shipping_rounded,
          route: '/pms/parcels',
        ),
        _StatCard(
          label: 'ATS kod',
          value: '●●●',
          icon: Icons.pin_rounded,
          route: '/pms/ats',
        ),
      ]),
    ];
  }

  List<Widget> _fieldCards(BuildContext context) {
    final pending = PmsMock.instance.patrolRoute
        .where((c) => c.status == CheckpointStatus.pending)
        .length;
    return [
      Card(
        child: ListTile(
          leading: const Icon(Icons.route_rounded),
          title: const Text('Aktif tur'),
          subtitle: Text('$pending checkpoint kaldı'),
          trailing: const Icon(Icons.chevron_right_rounded),
          onTap: () => Navigator.of(context).pushNamed('/pms/patrol'),
        ),
      ),
      const SizedBox(height: 8),
      _statRow(context, [
        _StatCard(
          label: 'İş emri',
          value: '2',
          icon: Icons.handyman_rounded,
          route: '/pms/maintenance',
        ),
        _StatCard(
          label: 'Duyuru',
          value: '${PmsMock.instance.announcements.length}',
          icon: Icons.campaign_rounded,
          route: '/pms/announcements',
        ),
      ]),
    ];
  }

  Widget _statRow(BuildContext context, List<_StatCard> cards) {
    return Row(
      children: [
        for (var i = 0; i < cards.length; i++) ...[
          if (i > 0) const SizedBox(width: 8),
          Expanded(child: cards[i].build(context)),
        ],
      ],
    );
  }
}

class _StatCard {
  const _StatCard({
    required this.label,
    required this.value,
    required this.icon,
    required this.route,
  });

  final String label;
  final String value;
  final IconData icon;
  final String? route;

  Widget build(BuildContext context) {
    return Card(
      child: InkWell(
        borderRadius: BorderRadius.circular(12),
        onTap: route == null
            ? null
            : () => Navigator.of(context).pushNamed(route!),
        child: Padding(
          padding: const EdgeInsets.all(12),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Icon(icon, size: 22),
              const SizedBox(height: 8),
              Text(value, style: Theme.of(context).textTheme.headlineSmall),
              Text(label, style: Theme.of(context).textTheme.labelMedium),
            ],
          ),
        ),
      ),
    );
  }
}