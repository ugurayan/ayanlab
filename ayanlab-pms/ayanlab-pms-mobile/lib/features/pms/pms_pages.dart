import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import '../../core/mock/pms_mock.dart';
import '../../core/state/app_state.dart';

String _woStatus(WorkOrderStatus s) {
  switch (s) {
    case WorkOrderStatus.open:
      return 'Açık';
    case WorkOrderStatus.scheduled:
      return 'Planlandı';
    case WorkOrderStatus.inProgress:
      return 'Devam ediyor';
    case WorkOrderStatus.done:
      return 'Tamamlandı';
  }
}

Color _dueColor(DueStatus s, BuildContext context) {
  switch (s) {
    case DueStatus.overdue:
      return Theme.of(context).colorScheme.error;
    case DueStatus.paid:
      return Colors.green.shade700;
    case DueStatus.upcoming:
      return Theme.of(context).colorScheme.primary;
  }
}

class DuesPage extends StatelessWidget {
  const DuesPage({super.key});

  @override
  Widget build(BuildContext context) {
    final dues = PmsMock.instance.dues;
    return Scaffold(
      appBar: AppBar(title: const Text('Aidat & borç')),
      body: ListView(
        padding: const EdgeInsets.all(16),
        children: [
          for (final d in dues)
            Card(
              child: ListTile(
                title: Text(d.month),
                subtitle: Text(d.breakdown),
                trailing: Column(
                  mainAxisAlignment: MainAxisAlignment.center,
                  crossAxisAlignment: CrossAxisAlignment.end,
                  children: [
                    Text('₺${d.amountTry}',
                        style: TextStyle(
                          fontWeight: FontWeight.bold,
                          color: _dueColor(d.status, context),
                        )),
                    Text(d.status == DueStatus.paid ? 'Ödendi' : 'Bekliyor',
                        style: Theme.of(context).textTheme.labelSmall),
                  ],
                ),
              ),
            ),
          FilledButton.icon(
            onPressed: () => _snack(context, 'Mock ödeme: aidat tahsil edildi'),
            icon: const Icon(Icons.credit_card_rounded),
            label: const Text('Kart ile öde (mock)'),
          ),
        ],
      ),
    );
  }
}

class MaintenancePage extends StatelessWidget {
  const MaintenancePage({super.key});

  @override
  Widget build(BuildContext context) {
    final persona = context.watch<SessionNotifier>().persona;
    final items = persona == PmsPersona.field
        ? PmsMock.instance.workOrders
            .where((w) => w.id == 'WO-1038' || w.id == 'WO-1031')
            .toList()
        : PmsMock.instance.workOrders;

    return Scaffold(
      appBar: AppBar(title: const Text('Bakım / iş emri')),
      body: ListView(
        padding: const EdgeInsets.all(16),
        children: [
          for (final w in items)
            Card(
              child: ListTile(
                title: Text(w.title),
                subtitle: Text('${w.id} · ${w.property}\n${_woStatus(w.status)} · ${w.assignee}'),
                isThreeLine: true,
                trailing: Chip(label: Text(w.priority)),
                onTap: () => _snack(context, '${w.id} detay (mock)'),
              ),
            ),
          if (persona == PmsPersona.resident)
            OutlinedButton.icon(
              onPressed: () => _snack(context, 'Yeni talep oluşturuldu (mock)'),
              icon: const Icon(Icons.add_rounded),
              label: const Text('Yeni talep'),
            ),
        ],
      ),
    );
  }
}

class ViolationsPage extends StatelessWidget {
  const ViolationsPage({super.key});

  @override
  Widget build(BuildContext context) {
    final rows = PmsMock.instance.violations;
    return Scaffold(
      appBar: AppBar(title: const Text('İhtarlar')),
      body: ListView(
        padding: const EdgeInsets.all(16),
        children: [
          for (final v in rows)
            Card(
              child: ListTile(
                title: Text('${v.id} — Daire ${v.unit}'),
                subtitle: Text('${v.rule}\n${v.status}'),
                isThreeLine: true,
                trailing: Text('₺${v.fineTry}',
                    style: const TextStyle(fontWeight: FontWeight.bold)),
              ),
            ),
        ],
      ),
    );
  }
}

class ApprovalsPage extends StatelessWidget {
  const ApprovalsPage({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Onaylar')),
      body: ListView(
        padding: const EdgeInsets.all(16),
        children: [
          for (final a in PmsMock.instance.approvals)
            Card(
              child: ListTile(
                title: Text(a.type),
                subtitle: Text('${a.summary}\n${a.unit} · ${a.submitted}'),
                isThreeLine: true,
                trailing: PopupMenuButton<String>(
                  onSelected: (v) => _snack(context, '$v: ${a.id} (mock)'),
                  itemBuilder: (_) => [
                    const PopupMenuItem(value: 'Onayla', child: Text('Onayla')),
                    const PopupMenuItem(value: 'Reddet', child: Text('Reddet')),
                  ],
                ),
              ),
            ),
        ],
      ),
    );
  }
}

