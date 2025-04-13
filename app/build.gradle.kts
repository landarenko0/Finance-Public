plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.hilt)
    alias(libs.plugins.room)
    alias(libs.plugins.ksp)
    alias(libs.plugins.serialization)
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "com.example.finance"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.finance"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    kotlinOptions {
        jvmTarget = "21"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.15"
    }
    composeCompiler {
        stabilityConfigurationFiles.add(rootProject.layout.projectDirectory.file("stability_config.conf"))
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    room {
        schemaDirectory("$projectDir/schemas")
    }
}

dependencies {

    implementation(libs.bundles.core)
    implementation(libs.bundles.lifecycle)
    implementation(libs.bundles.compose)
    implementation(libs.bundles.room)
    implementation(libs.bundles.hilt)
    implementation(libs.bundles.navigation)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.bundles.serialization)
    implementation(libs.bundles.dataStore)
    implementation(libs.bundles.workManager)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    debugImplementation(libs.bundles.compose.debug)
    ksp(libs.dagger.hilt.android.compiler)
    ksp(libs.hilt.androidx.compiler)
    ksp(libs.androidx.room.compiler)
}