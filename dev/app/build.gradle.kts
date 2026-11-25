import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.aboutlibraries.android)
    id("com.google.dagger.hilt.android")
    kotlin("plugin.serialization") version "2.4.21"
}

// Read local.properties at build time
val localProperties = Properties().apply {
    val f = rootProject.file("local.properties")
    if (f.exists()) load(FileInputStream(f))
}

android {
    namespace = "com.sadaqahcode.muslimlauncher"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.sadaqahcode.muslimlauncher"
        minSdk = 24
        targetSdk = 37
        versionCode = 261125
        versionName = "0.4.3"

        val hmacKey = localProperties.getProperty("HMACK")
            ?: "" // empty → fail closed at runtime, don't crash the build
        buildConfigField("String", "HMACK", "\"$hmacKey\"") // for get nonce

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    signingConfigs {
        create("release") {
            storeFile = file(localProperties.getProperty("RELEASE_STORE_FILE") ?: "")
            storePassword = localProperties.getProperty("RELEASE_STORE_PASSWORD") ?: ""
            keyAlias = localProperties.getProperty("RELEASE_KEY_ALIAS") ?: ""
            keyPassword = localProperties.getProperty("RELEASE_KEY_PASSWORD") ?: ""
        }
    }

    buildTypes {
        release {
            buildConfigField("boolean", "DEBUG", "false")
            // Enable minification (code shrinking and obfuscation)
            isMinifyEnabled = true

            // Enable resource shrinking (removes unused drawables, layouts, strings)
            isShrinkResources = true

            // Keep optimization configs if needed, but R8 handles this automatically now
            optimization {
                // R8 optimization is enabled by default when isMinifyEnabled = true
            }

            // Attach ProGuard rules file
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

            signingConfig = signingConfigs.getByName("release")
        }
        debug {
            buildConfigField("boolean", "DEBUG", "true")

            // for fcm
            signingConfig = signingConfigs.getByName("release")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    configurations.all {
        resolutionStrategy {
            force("org.jetbrains.kotlin:kotlin-metadata-jvm:2.4.0")
        }
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // ── Compose ────────────────────────────────────────────────────────────
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)

    // ── Core / Lifecycle ───────────────────────────────────────────────────
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)

    // ── Navigation ─────────────────────────────────────────────────────────
    implementation(libs.androidx.navigation.compose)


    // --- Retrofit & JSON Converter ---
    implementation("com.squareup.retrofit2:retrofit:3.0.0")
    implementation("com.squareup.retrofit2:converter-gson:3.0.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    implementation("com.squareup.retrofit2:converter-kotlinx-serialization:3.0.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.13.0")

    // Hilt
    ksp("org.jetbrains.kotlin:kotlin-metadata-jvm:2.4.0")
    implementation(libs.hilt.lifecycle.viewmodel.compose)
    implementation("com.google.dagger:hilt-android:2.59.2")
    ksp("com.google.dagger:hilt-android-compiler:2.59.2")


    // ── Persistence ────────────────────────────────────────────────────────
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.ui)
    implementation(libs.material)
    ksp(libs.androidx.room.compiler)

    implementation(libs.androidx.datastore.preferences)

    // ── Background work ────────────────────────────────────────────────────
    implementation(libs.androidx.work.runtime.ktx)

    // ── Date / time ────────────────────────────────────────────────────────
    // kotlinx-datetime works on minSdk 23 without desugaring.
    implementation(libs.kotlinx.datetime)

    // ── Coroutines (explicit) ──────────────────────────────────────────────
    implementation(libs.kotlinx.coroutines.android)

    // scroller
    implementation("com.github.nanihadesuka:LazyColumnScrollbar:2.2.0")

    // logger
    implementation("com.flagodna:debugprint:0.1.1")

    // Coil
    implementation("io.coil-kt.coil3:coil-compose:3.5.0")
    implementation("io.coil-kt.coil3:coil-network-okhttp:3.5.0")

    // theme
    implementation("com.materialkolor:material-color-utilities:6.0.0")
    implementation("com.materialkolor:material-kolor-material3:6.0.0")

    // Guide UI
    implementation("io.github.tharukack:guidekit:1.2.0")

    // Library UI
    implementation(libs.aboutlibraries.compose.m3)

    // ── Tests ──────────────────────────────────────────────────────────────
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)

    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)

    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
}

aboutLibraries {
    collect {
        configPath = file("../config")
    }
}