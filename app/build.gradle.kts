plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.mad_smartfit_android_app"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.mad_smartfit_android_app"
        minSdk = 24
        targetSdk = 34
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
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.auth)       // Firebase Authentication
    implementation(libs.firebase.firestore)  // Firebase Firestore
    implementation(libs.firebase.database)   // Firebase Realtime Database (if needed)
    implementation(libs.firebase.storage)    // Firebase Storage (if needed)

    // Glide for image loading
    implementation(libs.glide)
    implementation(libs.play.services.location)
    annotationProcessor(libs.glide.compiler)

    // Cloudinary SDK
    implementation("com.cloudinary:cloudinary-android:2.3.1")
    implementation ("com.squareup.picasso:picasso:2.8")

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}