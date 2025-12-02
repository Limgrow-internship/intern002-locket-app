import java.util.Properties

val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localProperties.load(localPropertiesFile.inputStream())
}

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.navigation.safeargs)
    alias(libs.plugins.ktlint)
    alias(libs.plugins.google.services)
    id("com.google.devtools.ksp")
}

fun getLocalProperty(key: String, project: Project): String? {
    val properties = Properties()
    val localPropertiesFile = project.rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        properties.load(localPropertiesFile.inputStream())
    }
    return properties.getProperty(key)
}

android {
    namespace = "com.intern002.locketapp"
    compileSdk = 34

    buildFeatures {
        buildConfig = true
        viewBinding = true
        dataBinding = true
    }

    defaultConfig {
        applicationId = "com.intern002.locketapp"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.2"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        val cloudName = localProperties.getProperty("CLOUDINARY_CLOUD_NAME") ?: ""
        val apiKey = localProperties.getProperty("CLOUDINARY_API_KEY") ?: ""
        val apiSecret = localProperties.getProperty("CLOUDINARY_API_SECRET") ?: ""
        val cloudinaryUrl = localProperties.getProperty("CLOUDINARY_URL") ?: ""

        buildConfigField("String", "CLOUDINARY_URL", "\"$cloudinaryUrl\"")
        buildConfigField("String", "CLOUD_NAME", "\"$cloudName\"")
        buildConfigField("String", "API_KEY", "\"$apiKey\"")
        buildConfigField("String", "API_SECRET", "\"$apiSecret\"")
    }

    buildTypes {
        debug {
            val baseUrl = getLocalProperty("base.url", project) ?: "http://10.0.2.2:8080"
            buildConfigField("String", "BASE_URL", "\"$baseUrl\"")
            val supabaseUrl = getLocalProperty("supabase.url", project) ?: ""
            buildConfigField("String", "SUPABASE_URL", "\"$supabaseUrl\"")
            val supabaseKey = getLocalProperty("supabase.key", project) ?: ""
            buildConfigField("String", "SUPABASE_KEY", "\"$supabaseKey\"")
        }
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
            buildConfigField("String", "BASE_URL", "\"https://your.production.server.com/\"")
            val supabaseUrl = getLocalProperty("supabase.url", project) ?: ""
            buildConfigField("String", "SUPABASE_URL", "\"$supabaseUrl\"")
            val supabaseKey = getLocalProperty("supabase.key", project) ?: ""
            buildConfigField("String", "SUPABASE_KEY", "\"$supabaseKey\"")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation("androidx.activity:activity-ktx:1.8.2")
    implementation("androidx.fragment:fragment-ktx:1.6.2")

    // DataStore
    implementation("androidx.datastore:datastore-preferences:1.1.1")

    // Lifecycle & Navigation
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.navigation.fragment.ktx)
    implementation(libs.navigation.ui.ktx)

    // Ktor Client
    implementation("io.ktor:ktor-client-android:2.3.10")
    implementation("io.ktor:ktor-client-core:2.3.10")
    implementation("io.ktor:ktor-client-okhttp:2.3.10")
    implementation("io.ktor:ktor-client-content-negotiation:2.3.10")
    implementation("io.ktor:ktor-client-logging:2.3.10")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.10")
    implementation("io.ktor:ktor-client-auth:2.3.10")
    implementation("io.ktor:ktor-serialization-gson:2.3.10")

    // Supabase
    implementation(platform(libs.supabase.bom))
    implementation(libs.supabase.realtime)
    implementation(libs.supabase.postgrest)

    // Network
    implementation(libs.serialization.json)
    implementation(libs.retrofit)
    implementation(libs.retrofit.serialization)
    implementation(libs.okhttp.logging)

    // Room
    implementation(libs.room.runtime)
    ksp(libs.room.compiler)
    implementation(libs.room.ktx)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // Firebase & Google
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    implementation(libs.play.services.auth)

    // Ads
    implementation(libs.play.services.ads)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.messaging)
    implementation(libs.firebase.storage)

    // Coroutines
    implementation(libs.coroutines.core)
    implementation(libs.coroutines.android)

    // Circle ImageView
    implementation("de.hdodenhof:circleimageview:3.1.0")

    // Tests
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Glide
    implementation("com.github.bumptech.glide:glide:4.16.0")
    ksp("com.github.bumptech.glide:compiler:4.16.0")

    // Converter
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")

    implementation(libs.billing)

    implementation("com.google.guava:guava:31.1-android")

    //Camera
    implementation("androidx.camera:camera-video:1.3.3")
    implementation("androidx.concurrent:concurrent-futures:1.1.0")
    implementation("androidx.camera:camera-core:1.3.3")
    implementation("androidx.camera:camera-camera2:1.3.3")
    implementation("androidx.camera:camera-lifecycle:1.3.3")
    implementation("androidx.camera:camera-view:1.3.3")

    //Cloudinary
    implementation("com.cloudinary:cloudinary-android:2.3.1")

    //Gson
    implementation("com.google.code.gson:gson:2.10.1")
}

ktlint {
    android.set(true)
    ignoreFailures.set(false)
}

apply(plugin = "com.google.gms.google-services")
