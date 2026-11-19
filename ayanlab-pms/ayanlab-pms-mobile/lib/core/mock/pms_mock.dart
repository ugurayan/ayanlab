import 'package:flutter/material.dart';

/// PMS mock store — no HTTP; mirrors web `mockDb` themes (TR HOA + rental).
class PmsMock {
  PmsMock._();
  static final instance = PmsMock._();

  String atsCode = '4829103746';

  final List<MockDue> dues = [
    const MockDue(
      month: 'Mart 2026',
      amountTry: 4250,
      status: DueStatus.overdue,
      breakdown: 'Aidat 3.200 · Isınma 650 · Su 400',
    ),
    const MockDue(
      month: 'Şubat 2026',
      amountTry: 4180,
      status: DueStatus.paid,
      breakdown: 'Aidat 3.200 · Isınma 580 · Su 400',
    ),
  ];

  final List<MockWorkOrder> workOrders = [
    const MockWorkOrder(
      id: 'WO-1042',
      title: 'Banyo su kaçağı — Daire 12B',
      property: 'Panorama Rezidans',
      status: WorkOrderStatus.inProgress,
      priority: 'Yüksek',
      assignee: 'Atlas Tesisat',
    ),
    const MockWorkOrder(
      id: 'WO-1038',
      title: 'Asansör aydınlatma arızası',
      property: 'Yeşil Vadi Sitesi',
      status: WorkOrderStatus.open,
      priority: 'Orta',
      assignee: '—',
    ),
    const MockWorkOrder(
      id: 'WO-1031',
      title: 'Kapı otomatı kalibrasyon',
      property: 'Panorama Rezidans',
      status: WorkOrderStatus.scheduled,
      priority: 'Düşük',
      assignee: 'Site güvenlik',
    ),
  ];

  final List<MockViolation> violations = [
    const MockViolation(
      id: 'V-220',
      unit: '12B',
      rule: 'Balkonda kurutma',
      fineTry: 850,
      status: 'Açık',
    ),
    const MockViolation(
      id: 'V-215',
      unit: '8A',
      rule: 'Gürültü (22:30)',
      fineTry: 1200,
      status: 'Ödeme bekliyor',
    ),
  ];

  final List<MockApproval> approvals = [
    const MockApproval(
      id: 'AR-18',
      type: 'Mimari onay',
      unit: '5C',
      summary: 'Mutfak tezgahı değişimi',
      submitted: '2 gün önce',
    ),
    const MockApproval(
      id: 'EFT-44',
      type: 'EFT onayı',
      unit: '—',
      summary: '₺18.400 vendor ödemesi',
      submitted: 'Bugün',
    ),
    const MockApproval(
      id: 'V-220',
      type: 'İhtar onayı',
      unit: '12B',
      summary: 'Balkon kurutma ihtarı',
      submitted: 'Dün',
    ),
  ];

  final List<MockPatrolCheckpoint> patrolRoute = [
    const MockPatrolCheckpoint(
      name: 'Ana giriş — turnike',
      status: CheckpointStatus.done,
      note: '10:02 QR',
    ),
    const MockPatrolCheckpoint(
      name: 'Otopark B2',
      status: CheckpointStatus.done,
      note: '10:18 foto',
    ),
    const MockPatrolCheckpoint(
      name: 'Çocuk parkı',
      status: CheckpointStatus.pending,
      note: '—',
    ),
    const MockPatrolCheckpoint(
      name: 'Jeneratör odası',
      status: CheckpointStatus.pending,
      note: '—',
    ),
  ];

  final List<MockParcel> parcels = [
    const MockParcel(
      carrier: 'Yurtiçi',
      tracking: 'YK78299102',
      locker: 'Koli dolabı A3',
      arrived: 'Bugün 14:20',
    ),
    const MockParcel(
      carrier: 'MNG',
      tracking: 'MNG4492011',
      locker: 'Güvenlik',
      arrived: 'Dün',
    ),
  ];

