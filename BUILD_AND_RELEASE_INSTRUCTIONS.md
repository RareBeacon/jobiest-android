# Jobiest Android Build and Release Instructions

## Prerequisites
1. **JDK 17**: Eclipse Adoptium OpenJDK 17 or equivalent.
2. **Android SDK 34**: Android SDK platform 34 and build-tools 34.0.0.
3. **Gradle 8.7**.

---

## 1. Local Environment Setup

Set the following environment variables (PowerShell):
```powershell
$env:JAVA_HOME = "C:\Users\CEO Profitablechoice\.gemini\antigravity\scratch\android-toolchain\jdk-17"
$env:ANDROID_HOME = "C:\Users\CEO Profitablechoice\.gemini\antigravity\scratch\android-toolchain\android-sdk"
$env:PATH = "$env:JAVA_HOME\bin;C:\Users\CEO Profitablechoice\.gemini\antigravity\scratch\android-toolchain\gradle-8.7\bin;$env:ANDROID_HOME\cmdline-tools\latest\bin;$env:ANDROID_HOME\platform-tools;$env:PATH"
```

---

## 2. Compiling the Application

Navigate to the project root:
```powershell
cd "C:\Users\CEO Profitablechoice\.gemini\antigravity\scratch\jobiest-android"
```

### Build Debug APK
```powershell
gradle assembleDebug --no-daemon
```
The resulting APK is generated at:
`app/build/outputs/apk/debug/app-debug.apk`

### Run Automated Unit Tests
```powershell
gradle testDebugUnitTest --no-daemon
```

---

## 3. Physical Device Installation

1. Connect your Android phone via USB cable.
2. Ensure **Developer Options** and **USB Debugging** are enabled on the phone.
3. Verify connection:
   ```powershell
   adb devices -l
   ```
4. Install the debug APK:
   ```powershell
   adb install -r app/build/outputs/apk/debug/app-debug.apk
   ```

---

## 4. Google Play Release Preparation

To build a release-ready Android App Bundle (`.aab`) for Google Play Console:

1. Generate a production signing key:
   ```powershell
   keytool -genkey -v -keystore jobiest-release-key.jks -keyalg RSA -keysize 2048 -validity 10000 -alias jobiest-release
   ```
2. Store the keystore securely and set environment variables:
   - `KEYSTORE_FILE`
   - `KEYSTORE_PASSWORD`
   - `KEY_ALIAS`
   - `KEY_PASSWORD`
3. Build the bundle:
   ```powershell
   gradle bundleRelease --no-daemon
   ```
4. Output file:
   `app/build/outputs/bundle/release/app-release.aab`
