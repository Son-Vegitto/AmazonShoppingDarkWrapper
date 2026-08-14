android {
    namespace = "com.amazon.shopping.dark"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.amazon.shopping.dark"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            isShrinkResources = false
            isDebuggable = false
        }
    }
}
