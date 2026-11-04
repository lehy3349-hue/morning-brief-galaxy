plugins { id("com.android.application") }
android {
 namespace = "com.morningbrief.alarm"
 compileSdk = 35
 defaultConfig { applicationId = "com.morningbrief.alarm"; minSdk = 26; targetSdk = 35; versionCode = 3; versionName = "0.3" }
 compileOptions { sourceCompatibility = JavaVersion.VERSION_17; targetCompatibility = JavaVersion.VERSION_17 }
}