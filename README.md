# TextScanner Android App

TextScanner is an Android application that uses the device's camera to capture and recognize text in real-time. The app features a target region in the middle of the camera preview, making it easy to focus on specific text you want to capture.

## Features

- Real-time camera preview
- Target region frame for precise text capture
- Text recognition using ML Kit
- Copy text to clipboard functionality
- User-friendly interface with instant feedback
- Support for various text orientations and languages

## Technical Details

- **Minimum SDK**: 21 (Android 5.0)
- **Target SDK**: 34 (Android 14)
- **ML Kit Version**: 16.0.1
- **CameraX Version**: 1.3.1

## Dependencies

- AndroidX Core KTX
- AndroidX AppCompat
- Google Material Design Components
- ML Kit Text Recognition
- CameraX (Camera2, Lifecycle, View)
- ViewBinding

## How to Use

1. Launch the app
2. Grant camera permission when prompted
3. Position the text you want to capture within the rectangular frame in the middle of the screen
4. Tap the "Capture Text" button to recognize text
5. Once text is captured, it will be displayed on screen
6. Use the "Copy Text" button to copy the recognized text to clipboard

## Building the Project

1. Clone the repository
```bash
git clone <repository-url>
```

2. Open the project in Android Studio

3. Build the debug version:
```bash
./gradlew assembleDebug
```

4. Build the release version:
```bash
./gradlew assembleRelease
```

## Version History

### v0.1
- Initial release
- Basic text recognition functionality
- Camera preview with target region
- Copy to clipboard feature
- Real-time feedback for text capture

## Technical Implementation

The app uses:
- CameraX for camera preview and image capture
- ML Kit's Text Recognition API for OCR
- ViewBinding for view access
- Kotlin Coroutines for asynchronous operations

## License

GPL-3.0

## Contributing

Feel free to submit issues and enhancement requests!

## Acknowledgments

This application was developed with the assistance of multiple AI services.
