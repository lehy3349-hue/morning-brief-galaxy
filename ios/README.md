# ayeon wake up! — iPhone app

This is the iOS-native project source started from the existing Samsung Lions Morning Brief UI. Android files remain unchanged.

## Current status
- SwiftUI iOS entry point and WKWebView showing the existing live home screen.
- Native local notification scheduling helper.
- **Not yet a signed IPA or TestFlight release.**
- **Does not automatically play spoken news while locked.** iOS does not generally allow a terminated app to launch and start arbitrary audio at a scheduled time.
- The existing web view still needs iOS-native bridges for weather/news/alarms and independent live-news API endpoints.

## Build
Create an Xcode iOS App target named MorningBrief, minimum iOS 26, SwiftUI, and add these Swift files. Select your Apple development team, signing, and a unique bundle ID. For production/TestFlight distribution an Apple Developer Program membership and signing are required.

## Validation plan
1. Implement and test AlarmKit authorization and recurring alarms on a physical iPhone running iOS 26+.
2. Verify allowed custom alarm sound duration and behavior; do not promise full news playback until proven.
3. Implement a news/audio backend and verify playback while screen is locked when audio has already started.
4. Test device locked, silent mode, Focus mode, reboot, force-quit, no network, and offline fallback.
5. Distribute with TestFlight after signing.

Design source: https://github.com/lehy3349-hue/morning-brief-galaxy/tree/main/docs
