package com.vk.kmp.buildlogic

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class AppConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        pluginManager.apply("vk.cmp.feature")

        extensions.configure<KotlinMultiplatformExtension> {
            configureVkTargets(target, exportIosFramework = true)
        }
    }
}
