
plugins {
    id("com.gtnewhorizons.gtnhconvention")
}

dependencies {
    // Forge provides at runtime; needed for compile/IDE resolution of org.apache.logging.log4j
    compileOnly("org.apache.logging.log4j:log4j-api:2.0.2")
}
