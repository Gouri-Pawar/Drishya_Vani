plugins {
    alias(libs.plugins.android.application) apply false

    // Add Firebase Google Services plugin
    id("com.google.gms.google-services") version "4.4.4" apply false
}