plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
}

android {
    viewBinding {
        enable = true
    }
    namespace = "com.example.goldenromance"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.goldenromance"
        minSdk = 24
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        vectorDrawables {
            useSupportLibrary = true
        }
    }

    //Nuevo


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
    kotlinOptions {
        jvmTarget = "1.8"
    }

}




dependencies {

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.12.0-alpha01")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.firebase:firebase-analytics:21.5.0")
    implementation(files("show-case-card-view"))
    implementation(files("libs/show-case-card-view.jar"))
    implementation(files("libs/BottomNavigationViewEx.jar"))
    implementation(files("libs/show-case-card-view.jar"))
    implementation("com.google.firebase:firebase-firestore:24.10.1")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")


    implementation ("com.google.firebase:firebase-auth:17.0.0")

    implementation ("com.google.firebase:firebase-storage:17.0.0")
    implementation ("com.google.firebase:firebase-messaging:19.0.0")


//nuevo
    implementation(platform("com.google.firebase:firebase-bom:32.7.1"))
    //antiguo
    implementation ("com.github.bumptech.glide:glide:4.12.0")
    implementation ("com.lorentzos.swipecards:library:1.0.9")
    implementation ("com.google.firebase:firebase-core:17.0.0")
    implementation ("com.google.android.material:material:1.1.0-alpha03")
    implementation ("com.google.firebase:firebase-database:18.0.0")


    //Circle ImageView
    implementation ("de.hdodenhof:circleimageview:2.2.0")


    implementation ("com.google.firebase:firebase-auth:latest_version")
    implementation ("com.google.firebase:firebase-core:latest_version")

    //Antes de esto funciona
    implementation ("com.android.support:support-annotations:22.2.0")

    //android.support.v4.app.Fragment;
    implementation("android.arch.navigation:navigation-fragment-ktx:1.0.0")
    implementation("com.google.android.material:material:1.0.0")


}







