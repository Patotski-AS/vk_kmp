# VK Pet — план проекта

Пет-проект на Kotlin Multiplatform с VK API.

## Стек

| Слой | Технология |
|------|------------|
| UI | Compose Multiplatform |
| Навигация | Decompose |
| Состояние | MVIKotlin |
| Сеть | Ktor Client + kotlinx.serialization |
| Хранение | DataStore / SQLDelight + platform secure storage |
| DI | Koin |
| Платформы | Android, iOS, Desktop (JVM) |

## Архитектурные принципы

- **Многомодульность:** `core` / `data` / `feature`, каждый feature и data-слой разделён на **api** и **impl**.
- **Зависимости:** feature-модули зависят только от **api** других модулей, никогда от чужих **impl**.
- **Сборка DI:** только модуль `app` подключает все **impl** и собирает граф зависимостей.
- **UI:** весь UI на Compose; бизнес-логика и навигация — в shared.

---

## Модули

```
vk_kmp/
├── build-logic/
│   └── convention/                    # convention plugins
│
├── app/                               # composeApp, entry point, DI root, RootComponent
│
├── core/
│   ├── common/                        # Result, extensions, DispatcherProvider
│   ├── ui/                            # Theme, design system, common composables
│   ├── navigation/                    # Decompose utils, serializers
│   └── network/                       # HttpClient factory, logging (без VK-специфики)
│
├── auth/
│   ├── api/                           # контракты сессии и зон
│   └── impl/                          # SessionManager, refresh, logout, OAuth (desktop)
│
├── data/
│   ├── storage/
│   │   ├── api/
│   │   └── impl/
│   └── vk/
│       ├── api/                        # DTO, VkApi, repository interfaces
│       └── impl/                       # Ktor, mappers, WallRepository
│
└── feature/
    ├── login/
    │   ├── api/
    │   └── impl/                       # Login UI + platform AuthLauncher
    ├── main/
    │   ├── api/
    │   └── impl/                       # shell: tabs, только навигация
    └── feed/
        ├── api/
        └── impl/                       # первая фича — лента
```

### Граф зависимостей

```
app
 ├─ auth:impl, data:storage:impl, data:vk:impl
 ├─ feature:login:impl, feature:main:impl, feature:feed:impl
 └─ core:*

feature:login:impl  → feature:login:api, auth:api, core:ui
feature:main:impl   → feature:main:api, feature:feed:api, auth:api, core:ui
feature:feed:impl   → feature:feed:api, auth:api, data:vk:api, core:ui

auth:impl           → auth:api, data:storage:api, data:vk:api, core:network
data:vk:impl        → data:vk:api, auth:api, core:network
data:storage:impl   → data:storage:api

*:api               → core:common (минимум)
feature:*:api       → decompose (типы Component)
```

---

## Auth-модуль

Auth — **не экран логина**, а инфраструктура сессии и переключение зон.

### Ответственность

- хранение и обновление токена / сессии;
- переключение **авторизованная** / **неавторизованная** зона;
- logout (очистка storage, отмена запросов, сброс состояния);
- единая точка входа после успешного логина (`AuthResultHandler`).

### Платформенная стратегия (гибрид)

| Платформа | Вход | Кто отдаёт токены в auth:impl |
|-----------|------|-------------------------------|
| Android | VK ID SDK | `PlatformAuthLauncher` (actual) |
| iOS | VK ID SDK | `PlatformAuthLauncher` (actual) |
| Desktop | OAuth 2.0 + PKCE в браузере | `OAuthClient` (shared) + `BrowserLauncher` (actual) |

После получения `VkTokens` все платформы вызывают `AuthResultHandler.onLoginSuccess()` — дальше только `auth:impl`.

### Владение токеном

| Слой | Роль |
|------|------|
| `data:storage:impl` | persist: access, refresh, userId, expiresAt |
| `auth:impl` | валидность, refresh, logout, `SessionState` |
| `data:vk:impl` | не хранит токен, берёт через `SessionRepository` |

---

## Навигация (Decompose)

