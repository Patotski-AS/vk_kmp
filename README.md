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

## Auth (фаза 2)

### Настройка VK ID (Android)

В `local.properties` в корне проекта:

```properties
vk.client.id=YOUR_APP_ID
vk.client.secret=YOUR_CLIENT_SECRET
```

В кабинете VK ID: redirect scheme `vk{APP_ID}`, host `vk.ru`.

Без credentials сборка и запуск работают через stub-авторизацию.

### Проверка

1. Login → кнопка «Войти через VK ID» → Main (лента)
2. Перезапуск приложения → сразу Main (токены сохранены)
3. «Выйти» на Main → снова Login

### Хранение токенов

| Платформа | Где |
|-----------|-----|
| Android | DataStore `vk_tokens` |
| Desktop | `~/.vk_kmp/tokens.json` |
| iOS | UserDefaults `vk_kmp_tokens` |

### Не входит в фазу 2 (фаза 3)

- iOS VK ID SDK, Desktop OAuth
- `TokenRefresher` + Ktor interceptor
- Шифрование токенов (secure storage)
