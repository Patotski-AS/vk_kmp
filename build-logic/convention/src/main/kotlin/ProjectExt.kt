package com.vk.kmp.buildlogic

import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.getByType

internal val Project.libs: VersionCatalog
    get() = extensions.getByType<VersionCatalogsExtension>().named("libs")

internal fun Project.defaultNamespace(): String {
    val path = path
        .removePrefix(":")
        .split(':')
        .joinToString(".") { segment -> segment.replace('-', '.') }
    return "com.vk.kmp.$path"
}
