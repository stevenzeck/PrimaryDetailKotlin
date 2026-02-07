plugins {
    alias(libs.plugins.agp)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kover)
}

android {
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.example.primarydetailkotlin"
        minSdk = 23
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
        }
        getByName("debug") {
            isDebuggable = true
        }
    }

    // Need to include this for lambda
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        viewBinding = true
        resValues = true
    }

    namespace = "com.example.primarydetailkotlin"

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
}

tasks.withType<Test> {
    jvmArgs("-noverify")
}

kotlin {
    compilerOptions {
        languageVersion = org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_3
        allWarningsAsErrors = true
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation(libs.kotlin.stdlib)

    // UI & Core Libraries
    implementation(libs.bundles.ui)
    implementation(libs.bundles.navigation)

    // Lifecycle
    implementation(libs.lifecycle.viewmodel)
    ksp(libs.lifecycle.compiler)

    // Database & Network
    implementation(libs.bundles.room)
    ksp(libs.room.compiler)
    implementation(libs.bundles.retrofit)
    implementation(libs.kotlin.serialization.json)

    // Logic & DI
    runtimeOnly(libs.coroutines.android)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // Unit Tests
    testImplementation(libs.bundles.unit.test)
    kspTest(libs.hilt.compiler)

    // Android Instrumentation Tests
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.expresso.core)
}

kover {
    reports {
        filters {
            excludes {
                annotatedBy(
                    "dagger.internal.DaggerGenerated",
                    "dagger.hilt.codegen.OriginatingElement",
                    "javax.annotation.processing.Generated",
                )
                classes(
                    "**.BuildConfig",
                    "*ComposableSingletons*",
                    "dagger.hilt.internal.aggregatedroot.codegen.**",
                    "**.Dagger*",
                    "**.*_Factory*",
                    "**.Hilt_*",
                    "**.*_HiltModules*",
                    "hilt_aggregated_deps.**",
                    "**.*_Impl*",
                    "**.*_MembersInjector*",
                    "**.*_Provide*Factory*",
                )
            }
        }
    }
}
