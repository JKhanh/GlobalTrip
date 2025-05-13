import SwiftUI
import ComposeApp

@main
struct iOSApp: App {
    init() {
        // Initialize Koin for iOS
        GlobalTripIosAppKt.doInitialize()
    }
    
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}