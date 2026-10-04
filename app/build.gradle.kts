import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
}

// Keep real project credentials out of version control. Environment variables
// take precedence so CI can supply its own configuration without editing source.
val localSecrets = Properties().apply {
    val secretsFile = rootProject.file("secrets.properties")
    if (secretsFile.isFile) {
        secretsFile.inputStream().use { load(it) }
    }
}
val mapsApiKey = providers.environmentVariable("MAPS_API_KEY").orNull
    ?: localSecrets.getProperty("MAPS_API_KEY", "")
val googleWebClientId = providers.environmentVariable("GOOGLE_WEB_CLIENT_ID").orNull
    ?: localSecrets.getProperty("GOOGLE_WEB_CLIENT_ID", "")

android {
    namespace = "com.example.myapplication6"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.myapplication6"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        manifestPlaceholders["MAPS_API_KEY"] = mapsApiKey
        resValue("string", "server_client_id", googleWebClientId)
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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.play.services.maps)
    implementation(libs.play.services.location)
    implementation(libs.annotation)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.firebase.database)
    implementation(libs.play.services.identity.credentials)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation("com.google.android.gms:play-services-auth:20.7.0")
    implementation("com.open-meteo:sdk:1.10.0")
    implementation("com.squareup.okhttp3:okhttp:4.9.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation(platform("com.google.firebase:firebase-bom:33.4.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.maps.android:android-maps-utils:2.2.0")

    implementation("com.google.firebase:firebase-ai")
    implementation("com.google.guava:guava:32.1.3-android")
    implementation(platform("com.google.firebase:firebase-bom:34.3.0"))
}
