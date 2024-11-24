import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.devtools.ksp)
    id(libs.plugins.google.services.get().pluginId)
    id(libs.plugins.google.crashlytics.get().pluginId)
    alias(libs.plugins.compose.compiler)
    id(libs.plugins.room.get().pluginId)
}

android {
    namespace = "com.pavellukyanov.themartian"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.pavellukyanov.themartian"
        minSdk = 26
        targetSdk = 35
        versionCode = 11202
        versionName = "1.1.2"

        extensions.getByType(BasePluginExtension::class.java).archivesName.set("${rootProject.name}-$versionName-($versionCode)")

        vectorDrawables {
            useSupportLibrary = true
        }

        val localProperties = Properties()
        localProperties.load(FileInputStream(rootProject.file("local.properties")))

        buildConfigField("String", "API_KEY", localProperties["apiKey"].toString())
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }

        debug {
            isDebuggable = true
            isJniDebuggable = true
            isMinifyEnabled = false
            isShrinkResources = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_19
        targetCompatibility = JavaVersion.VERSION_19
    }
    kotlinOptions {
        jvmTarget = "19"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
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
    //Core
    implementation(libs.androidx.core)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.foundation)
    implementation(libs.compose.icons)
    implementation(libs.compose.ui)
    implementation(libs.compose.util)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.androidx.activity.compose)
    debugImplementation(libs.compose.ui.tooling)
    implementation(libs.androidx.compose.lifecycle.runtime)
    implementation(libs.androidx.compose.lifecycle.service)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.compose.foundation.layout)
    implementation(libs.compose.theme.adapter)
    implementation(libs.androidx.activity.compose)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.androidx.navigation.compose)
    coreLibraryDesugaring(libs.android.tools.desugar)
    implementation(libs.constraint.layout)
    implementation(libs.androidx.core.splashscreen)

    //Accompanist
    implementation(libs.accompanist.systemuicontroller)

    //Koin
    implementation(libs.koin.core)
    implementation(libs.koin.android)
    implementation(libs.koin.android.compose)

    //Coil
    implementation(libs.coil)

    //Retrofit
    implementation(libs.retrofit)
    implementation(libs.converterGson)
    implementation(libs.interceptor)
    implementation(libs.converterScalars)

    //Room
    implementation(libs.androidx.room.runtime)
    annotationProcessor(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    //Timber
    implementation(libs.timber)

    //Google Services
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.analytics)

    //WorkManager
    implementation(libs.workmanager)
}