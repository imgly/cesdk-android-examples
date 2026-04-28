plugins {
    id("com.android.application")
    id("kotlin-android")
}

// highlight-build-android
android {
    namespace = "com.example.cesdkapp"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.cesdkapp"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
        ndk {
            abiFilters += arrayOf("arm64-v8a", "armeabi-v7a", "x86_64", "x86")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.3"
    }
}
// highlight-build-android

// highlight-dependency
dependencies {
    // This dependency makes main compose and coroutine APIs available in your project
    implementation("ly.img:editor:1.74.0-rc.1")
    // Other dependencies here
}
// highlight-dependency
