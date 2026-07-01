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
