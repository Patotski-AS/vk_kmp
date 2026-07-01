# iosApp

Xcode-обёртка для iOS с VK ID SDK.

## Подготовка

1. Соберите shared framework:
   ```bash
   ./gradlew :app:linkDebugFrameworkIosSimulatorArm64
   ```
2. Укажите `VKClientId` и `VKClientSecret` в `iosApp/iosApp/Info.plist`.
3. В кабинете VK ID добавьте redirect scheme `vk{APP_ID}` и Universal Link.
4. Установите pods и откройте workspace:
   ```bash
   cd iosApp
   pod install
   open iosApp.xcworkspace
   ```

## Как работает auth

1. Kotlin (`PlatformAuthLauncher.ios`) отправляет notification `VkIdAuthRequested`.
2. `VkIdAuthHelper.swift` запускает `VKID.shared.authorize`.
3. Результат возвращается в Kotlin через `Login_implKt.completeVkIdIosAuthSuccess/Cancelled/Error`.

Создайте Xcode-проект `iosApp` (App template), подключите `VkApp.framework` из `app/build/xcode-frameworks/` и добавьте Swift-файлы из этой папки.
