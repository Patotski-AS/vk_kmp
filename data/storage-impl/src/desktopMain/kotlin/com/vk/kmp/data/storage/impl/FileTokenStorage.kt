package com.vk.kmp.data.storage.impl

import com.vk.kmp.auth.api.VkTokens
import com.vk.kmp.data.storage.api.TokenStorage
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import kotlin.io.path.exists
import kotlin.io.path.readText

private val json = Json {
    ignoreUnknownKeys = true
    encodeDefaults = true
}

internal class FileTokenStorage : TokenStorage {
    private val file: Path = Path.of(
        System.getProperty("user.home"),
        ".vk_kmp",
        "tokens.json",
    )

    override fun getTokens(): VkTokens? {
        if (!file.exists()) return null
        return runCatching {
            json.decodeFromString<StoredTokens>(file.readText()).toVkTokens()
        }.getOrNull()
    }

    override fun saveTokens(tokens: VkTokens) {
        Files.createDirectories(file.parent)
        Files.writeString(
            file,
            json.encodeToString(StoredTokens.from(tokens)),
            StandardOpenOption.CREATE,
            StandardOpenOption.TRUNCATE_EXISTING,
            StandardOpenOption.WRITE,
        )
    }

    override fun clear() {
        if (file.exists()) {
            Files.deleteIfExists(file)
        }
    }
}
