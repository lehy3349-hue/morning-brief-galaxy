import Foundation
import UserNotifications

@MainActor
final class AlarmManager: ObservableObject {
    @Published var authorized = false

    func requestPermission() async {
        do {
            authorized = try await UNUserNotificationCenter.current().requestAuthorization(options: [.alert, .badge, .sound])
        } catch {
            authorized = false
        }
    }

    /// Fallback notification. This is NOT an automatic spoken-news alarm.
    func scheduleDailyReminder(hour: Int, minute: Int) async throws {
        let content = UNMutableNotificationContent()
        content.title = "🦁 ayeon wake up!"
        content.body = "오늘의 모닝 브리프가 준비됐어요. 눌러서 뉴스를 들어보세요."
        content.sound = .default
        var components = DateComponents()
        components.hour = hour
        components.minute = minute
        let trigger = UNCalendarNotificationTrigger(dateMatching: components, repeats: true)
        let request = UNNotificationRequest(identifier: "daily-morning-brief", content: content, trigger: trigger)
        try await UNUserNotificationCenter.current().add(request)
    }
}
