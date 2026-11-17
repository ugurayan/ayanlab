#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"

write_pom() {
  local dir="$1"
  local artifact="$2"
  local main_class="$3"
  local extra_deps="${4:-}"

  cat > "${dir}/pom.xml" <<XML
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.3.5</version>
        <relativePath/>
    </parent>

    <groupId>com.ayanlabs</groupId>
    <artifactId>${artifact}</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <name>${artifact}</name>

    <properties>
        <java.version>21</java.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>com.ayanlabs</groupId>
            <artifactId>ayanlab-platform-core</artifactId>
            <version>1.0.0-SNAPSHOT</version>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springdoc</groupId>
            <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
            <version>2.6.0</version>
        </dependency>
        <dependency>
            <groupId>org.postgresql</groupId>
            <artifactId>postgresql</artifactId>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
${extra_deps}
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <configuration>
                    <mainClass>${main_class}</mainClass>
                </configuration>
            </plugin>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <configuration>
                    <parameters>true</parameters>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
XML
}

write_run_local() {
  local product_dir="$1"
  local backend_dir="$2"
  cat > "${product_dir}/run-local.sh" <<SH
#!/usr/bin/env bash
set -euo pipefail
HERE="\$(cd "\$(dirname "\$0")" && pwd)"
ROOT="\$(cd "\$HERE/.." && pwd)"
export ENV="\${ENV:-local}"

# shellcheck source=scripts/lib/resolve-env-file.sh
source "\$ROOT/scripts/lib/resolve-env-file.sh"
# shellcheck source=scripts/lib/mvn-install-libs.sh
source "\$ROOT/scripts/lib/mvn-install-libs.sh"

mvn_install_libs "\$ROOT"
cd "\$HERE/${backend_dir}"
load_env_file

if [[ -z "\${POSTGIS_URL:-}" && "\${POSTGIS_HOST:-}" == "postgres" ]]; then
  export POSTGIS_HOST=localhost
fi

echo "Running Maven spring-boot:run"
echo "  env file: \${ENV_FILE}"
echo "  database: \${POSTGIS_HOST:-?}/\${POSTGIS_DB:-?} schema=\${POSTGIS_SCHEMA:-?}"
exec mvn -s "$ROOT/.mvn/settings-central.xml" spring-boot:run "\$@"
SH
  chmod +x "${product_dir}/run-local.sh"
}

H2_DEP='        <dependency>
            <groupId>com.h2database</groupId>
            <artifactId>h2</artifactId>
            <scope>runtime</scope>
        </dependency>'

products=(
  "ayanlab-platform|ayanlab-platform-backend|com.ayanlabs.auth.AuthBackendApplication"
  "ayanlab-vault|ayanlab-vault-backend|com.ayanlabs.vault.VaultBackendApplication"
  "ayanlab-pms|ayanlab-pms-backend|com.ayanlabs.propertymgmt.PmsBackendApplication"
  "ayanlab-crm|ayanlab-crm-backend|com.ayanlabs.accounting.CrmBackendApplication"
  "ayanlab-hrm|ayanlab-hrm-backend|com.ayanlabs.rideshare.HrmBackendApplication"
  "ayanlab-booking|ayanlab-booking-backend|com.ayanlabs.booking.BookingBackendApplication"
  "ayanlab-comm|ayanlab-comm-backend|com.ayanlabs.comm.CommBackendApplication"
)

for row in "${products[@]}"; do
  IFS='|' read -r product artifact main <<< "$row"
  write_pom "${ROOT}/${product}/${artifact}" "$artifact" "$main" ""
  write_run_local "${ROOT}/${product}" "$artifact"
done

write_pom "${ROOT}/ayanlab-platform/ayanlab-platform-backend" "ayanlab-platform-backend" "com.ayanlabs.platform.PlatformApplication" "$H2_DEP"
write_run_local "${ROOT}/ayanlab-core" "ayanlab-platform-backend"

echo "Maven poms + run-local.sh generated"