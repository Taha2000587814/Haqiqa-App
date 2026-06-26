# Haqiqa (حقيقة) — Truth in Real Time

Haqiqa is a professional-grade fact-checking and media verification application designed for mobile devices. Using state-of-the-art AI verification models and deep web search capabilities, Haqiqa enables users to instantly verify social media claims, links, images, and audio clips.

---

## 🎨 Design Theme: Professional Polish

The application implements the **Professional Polish** design language, centered on an elegant, modern, and trustworthy aesthetic. Rather than using harsh, generic dark themes, it employs a sophisticated palette with careful attention to Material Design 3 guidelines:

- **Elegant Color Palette**: Uses a clean, off-white background (`#FDFBFF`) paired with soft slate-gray containers (`#F3F3FA`) to separate content card zones with subtle borders (`#DCE3E9`).
- **Trustworthy Accents**: Employs deep professional blue (`#0061A4`) for core tags, primary actions, and branding, ensuring strong contrast and a sense of authority.
- **Contextual Status Indicators**: High-contrast, color-coded badges highlight claim states—vibrant red (`#BA1A1A`) for verified false claims, deep forest green (`#006F3F`) for verified true claims, and rich warm amber (`#8E5000`) for unverifiable statements.
- **Bilingual Typographic Harmony**: Fully optimized for dual-language (English & Arabic) layout transitions, employing balanced negative space, appropriate padding, and consistent vertical grid alignment.

---

## 🚀 Key Features

### 🔍 Real-Time Claim & Link Verification
Paste any text, social media link, or news article to instantly initiate a deep fact-checking query. The verification process leverages deep web search, cross-referencing reliable archives, official statements, and fact-checking databases.

### 🤖 Multi-Engine Support
Select between specialized AI models optimized for different intelligence and retrieval objectives:
- **Gemini (Default)**: Balanced, multi-perspective logical reasoning and structured knowledge synthesis.
- **Galaxy AI**: Specialized in high-fidelity image inspection, fabrication detection, and deepfake analysis.
- **Fact GPT**: Optimized for deep crawling of digital news archives, press releases, and historical statements.

### 📊 Real-Time Snapshot
View recent fact-checking entries at a glance, showing the status, source references, a clear summary of the core truth, and convenient sharing options.

### ⏱️ Hourly Usage Tracker
Monitors usage metrics under a custom light-blue container with navy-blue details. Includes a dynamic progress bar showing hourly limits, live countdown timers for limits reset, and an interactive developer testing reset button.

### 🌍 Dual-Language System
Support for bilingual users with a toggle at the top of the interface allowing instant switching between English (EN) and Arabic (AR) layouts, translating all labels, hints, and error states dynamically.

---

## 🛠️ Architecture & Technologies

The application is built on top of modern Android and Jetpack Compose frameworks:

- **Jetpack Compose**: 100% declarative UI with Material Design 3 components.
- **Coroutines & Flow**: Structured concurrency for non-blocking asynchronous requests and reactive state management.
- **Retrofit & Ktor**: Robust, production-grade network layers for REST APIs.
- **ViewModel & StateFlow**: State preservation and unidirectional data flow (UDF) patterns.
- **Multi-language Translation Engine**: Custom dynamic translation manager to localize content in real-time.

---

## 📦 Getting Started

### Prerequisites
- Android Studio Ladybug (or newer)
- JDK 17+
- Android SDK 34 (Upside Down Cake) or newer

### Building and Running
1. Clone the repository to your local system.
2. Open the project in Android Studio.
3. Sync project Gradle files.
4. Set up your API credentials in your environment configuration (or via AI Studio Secrets).
5. Build and run the app on your physical device or emulator.

---

## 🧪 Testing

The codebase includes JVM local tests utilizing Robolectric and Roborazzi for visual verification.

To execute the unit and integration tests:
```bash
gradle :app:testDebugUnitTest
```
