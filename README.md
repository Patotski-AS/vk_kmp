# vk_kmp

Kotlin Multiplatform (KMP) пет-проект с VK API.

## Ветки

| Ветка | Назначение |
|-------|------------|
| `main` | Стабильная база |
| `dev` | Активная разработка |

## Документация

- [`plan.md`](plan.md) — архитектура, модули, этапы разработки
- [`.cursor/rules/`](.cursor/rules/) — правила для Cursor Agent

## Сборка

```bash
./gradlew :androidApp:assembleDebug
./gradlew :desktopApp:run
```

## Auth

### Настройка VK ID (Android / Desktop / iOS)

В `local.properties` в корне проекта:

```properties
vk.client.id=YOUR_APP_ID
vk.client.secret=YOUR_CLIENT_SECRET
```

| Платформа | Дополнительно |
|-----------|---------------|
| Android | redirect scheme `vk{APP_ID}`, host `vk.ru` |
| Desktop | redirect `http://127.0.0.1:<port>/callback` в кабинете VK ID; credentials также читаются из `~/.vk_kmp/credentials.properties` или env `VK_CLIENT_ID` / `VK_CLIENT_SECRET` |
| iOS | `iosApp/iosApp/Info.plist` → `VKClientId`, `VKClientSecret`; см. [`iosApp/README.md`](iosApp/README.md) |

Без credentials сборка и запуск работают через stub-авторизацию.

### Проверка

1. Login → «Войти через VK ID» → Main (лента)
2. Перезапуск приложения → сразу Main (токены сохранены)
3. «Выйти» на Main → снова Login

### Хранение токенов

| Платформа | Где |
|-----------|-----|
| Android | DataStore `vk_tokens` |
| Desktop | `~/.vk_kmp/tokens.json` |
| iOS | UserDefaults `vk_kmp_tokens` |

### Фаза 3 — iOS + Desktop auth, refresh ✅

- **Desktop:** OAuth 2.0 + PKCE через `OAuthClient`, браузер (`java.awt.Desktop.browse`)
- **iOS:** VK ID SDK через `iosApp` + Kotlin bridge (`Login_implKt.completeVkIdIosAuth*`)
- **Все платформы:** `TokenRefresher` + Ktor interceptor в `vk-impl` (401 / VK error 5 → `refreshAccessToken()` → retry → logout)
- **Android:** refresh через VK ID SDK (`VKID.refreshToken`), если в storage нет `refresh_token`/`device_id`

Следующий этап — **фаза 4** (лента `wall.get`).
