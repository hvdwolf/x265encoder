# x265encoder

### Android ffmpegkit based x265 encoder/scaler/cropper

This app is completely based on [FFmpegKit](https://github.com/arthenica/ffmpeg-kit)

<img src="https://https://github.com/hvdwolf/x265encoder/blob/main/docs/assets/ffmpeg-kit-icon-v9.png" width="240">

`FFmpegKit` is a collection of tools to use `FFmpeg` in `Android`, `iOS`, `Linux`, `macOS`, `tvOS`, `Flutter` and `React Native` applications.

`x265encoder` is an Android app where I only compiled the underlying `ffmeg-kit.aar` for x265 and x264, and only for Android arm64-v8a.
This app is "only" created for myself to rescale and crop x264 and x265 4k movies to x265 1080p or 720p in case I need smaller videos. For that reasons it is a very quick & dirty app based on the Android `test-app-local-dependency` (and I did not really clean up).
If you can use it, please feel free to download.

### License
`x265encoder` is licensed under `GPL v3.0`.
`FFmpegKit` library alone is licensed under the `LGPL v3.0`.

`FFmpegKit` bundles (`.aar` archives, `frameworks`, `xcframeworks`), which include both  `FFmpegKit` and `FFmpeg`
libraries, are also licensed under the `LGPL v3.0`.
`FFmpegKit Test` repository is licensed under the [MIT License](https://opensource.org/licenses/MIT), fonts used by 
the applications are licensed under the [SIL Open Font License](https://opensource.org/licenses/OFL-1.1), other 
digital assets are published in the public domain.