```
RootComponent                          [:app]
│
├─ [Unauthorized] LoginComponent       [:feature:login]
│     Store: LoginStore
│     Intent: OnLoginClick, OnRetry
│
└─ [Authorized] MainComponent          [:feature:main]
      │
      └─ BottomBar + TabStack
            └─ FeedComponent            [:feature:feed]  ← MVP: один таб
                  Store: FeedStore
                  Intent: Load, Refresh, LoadMore
```

Root слушает `AuthZoneController.zone` и переключает детей:

- `Unauthorized` → `LoginComponent`
- `Authorized` → `MainComponent`

Logout из любой фичи: `sessionRepository.logout()` → Root автоматически переходит в Login.

### Паттерн на экран

- `*Component` (Decompose) — `Value<Model>`, `onIntent()`, навигация по `Label`;
- `*Store` (MVIKotlin) — `Intent` → `State` → `Label`;
- UI (`@Composable`) — подписка на state, вызов intents.

---

## Первая фича: лента (`feature:feed`)

### MVP

- один таб «Лента» в `feature:main:impl`;
- список постов + pull-to-refresh;
- пагинация при скролле;
- состояния: Loading / Error / Empty;
- без детального экрана поста (второй этап).

### VK API (MVP)

| Method | Назначение |
|--------|------------|
| `wall.get` | посты (`owner_id`, `offset`, `count`) |
| `users.get` | имя и аватар текущего пользователя (`fields=photo_100`) |

Для старта: `wall.get` с `owner_id = текущий userId` (своя стена).

---

## Convention plugins (`build-logic`)

| Plugin ID | Модули | Включает |
|-----------|--------|----------|
| `vk.kmp.library` | core:*, data:*:api | kmp + android + ios + desktop |
| `vk.kmp.api` | *:api | минимальный kmp |
| `vk.cmp.library` | core:ui | compose |
| `vk.cmp.feature` | feature:*:impl | + compose, mvikotlin, decompose |
| `vk.app` | app | application, все impl |

---

## DI (Koin) — порядок модулей

```kotlin
modules(
    coreModule,
    storageImplModule,
    authImplModule,
    vkImplModule,
    loginFeatureModule,
    mainFeatureModule,
    feedFeatureModule,
    appModule,
)
```

---

## План по этапам

### Фаза 1 — Скелет
- Gradle: `build-logic` + пустые модули
- `app`: Compose, Root с двумя placeholder-зонами
- Convention plugins

### Фаза 2 — Auth
- `data:storage:impl`: save/load `VkTokens`
- `auth:impl`: `SessionManager`, `AuthZoneController`, logout
- `feature:login:impl`: UI + Android VK ID SDK
- Root переключает зоны

### Фаза 3 — iOS + Desktop auth ✅
- iOS VK ID SDK
- Desktop OAuth + `BrowserLauncher`
- `TokenRefresher` + Ktor interceptor (`refreshAccessToken` при 401 / error 5)

### Фаза 4 — Лента
- `data:vk:impl`: `wall.get`, DTO, mapper
- `feature:feed:impl`: Store + UI
- `feature:main:impl`: один таб Feed
- Pull-to-refresh, pagination, error states

### Фаза 5 — Полировка
- `Refreshing` overlay в Root
- Coil для аватарок и превью
- Тесты: `FeedStore`, `SessionManager` с fakes

---

## Зафиксированные решения

| Вопрос | Решение |
|--------|---------|
| Платформы | Android + iOS + Desktop |
| UI | Compose Multiplatform |
| Архитектура UI/State | Decompose + MVIKotlin |
| Структура модулей | core / data / feature, api-impl |
| Auth | Гибрид: VK ID SDK (mobile) + OAuth (desktop) → единый auth:impl |
| Auth-модуль | Токены, refresh, зоны, logout — не UI логина |
| Main | Тонкий shell, табы = отдельные feature-модули |
| Первая фича | `feature:feed` — лента через `wall.get` |
| Gradle | Convention plugins в `build-logic` |
