# x265encoder

### Android ffmpegkit based x265 encoder/scaler/cropper

This app is completely based on [FFmpegKit](https://github.com/arthenica/ffmpeg-kit)

<img src="https://github.com/hvdwolf/x265encoder/blob/main/docs/assets/ffmpeg-kit-icon-v9.png" width="240">

`FFmpegKit` is a collection of tools to use `FFmpeg` in `Android`, `iOS`, `Linux`, `macOS`, `tvOS`, `Flutter` and `React Native` applications.

`x265encoder` is an Android app where I only compiled the underlying `ffmeg-kit.aar` for x265 and x264, and only for Android arm64-v8a.
This app is "only" created for myself to rescale and crop x264 and x265 4k movies to x265 1080p or 720p in case I need smaller videos. For that reasons it is a very quick & dirty app based on the Android `test-app-local-dependency`  from [ffmpeg-kit-test android](https://github.com/arthenica/ffmpeg-kit-test/tree/main/android) (with only some minor clean up).<br>
If you can use it, please feel free to download.

### Usage
Manually input a filename like `20230628-160301.mp4`.<br>
It will take it from `/storage/emulated/0/DCIM/Camera/`.<br>
No file picker. Use some filemanager to get the video name.<br>
It will create a new mp4 at the same location, but then something like `0230628-160301-1080p.mp4` (rescale 4k to 2k) or `0230628-160301-crop-1080p.mp4` (crop 4k to 2k).<br>
*(Did I already mention that it is a quick & dirty application?)*

### License
`x265encoder` is licensed under `GPL v3.0`.

`FFmpegKit` library alone is licensed under the `LGPL v3.0`.

`FFmpegKit` bundles (`.aar` archives, `frameworks`, `xcframeworks`), which include both  `FFmpegKit` and `FFmpeg`
libraries, are also licensed under the `LGPL v3.0`.
`FFmpegKit Test` repository is licensed under the [MIT License](https://opensource.org/licenses/MIT), fonts used by the applications are licensed under the [SIL Open Font License](https://opensource.org/licenses/OFL-1.1), other digital assets are published in the public domain.
