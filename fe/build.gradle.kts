import java.util.Properties
import java.io.FileInputStream

val envFile = rootProject.file(".env")
val properties = Properties()
if (envFile.exists()) {
    properties.load(FileInputStream(envFile))
}

val geminiApiKey = properties.getProperty("GEMINI_API_KEY")
extra.set("geminiApiKey", geminiApiKey)

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    id("com.google.gms.google-services") version "4.4.3" apply false
}