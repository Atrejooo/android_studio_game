plugins {
    alias(libs.plugins.android.application)
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    testImplementation(libs.junit.jupiter)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    implementation(libs.appcompat)
    implementation(libs.material)

    // JUnit 4 for unit tests
    // testImplementation("junit:junit:4.13.2")

    // JUnit 5 for advanced features (optional)
    // testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")

    // AndroidX Test for instrumentation tests
    // androidTestImplementation("androidx.test.ext:junit:1.1.5")
    // androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

}

tasks.withType<Test> {
    useJUnitPlatform() // Enables JUnit 5
}

android {
    namespace = "visual.activities"
    compileSdk = 34

    defaultConfig {
        applicationId = "visual.activities"
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    testOptions {
        unitTests {
            isIncludeAndroidResources = true // Include Android resources in tests
            all {
                // useJUnitPlatform() // Use JUnit 5 platform
            }
        }
    }
    namespace = "visual.activities"
    compileSdk = 34


}
