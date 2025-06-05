import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig
import java.util.Properties

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.sqldelight)
}

kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }
    
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }
    
    // Temporarily disabled WasmJS target due to SQLDelight compatibility issues
    // @OptIn(ExperimentalWasmDsl::class)
    // wasmJs {
    //     moduleName = "composeApp"
    //     browser {
    //         val rootDirPath = project.rootDir.path
    //         val projectDirPath = project.projectDir.path
    //         commonWebpackConfig {
    //             outputFileName = "composeApp.js"
    //             devServer = (devServer ?: KotlinWebpackConfig.DevServer()).apply {
    //                 static = (static ?: mutableListOf()).apply {
    //                     // Serve sources to debug inside browser
    //                     add(rootDirPath)
    //                     add(projectDirPath)
    //                 }
    //             }
    //         }
    //     }
    //     binaries.executable()
    // }
    
    sourceSets {
        // Temporarily disabled WasmJS target due to SQLDelight compatibility issues
        // wasmJsMain.dependencies {
        //     // For WasmJS, we'll need to implement a mock version of the repository
        //     // that uses a different storage mechanism or API calls instead of SQLDelight
        // }
        
        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.koin.test)
        }
        
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtime.compose)
            implementation(libs.kotlinx.datetime)
            implementation(libs.kotlinx.serialization.json)
            
            // SQLDelight
            implementation(libs.sqldelight.runtime)
            implementation(libs.sqldelight.coroutines)
            
            // Koin DI
            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
            
            // Compose Navigation from JetBrains - correct implementation
            implementation(libs.androidx.navigation.compose)
            
            // Date Time Picker
            implementation(libs.kmp.date.time.picker)
            
            // Supabase
            implementation(libs.supabase.auth)
            implementation(libs.supabase.auth.compose)
            implementation(libs.ktor.client.core)
            
            // Logging
            implementation(libs.napier)
        }
        
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.sqldelight.android)
            implementation(libs.ktor.client.okhttp)
            implementation(libs.koin.android)
        }
        
        iosMain.dependencies {
            implementation(libs.sqldelight.native)
            implementation(libs.ktor.client.darwin)
        }
    }
}

android {
    namespace = "com.jkhanh.globaltrip"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.jkhanh.globaltrip"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
        
        // Read Supabase credentials from local.properties
        val localProperties = Properties().apply {
            val localPropertiesFile = rootProject.file("local.properties")
            if (localPropertiesFile.exists()) {
                localPropertiesFile.inputStream().use { load(it) }
            }
        }
        
        buildConfigField("String", "SUPABASE_URL", "\"${localProperties.getProperty("SUPABASE_URL", "")}\"")
        buildConfigField("String", "SUPABASE_ANON_KEY", "\"${localProperties.getProperty("SUPABASE_ANON_KEY", "")}\"")
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    buildFeatures {
        buildConfig = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

// Configure SQLDelight
sqldelight {
    databases {
        create("GlobalTripDatabase") {
            packageName.set("com.jkhanh.globaltrip.core.database")
        }
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
}

