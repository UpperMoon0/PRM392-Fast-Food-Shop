import java.util.Properties
import java.io.FileInputStream

plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
}

// Đọc local.properties
val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localProperties.load(FileInputStream(localPropertiesFile))
}
val cloudinaryApiKey = localProperties.getProperty("CLOUDINARY_API_KEY") ?: ""
val cloudinaryApiSecret = localProperties.getProperty("CLOUDINARY_API_SECRET") ?: ""
val geminiApiKey: String by rootProject.extra

android {
    namespace = "com.nstut.fast_food_shop"
    compileSdk = 35
    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        buildConfigField("String", "CLOUDINARY_API_KEY", "\"$cloudinaryApiKey\"")
        buildConfigField("String", "GEMINI_API_KEY", "\"$geminiApiKey\"")
        buildConfigField("String", "CLOUDINARY_API_SECRET", "\"$cloudinaryApiSecret\"")
        applicationId = "com.nstut.fast_food_shop"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    buildFeatures {
        viewBinding = true
    }
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    coreLibraryDesugaring(libs.android.desugarJdkLibs)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation("com.cloudinary:cloudinary-android:2.4.0")
    implementation("com.github.bumptech.glide:glide:4.15.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.15.0")
    implementation("com.google.ai.client.generativeai:generativeai:0.3.0")
    implementation("com.google.guava:guava:32.0.1-android")


    //Room
    implementation("androidx.room:room-runtime:2.6.0")
    annotationProcessor("androidx.room:room-compiler:2.6.0")

    // Gson
    implementation("com.google.code.gson:gson:2.10.1")

    // BCrypt
    implementation("at.favre.lib:bcrypt:0.9.0")

   implementation(platform("com.google.firebase:firebase-bom:33.1.1"))
   implementation("com.google.firebase:firebase-auth")
   implementation("com.google.firebase:firebase-firestore")
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
}