  final List<MockAnnouncement> announcements = [
    const MockAnnouncement(
      title: 'Genel kurul 28 Mart',
      body: 'Salon katı, 19:00. Gündem: bütçe ve asansör yenileme.',
      time: '3 saat önce',
    ),
    const MockAnnouncement(
      title: 'Su kesintisi',
      body: 'Yarın 09:00–12:00 ana şebeke bakımı.',
      time: 'Dün',
    ),
  ];

  List<MockNotification> notificationsFor(PmsPersona persona) {
    switch (persona) {
      case PmsPersona.manager:
        return const [
          MockNotification(
            title: '3 onay bekliyor',
            body: 'Mimari + EFT + ihtar',
            time: 'Şimdi',
          ),
          MockNotification(
            title: 'Banka eşleşmesi',
            body: '4 işlem eşleşmedi',
            time: '1 saat önce',
          ),
          MockNotification(
            title: 'Geciken WO',
            body: 'WO-1042 SLA aşıldı',
            time: 'Bugün',
            unread: false,
          ),
        ];
      case PmsPersona.resident:
        return const [
          MockNotification(
            title: 'Aidat hatırlatma',
            body: 'Mart aidatı ₺4.250 — gecikmiş',
            time: 'Şimdi',
          ),
          MockNotification(
            title: 'Kargo geldi',
            body: 'Yurtiçi — dolap A3',
            time: 'Bugün',
          ),
          MockNotification(
            title: 'Duyuru',
            body: 'Genel kurul 28 Mart',
            time: 'Dün',
            unread: false,
          ),
        ];
      case PmsPersona.field:
        return const [
          MockNotification(
            title: 'Yeni iş emri',
            body: 'WO-1038 atandı',
            time: 'Şimdi',
          ),
          MockNotification(
            title: 'Tur kontrol',
            body: '2 checkpoint kaldı',
            time: '30 dk önce',
          ),
        ];
    }
  }

  void refreshAts() {
    final n = DateTime.now().millisecondsSinceEpoch % 10000000000;
    atsCode = n.toString().padLeft(10, '0');
  }
}

enum PmsPersona { manager, resident, field }

extension PmsPersonaX on PmsPersona {
  String get label {
    switch (this) {
      case PmsPersona.manager:
        return 'Yönetici';
      case PmsPersona.resident:
        return 'Sakin / Malik';
      case PmsPersona.field:
        return 'Saha';
    }
  }

  IconData get icon {
    switch (this) {
      case PmsPersona.manager:
        return Icons.apartment_rounded;
      case PmsPersona.resident:
        return Icons.home_work_rounded;
      case PmsPersona.field:
        return Icons.engineering_rounded;
    }
  }
}

PmsPersona personaFromEmail(String email) {
  final e = email.trim().toLowerCase();
  if (e == 'owner@demo.ayanlab') return PmsPersona.resident;
  if (e == 'field@demo.ayanlab') return PmsPersona.field;
  return PmsPersona.manager;
}

enum DueStatus { paid, overdue, upcoming }

class MockDue {
  const MockDue({
    required this.month,
    required this.amountTry,
    required this.status,
    required this.breakdown,
  });

  final String month;
  final int amountTry;
  final DueStatus status;
  final String breakdown;
}

enum WorkOrderStatus { open, scheduled, inProgress, done }

class MockWorkOrder {
  const MockWorkOrder({
    required this.id,
    required this.title,
    required this.property,
    required this.status,
    required this.priority,
    required this.assignee,
  });

  final String id;
  final String title;
  final String property;
  final WorkOrderStatus status;
  final String priority;
  final String assignee;
}

class MockViolation {
  const MockViolation({
    required this.id,
    required this.unit,
    required this.rule,
    required this.fineTry,
    required this.status,
  });

  final String id;
  final String unit;
  final String rule;
  final int fineTry;
  final String status;
}

class MockApproval {
  const MockApproval({
    required this.id,
    required this.type,
    required this.unit,
    required this.summary,
    required this.submitted,
  });

