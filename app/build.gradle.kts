plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
    id("com.chaquo.python")
}

android {
    namespace = "com.example.test"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.test"
        minSdk = 28
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        ndk {
            abiFilters += listOf("arm64-v8a", "x86_64")
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        mlModelBinding = true
    }
}

chaquopy {
    defaultConfig {
        version = "3.11"
        buildPython("C:/Users/XPS 13/AppData/Local/Programs/Python/Python311/python.exe")
    }

    sourceSets {
        getByName("main") {
            srcDir("src/main/python")
        }
    }
}

dependencies {
    implementation("org.tensorflow:tensorflow-lite:2.12.0")
    implementation("org.tensorflow:tensorflow-lite-support:0.4.3")
    implementation("org.tensorflow:tensorflow-lite-gpu:2.12.0")
    implementation(platform("com.google.firebase:firebase-bom:33.0.0"))
    implementation("com.google.firebase:firebase-database")
    implementation(platform("com.google.firebase:firebase-bom:33.0.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("org.tensorflow:tensorflow-lite-metadata:0.1.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    implementation("androidx.camera:camera-core:1.3.1")
    implementation("androidx.camera:camera-camera2:1.3.1")
    implementation("androidx.camera:camera-lifecycle:1.3.1")
    implementation("androidx.camera:camera-view:1.3.1")
    // CameraX core library
    implementation ("androidx.camera:camera-core:1.2.2")
    implementation ("androidx.camera:camera-camera2:1.2.2")
    implementation ("androidx.camera:camera-lifecycle:1.2.2")
    implementation ("androidx.camera:camera-view:1.2.2")
    implementation ("androidx.camera:camera-extensions:1.2.2")
    implementation ("org.tensorflow:tensorflow-lite:2.13.0")// Check for the latest version
    implementation ("org.tensorflow:tensorflow-lite-select-tf-ops:2.13.0") // Check for the latest version

}
