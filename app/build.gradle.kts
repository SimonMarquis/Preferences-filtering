plugins {
    id("com.android.application")
    kotlin("android")
}

android {
    compileSdk = 31
    defaultConfig {
        applicationId = "fr.smarquis.preferences"
        minSdk = 21
        targetSdk = 31
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        debug {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"))
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    testOptions {
        animationsDisabled = true
        devices {
            create<com.android.build.api.dsl.ManagedVirtualDevice>("pixel2") {
                device = "Pixel 2"
                apiLevel = 30
                systemImageSource = "aosp-atd"
                abi = "x86"
            }
        }
    }
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.4.1")
    implementation("androidx.preference:preference-ktx:1.1.1")
    testImplementation("com.google.testparameterinjector:test-parameter-injector:1.7")
    testImplementation("io.mockk:mockk:1.12.2")
    testImplementation("junit:junit:4.13.2")
    testImplementation(kotlin("test"))
    androidTestImplementation("androidx.test:core-ktx:1.4.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
    androidTestImplementation("androidx.test.ext:junit-ktx:1.1.3")
}