  final String id;
  final String type;
  final String unit;
  final String summary;
  final String submitted;
}

enum CheckpointStatus { pending, done }

class MockPatrolCheckpoint {
  const MockPatrolCheckpoint({
    required this.name,
    required this.status,
    required this.note,
  });

  final String name;
  final CheckpointStatus status;
  final String note;
}

class MockParcel {
  const MockParcel({
    required this.carrier,
    required this.tracking,
    required this.locker,
    required this.arrived,
  });

  final String carrier;
  final String tracking;
  final String locker;
  final String arrived;
}

class MockAnnouncement {
  const MockAnnouncement({
    required this.title,
    required this.body,
    required this.time,
  });

  final String title;
  final String body;
  final String time;
}

class MockNotification {
  const MockNotification({
    required this.title,
    required this.body,
    required this.time,
    this.unread = true,
  });

  final String title;
  final String body;
  final String time;
  final bool unread;
}

class PmsModule {
  const PmsModule({
    required this.title,
    required this.subtitle,
    required this.icon,
    required this.route,
    required this.personas,
  });

  final String title;
  final String subtitle;
  final IconData icon;
  final String route;
  final Set<PmsPersona> personas;
}

class MockUser {
  const MockUser({
    required this.name,
    required this.email,
    required this.phone,
    required this.memberSince,
  });

  final String name;
  final String email;
  final String phone;
  final String memberSince;
}

const mockUser = MockUser(
  name: 'Demo PM',
  email: 'pm@demo.ayanlab',
  phone: '+90 555 000 00 00',
  memberSince: '2026-01-01',
);

const demoLogins = <({String email, String password, String label})>[
  (email: 'pm@demo.ayanlab', password: 'password', label: 'Yönetici (PM)'),
  (email: 'owner@demo.ayanlab', password: 'password', label: 'Sakin / Malik'),
  (email: 'field@demo.ayanlab', password: 'password', label: 'Saha ekibi'),
];

final pmsModules = <PmsModule>[
  PmsModule(
    title: 'Onaylar',
    subtitle: 'Mimari, EFT, ihtar',
    icon: Icons.fact_check_rounded,
    route: '/pms/approvals',
    personas: {PmsPersona.manager},
  ),
  PmsModule(
    title: 'Aidat & borç',
    subtitle: 'Ödeme geçmişi',
    icon: Icons.payments_rounded,
    route: '/pms/dues',
    personas: {PmsPersona.resident, PmsPersona.manager},
  ),
  PmsModule(
    title: 'Bakım / iş emri',
    subtitle: 'Talepler ve SLA',
    icon: Icons.handyman_rounded,
    route: '/pms/maintenance',
    personas: PmsPersona.values.toSet(),
  ),
  PmsModule(
    title: 'İhtarlar',
    subtitle: 'Kural ihlalleri',
    icon: Icons.gavel_rounded,
    route: '/pms/violations',
    personas: {PmsPersona.manager, PmsPersona.resident},
  ),
  PmsModule(
    title: 'Tur kontrol',
    subtitle: 'Checkpoint rotası',
    icon: Icons.route_rounded,
    route: '/pms/patrol',
    personas: {PmsPersona.field, PmsPersona.manager},
  ),
  PmsModule(
    title: 'Kargo',
    subtitle: 'Bekleyen paketler',
    icon: Icons.local_shipping_rounded,
    route: '/pms/parcels',
    personas: {PmsPersona.resident},
  ),
  PmsModule(
    title: 'ATS borç kodu',
    subtitle: '10 haneli sorgu',
    icon: Icons.pin_rounded,
    route: '/pms/ats',
    personas: {PmsPersona.resident},
  ),
  PmsModule(
    title: 'Duyurular',
    subtitle: 'Site haberleri',
    icon: Icons.campaign_rounded,
    route: '/pms/announcements',
    personas: PmsPersona.values.toSet(),
  ),
];