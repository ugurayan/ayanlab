#!/usr/bin/env bash
set -euo pipefail
TARGET="$1"
SERVICE_NAME="$2"

mkdir -p "${TARGET}"

cat > "${TARGET}/package.json" <<EOF
{
  "name": "${SERVICE_NAME}",
  "version": "0.1.0",
  "private": true,
  "main": "expo-router/entry",
  "scripts": {
    "start": "expo start",
    "android": "expo start --android",
    "ios": "expo start --ios",
    "web": "expo start --web"
  },
  "dependencies": {
    "expo": "~52.0.0",
    "expo-status-bar": "~2.0.0",
    "react": "18.3.1",
    "react-native": "0.76.0"
  },
  "devDependencies": {
    "@babel/core": "^7.25.0",
    "@types/react": "~18.3.0",
    "typescript": "~5.3.0"
  }
}
EOF

cat > "${TARGET}/app.json" <<EOF
{
  "expo": {
    "name": "${SERVICE_NAME}",
    "slug": "${SERVICE_NAME}",
    "version": "0.1.0",
    "orientation": "portrait",
    "scheme": "${SERVICE_NAME}",
    "userInterfaceStyle": "automatic",
    "ios": { "supportsTablet": true },
    "android": { "adaptiveIcon": { "backgroundColor": "#0f1419" } }
  }
}
EOF

cat > "${TARGET}/App.tsx" <<EOF
import { StatusBar } from "expo-status-bar";
import { StyleSheet, Text, View } from "react-native";

export default function App() {
  return (
    <View style={styles.container}>
      <Text style={styles.title}>${SERVICE_NAME}</Text>
      <Text style={styles.sub}>Expo / React Native shell</Text>
      <StatusBar style="light" />
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: "#0f1419", alignItems: "center", justifyContent: "center" },
  title: { fontSize: 22, color: "#e7ecf1", fontWeight: "600" },
  sub: { marginTop: 8, color: "#8b9aab" }
});
EOF

cat > "${TARGET}/tsconfig.json" <<'EOF'
{
  "extends": "expo/tsconfig.base",
  "compilerOptions": { "strict": true }
}
EOF

cat > "${TARGET}/README.md" <<'EOF'
# Mobile (Expo)

Run locally: `npm install && npm start`.

Not containerized for store builds; CI can use EAS Build later.
EOF

cat > "${TARGET}/.dockerignore" <<'EOF'
node_modules
.expo
EOF

# Optional dev container for Expo web preview
cat > "${TARGET}/Dockerfile" <<'EOF'
# Dev/preview only — use `expo start` on host for device builds
FROM node:22-alpine
WORKDIR /app
COPY package.json ./
RUN npm install
COPY . .
EXPOSE 19006
CMD ["npx", "expo", "start", "--web", "--non-interactive"]
EOF