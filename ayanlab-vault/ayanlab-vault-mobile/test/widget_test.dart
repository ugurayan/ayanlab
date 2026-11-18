import 'package:flutter_test/flutter_test.dart';
import 'package:ayanlab_vault_mobile/features/auth/onboarding_page.dart';
import 'package:ayanlab_vault_mobile/main.dart';
import 'package:provider/provider.dart';
import 'package:shared_preferences/shared_preferences.dart';

import 'package:ayanlab_vault_mobile/core/state/app_state.dart';

void main() {
  testWidgets('splash routes to onboarding on first run', (tester) async {
    SharedPreferences.setMockInitialValues(<String, Object>{});
    final prefs = await SharedPreferences.getInstance();

    await tester.pumpWidget(
      MultiProvider(
        providers: [
          ChangeNotifierProvider.value(value: SessionNotifier(prefs)),
          ChangeNotifierProvider.value(value: LocaleNotifier(prefs)),
          ChangeNotifierProvider.value(value: ThemeNotifier(prefs)),
        ],
        child: const AyanLabApp(),
      ),
    );

    // Fire the splash timer (1.2s) and let it route to onboarding.
    await tester.pumpAndSettle(const Duration(seconds: 2));

    expect(find.byType(OnboardingPage), findsOneWidget);
  });
}
