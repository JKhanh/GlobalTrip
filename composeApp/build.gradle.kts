import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

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
        
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.sqldelight.android)
            implementation(libs.koin.android)
        }
        
        iosMain.dependencies {
            implementation(libs.sqldelight.native)
        }
        
        // Temporarily disabled WasmJS target due to SQLDelight compatibility issues
        // wasmJsMain.dependencies {
        //     // For WasmJS, we'll need to implement a mock version of the repository
        //     // that uses a different storage mechanism or API calls instead of SQLDelight
        // }
        
        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(libs.kotlinx.coroutines.test)
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
            
            // Koin
            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            
            // Compose Navigation from JetBrains - correct implementation
            implementation(libs.androidx.navigation.compose)
        }
        
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.sqldelight.android)
            implementation(libs.koin.android)
        }
        
        iosMain.dependencies {
            implementation(libs.sqldelight.native)
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

