import Foundation
import UIKit
import VKID
import VkApp

enum VkIdAuthHelper {
    private static let authRequestNotification = Notification.Name("VkIdAuthRequested")

    static func configure() {
        configureVkIdSdk()
        NotificationCenter.default.addObserver(
            forName: authRequestNotification,
            object: nil,
            queue: .main
        ) { _ in
            authorize()
        }
    }

    private static func configureVkIdSdk() {
        guard
            let clientId = Bundle.main.object(forInfoDictionaryKey: "VKClientId") as? String,
            let clientSecret = Bundle.main.object(forInfoDictionaryKey: "VKClientSecret") as? String,
            !clientId.isEmpty,
            clientId != "YOUR_APP_ID"
        else {
            return
        }

        do {
            _ = try VKID(
                config: Configuration(
                    appCredentials: AppCredentials(
                        clientId: clientId,
                        clientSecret: clientSecret
                    )
                )
            )
        } catch {
            assertionFailure("Failed to initialize VKID: \(error)")
        }
    }

    private static func authorize() {
        guard let controller = UIApplication.shared.connectedScenes
            .compactMap({ $0 as? UIWindowScene })
            .flatMap(\.windows)
            .first(where: \.isKeyWindow)?
            .rootViewController
        else {
            Login_implKt.completeVkIdIosAuthError(message: "Root view controller is not available")
            return
        }

        VKID.shared.authorize(
            from: controller,
            using: .ui
        ) { result in
            switch result {
            case .success(let session):
                Login_implKt.completeVkIdIosAuthSuccess(
                    accessToken: session.accessToken.value,
                    refreshToken: session.refreshToken?.value,
                    userId: Int64(session.userId.value),
                    expiresAtEpochSeconds: session.accessToken.expirationDate.map { Int64($0.timeIntervalSince1970) },
                    deviceId: session.deviceId.value
                )
            case .failure(let error):
                if error.localizedDescription.lowercased().contains("cancel") {
                    Login_implKt.completeVkIdIosAuthCancelled()
                } else {
                    Login_implKt.completeVkIdIosAuthError(message: error.localizedDescription)
                }
            }
        }
    }
}
