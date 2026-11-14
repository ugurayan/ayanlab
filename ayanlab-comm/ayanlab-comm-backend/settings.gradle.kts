rootProject.name = "ayanlab-comm-backend"

include(":libs:ayanlab-core-domain")
project(":libs:ayanlab-core-domain").projectDir = file("../../libs/ayanlab-core-domain")

include(":libs:ayanlab-security")
project(":libs:ayanlab-security").projectDir = file("../../libs/ayanlab-security")
