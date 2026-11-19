/// API mode (M1.4) — default mock; pass `--dart-define=USE_PMS_API=true` with backend running.
const bool kUsePmsApi = bool.fromEnvironment('USE_PMS_API', defaultValue: false);

const String kPmsApiBase =
    String.fromEnvironment('PMS_API_BASE', defaultValue: 'http://localhost:8105/pms/api');

const String kAuthApiBase =
    String.fromEnvironment('AUTH_API_BASE', defaultValue: 'http://localhost:8102/auth/api');