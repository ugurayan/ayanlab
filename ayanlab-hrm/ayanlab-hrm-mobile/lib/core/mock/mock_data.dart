import 'package:flutter/material.dart';

/// Simple mock data used across the template screens.
/// No API connection — replace with real repositories later.
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

class MockItem {
  const MockItem({required this.title, required this.subtitle, this.icon});

  final String title;
  final String subtitle;
  final IconData? icon;
}

const mockUser = MockUser(
  name: 'Alex Demo',
  email: 'alex.demo@example.com',
  phone: '+90 555 000 00 00',
  memberSince: '2026-01-01',
);

const mockNotifications = <MockNotification>[
  MockNotification(
    title: 'New application',
    body: 'Lena Meier applied for Evening bar.',
    time: '09:40',
  ),
  MockNotification(
    title: 'Hours to approve',
    body: 'Two shifts from yesterday are waiting.',
    time: 'Yesterday',
  ),
  MockNotification(
    title: 'Shift filled',
    body: 'Breakfast service has both people hired.',
    time: '2 days ago',
    unread: false,
  ),
];

const mockRecentItems = <MockItem>[
  MockItem(title: 'Item One', subtitle: 'Mock subtitle', icon: Icons.article),
  MockItem(title: 'Item Two', subtitle: 'Mock subtitle', icon: Icons.folder),
  MockItem(title: 'Item Three', subtitle: 'Mock subtitle', icon: Icons.chat),
  MockItem(title: 'Item Four', subtitle: 'Mock subtitle', icon: Icons.event),
];
