import SwiftUI
import VkApp

@main
struct iOSApp: App {
    init() {
        VkIdAuthHelper.configure()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