class PatrolPage extends StatefulWidget {
  const PatrolPage({super.key});

  @override
  State<PatrolPage> createState() => _PatrolPageState();
}

class _PatrolPageState extends State<PatrolPage> {
  late List<MockPatrolCheckpoint> _points;

  @override
  void initState() {
    super.initState();
    _points = List.of(PmsMock.instance.patrolRoute);
  }

  void _scan(int index) {
    setState(() {
      _points[index] = MockPatrolCheckpoint(
        name: _points[index].name,
        status: CheckpointStatus.done,
        note: 'QR ${DateTime.now().hour.toString().padLeft(2, '0')}:${DateTime.now().minute.toString().padLeft(2, '0')}',
      );
    });
    _snack(context, 'Checkpoint kaydedildi (mock)');
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Tur kontrol')),
      body: ListView(
        padding: const EdgeInsets.all(16),
        children: [
          Text('Sabah turu — Panorama Rezidans',
              style: Theme.of(context).textTheme.titleMedium),
          const SizedBox(height: 8),
          for (var i = 0; i < _points.length; i++)
            Card(
              child: ListTile(
                leading: Icon(
                  _points[i].status == CheckpointStatus.done
                      ? Icons.check_circle_rounded
                      : Icons.radio_button_unchecked,
                  color: _points[i].status == CheckpointStatus.done
                      ? Colors.green
                      : null,
                ),
                title: Text(_points[i].name),
                subtitle: Text(_points[i].note),
                trailing: _points[i].status == CheckpointStatus.pending
                    ? FilledButton(
                        onPressed: () => _scan(i),
                        child: const Text('QR'),
                      )
                    : null,
              ),
            ),
        ],
      ),
    );
  }
}

class ParcelsPage extends StatelessWidget {
  const ParcelsPage({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Kargo')),
      body: ListView(
        padding: const EdgeInsets.all(16),
        children: [
          for (final p in PmsMock.instance.parcels)
            Card(
              child: ListTile(
                leading: const Icon(Icons.inventory_2_rounded),
                title: Text(p.carrier),
                subtitle: Text('${p.tracking}\n${p.locker} · ${p.arrived}'),
                isThreeLine: true,
                onTap: () => _snack(context, 'Teslim alındı (mock)'),
              ),
            ),
        ],
      ),
    );
  }
}

class AtsPage extends StatefulWidget {
  const AtsPage({super.key});

  @override
  State<AtsPage> createState() => _AtsPageState();
}

class _AtsPageState extends State<AtsPage> {
  @override
  Widget build(BuildContext context) {
    final code = PmsMock.instance.atsCode;
    return Scaffold(
      appBar: AppBar(title: const Text('ATS borç sorgu')),
      body: Padding(
        padding: const EdgeInsets.all(24),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            Text('10 haneli kod', style: Theme.of(context).textTheme.titleMedium),
            const SizedBox(height: 12),
            SelectableText(
              code,
              style: Theme.of(context).textTheme.displaySmall?.copyWith(
                    letterSpacing: 4,
                    fontWeight: FontWeight.bold,
                  ),
              textAlign: TextAlign.center,
            ),
            const SizedBox(height: 8),
            Text(
              'Banka/ATM borç sorgu ekranında kullanılır (mock).',
              style: Theme.of(context).textTheme.bodySmall,
              textAlign: TextAlign.center,
            ),
            const Spacer(),
            FilledButton.icon(
              onPressed: () {
                setState(PmsMock.instance.refreshAts);
                _snack(context, 'Yeni kod üretildi (mock)');
              },
              icon: const Icon(Icons.refresh_rounded),
              label: const Text('Kodu yenile'),
            ),
          ],
        ),
      ),
    );
  }
}

class AnnouncementsPage extends StatelessWidget {
  const AnnouncementsPage({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Duyurular')),
      body: ListView(
        padding: const EdgeInsets.all(16),
        children: [
          for (final a in PmsMock.instance.announcements)
            Card(
              child: ListTile(
                title: Text(a.title),
                subtitle: Text('${a.body}\n${a.time}'),
                isThreeLine: true,
              ),
            ),
        ],
      ),
    );
  }
}

void _snack(BuildContext context, String msg) {
  ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text(msg)));
}