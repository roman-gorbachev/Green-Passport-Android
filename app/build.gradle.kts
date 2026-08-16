plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt.android)
}

android {
    namespace = "com.smartcity.greenpassport"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "com.smartcity.greenpassport"
        minSdk = 26
        targetSdk = 36
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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(project(":core:model"))
    implementation(project(":core:common"))
    implementation(project(":core:designsystem"))
    implementation(project(":core:navigation"))
    implementation(project(":core:network"))
    implementation(project(":core:database"))
    implementation(project(":core:storage"))
    implementation(project(":core:auth"))
    implementation(project(":core:messaging"))
    implementation(project(":core:datastore"))

    implementation(project(":feature:auth:domain"))
    implementation(project(":feature:auth:presentation"))
    implementation(project(":feature:profile:domain"))
    implementation(project(":feature:profile:presentation"))
    implementation(project(":feature:tasks:domain"))
    implementation(project(":feature:tasks:presentation"))
    implementation(project(":feature:shop:domain"))
    implementation(project(":feature:shop:presentation"))
    implementation(project(":feature:calendar:domain"))
    implementation(project(":feature:calendar:presentation"))
    implementation(project(":feature:map:domain"))
    implementation(project(":feature:map:presentation"))
    implementation(project(":feature:community:domain"))
    implementation(project(":feature:community:presentation"))
    implementation(project(":feature:ecotips:domain"))
    implementation(project(":feature:ecotips:presentation"))
    implementation(project(":feature:feedback:domain"))
    implementation(project(":feature:feedback:presentation"))
    implementation(project(":feature:games:domain"))
    implementation(project(":feature:games:presentation"))

    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}
