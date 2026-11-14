import { StatusBar } from "expo-status-bar";
import { StyleSheet, Text, View } from "react-native";

export default function App() {
  return (
    <View style={styles.container}>
      <Text style={styles.title}>ayanlab-comm-mobile</Text>
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
