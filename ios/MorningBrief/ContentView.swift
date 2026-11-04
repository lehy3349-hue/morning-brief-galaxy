import SwiftUI
import WebKit
import UserNotifications

struct ContentView: View {
    @StateObject private var alarms = AlarmManager()
    var body: some View {
        ZStack {
            Color(red: 0.01, green: 0.07, blue: 0.18).ignoresSafeArea()
            WebHomeView()
                .ignoresSafeArea(edges: .bottom)
        }
        .task { await alarms.requestPermission() }
    }
}

struct WebHomeView: UIViewRepresentable {
    func makeUIView(context: Context) -> WKWebView {
        let web = WKWebView()
        web.scrollView.contentInsetAdjustmentBehavior = .never
        web.isOpaque = false
        web.backgroundColor = UIColor(red: 0.01, green: 0.07, blue: 0.18, alpha: 1)
        web.load(URLRequest(url: URL(string: "https://ayeon-morning-brief.vercel.app")!))
        return web
    }
    func updateUIView(_ uiView: WKWebView, context: Context) {}
}
