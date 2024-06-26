plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}
android {
    namespace = "com.ndm.da_test"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.ndm.da_test"
        minSdk = 24
        targetSdk = 33
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        viewBinding = true

    }
}

dependencies {

    implementation ("androidx.fragment:fragment:1.7.1")
    implementation ("androidx.activity:activity:1.9.0")

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.core:core:1.13.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.annotation:annotation:1.7.1")

    implementation(platform("com.google.firebase:firebase-bom:32.7.4"))

    //authentication
    implementation("com.google.firebase:firebase-auth:22.3.1")

    //realtime db
    implementation("com.google.firebase:firebase-database")
    implementation("com.firebaseui:firebase-ui-database:7.1.1")

    //storage
    implementation("com.google.firebase:firebase-storage:20.3.0")

    //load ảnh bằng url
    implementation("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.15.0")

    //Circle image
    implementation("de.hdodenhof:circleimageview:3.1.0")

    //map
    implementation("com.google.android.gms:play-services-maps:18.2.0")

    //location
    implementation("com.google.android.gms:play-services-location:21.2.0")

    //messaging
    implementation("com.google.firebase:firebase-messaging:23.4.1")

    //gson
    implementation("com.google.code.gson:gson:2.10.1")

    //retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("androidx.work:work-runtime:2.9.0")

    //Qr
    implementation("com.google.zxing:core:3.4.1")
    implementation("com.journeyapps:zxing-android-embedded:4.3.0")

    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")













}

