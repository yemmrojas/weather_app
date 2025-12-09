## V 1.0.0

## Added
- [TASK-001] Initial configuration and setup of the project structure.
- [HU-001] The presentation screen display was implemented
- [HU-002] Real-Time Location Search was implemented
- [HU-003] Location Details View was implemented
- [TASK-002] Implemented native splash screen using Android SplashScreen API
  - Added `androidx.core:core-splashscreen` dependency for splash screen support
  - Configured splash screen theme with custom colors (Purple40 background, Purple80 icon background)
  - Integrated adaptive launcher icon for consistent branding
  - Replaced Compose-based splash screen with native implementation to eliminate double splash issue

## Changed
- [TASK-002] Refactored navigation structure
  - Removed unused `Routes.Error` and error screen entry from navigation
  - Simplified navigation to only include `WeatherSearch` and `WeatherDetail` routes
  - Updated initial route from `Splash` to `WeatherSearch` after splash screen refactoring
  - Cleaned up unused imports and components in `NavWrapper`