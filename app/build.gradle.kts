plugins { id("com.android.application") }
android {
 namespace = "com.morningbrief.alarm"
 compileSdk = 35
 defaultConfig { applicationId = "com.morningbrief.alarm"; minSdk = 26; targetSdk = 32; versionCode = 1; versionName = "0.1" }
 compileOptions { sourceCompatibility = JavaVersion.VERSION_17; targetCompatibility = JavaVersion.VERSION_17 }
}