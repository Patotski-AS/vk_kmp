plugins {
    `kotlin-dsl`
}

group = "com.vk.kmp.buildlogic"

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.compose.gradlePlugin)
}

gradlePlugin {
    plugins {
        register("kmpLibrary") {
            id = "vk.kmp.library"
            implementationClass = "com.vk.kmp.buildlogic.KmpLibraryConventionPlugin"
        }
        register("kmpApi") {
            id = "vk.kmp.api"
            implementationClass = "com.vk.kmp.buildlogic.KmpApiConventionPlugin"
        }
        register("cmpLibrary") {
            id = "vk.cmp.library"
            implementationClass = "com.vk.kmp.buildlogic.CmpLibraryConventionPlugin"
        }
        register("cmpFeature") {
            id = "vk.cmp.feature"
            implementationClass = "com.vk.kmp.buildlogic.CmpFeatureConventionPlugin"
        }
        register("app") {
            id = "vk.app"
            implementationClass = "com.vk.kmp.buildlogic.AppConventionPlugin"
        }
    }
}
