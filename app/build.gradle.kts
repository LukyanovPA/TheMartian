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
    compileSdk = 37

    defaultConfig {
        applicationId = "com.pavellukyanov.themartian"
        minSdk = 26
        targetSdk = 37
        versionCode = 20000
        versionName = "2.0"

        extensions.getByType(BasePluginExtension::class.java).archivesName.set("${rootProject.name}-$versionName-($versionCode)")

        vectorDrawables {
            useSupportLibrary = true
        }

        val localProperties = Properties()
        val localPropertiesFile = rootProject.file("local.properties")
        if (localPropertiesFile.exists()) {
            localPropertiesFile.inputStream().use(localProperties::load)
        }

        val apiKey = localProperties.getProperty("apiKey").orEmpty().trim().trim('"')
        require(apiKey.isNotEmpty()) {
            "Missing 'apiKey' in local.properties. Add the MarsVista API key as: apiKey=<key>"
        }

        buildConfigField("String", "API_KEY", "\"$apiKey\"")

        val relayBaseUrl = localProperties.getProperty("relayBaseUrl").orEmpty().trim().trim('"')
        require(relayBaseUrl.isNotEmpty()) {
            "Missing 'relayBaseUrl' in local.properties. Add the relay endpoint as: " +
                "relayBaseUrl=https://<host>/api/v2/"
        }

        require(relayBaseUrl.endsWith("/")) {
            "relayBaseUrl must end with '/': relayBaseUrl=https://<host>/api/v2/"
        }

        val relayToken = localProperties.getProperty("relayToken").orEmpty().trim().trim('"')
        require(relayToken.isNotEmpty()) {
            "Missing 'relayToken' in local.properties. Add the relay gate token as: relayToken=<token>"
        }

        buildConfigField("String", "BASE_URL", "\"$relayBaseUrl\"")
        buildConfigField("String", "RELAY_TOKEN", "\"$relayToken\"")
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
    implementation(libs.coil.network.okhttp)

    //Retrofit
    implementation(libs.retrofit)
    implementation(libs.converterGson)
    implementation(libs.interceptor)
    implementation(libs.converterScalars)

    //Room
    implementation(libs.androidx.room.runtime)
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
