import java.util.Properties
import java.io.FileInputStream

plugins {
    alias(libs.plugins.android.application)
    
}

// Đọc local.properties
val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localProperties.load(FileInputStream(localPropertiesFile))
}

android {
    namespace = "com.nstut.fast_food_shop"
    compileSdk = 35
    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
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
    implementation("androidx.recyclerview:recyclerview:1.2.1")
    implementation("androidx.cardview:cardview:1.0.0")
    
    
    implementation("com.android.volley:volley:1.2.1")
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation("com.github.bumptech.glide:glide:4.15.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.15.0")
    implementation("com.google.guava:guava:32.0.1-android")



    // Gson
    implementation("com.google.code.gson:gson:2.10.1")

    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // BCrypt
    implementation("at.favre.lib:bcrypt:0.9.0")

   
   
   
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")

    // Room for database
    implementation("androidx.room:room-runtime:2.6.1")
    annotationProcessor("androidx.room:room-compiler:2.6.1")

    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:33.1.1"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-auth")

    // Google AI
    implementation("com.google.ai.client.generativeai:generativeai:0.5.0")

    // OkHttp for multipart
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
}