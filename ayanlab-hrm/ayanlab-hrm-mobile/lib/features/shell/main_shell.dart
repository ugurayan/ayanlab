import 'package:flutter/material.dart';

import '../profile/profile_page.dart';
import '../staffing/hours_page.dart';
import '../staffing/jobs_page.dart';
import '../staffing/schedule_page.dart';
import '../staffing/staffing_theme.dart';

/// Employer shell: schedule, jobs, hours, and profile.
class MainShell extends StatefulWidget {
  const MainShell({super.key});

  @override
  State<MainShell> createState() => _MainShellState();
}

class _MainShellState extends State<MainShell> {
  int _index = 0;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: S.bg(context),
      body: IndexedStack(
        index: _index,
        children: const [
          SchedulePage(),
          JobsPage(),
          HoursPage(),
          ProfilePage(),
        ],
      ),
      bottomNavigationBar: DecoratedBox(
        decoration: BoxDecoration(
          color: S.bg(context),
          border: Border(top: BorderSide(color: S.line(context))),
        ),
        child: NavigationBar(
          selectedIndex: _index,
          labelBehavior: NavigationDestinationLabelBehavior.alwaysShow,
          destinations: const [
            NavigationDestination(
              icon: Icon(Icons.calendar_today_outlined),
              selectedIcon: Icon(Icons.calendar_today),
              label: 'Schedule',
            ),
            NavigationDestination(
              icon: Icon(Icons.work_outline),
              selectedIcon: Icon(Icons.work),
              label: 'Jobs',
            ),
            NavigationDestination(
              icon: Icon(Icons.schedule_outlined),
              selectedIcon: Icon(Icons.schedule),
              label: 'Hours',
            ),
            NavigationDestination(
              icon: Icon(Icons.person_outline),
              selectedIcon: Icon(Icons.person),
              label: 'Profile',
            ),
          ],
          onDestinationSelected: (index) => setState(() => _index = index),
        ),
      ),
    );
  }
}