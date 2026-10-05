![Hero image showing the configuration abilities of IMGLYEngine](https://img.ly/static/cesdk_release_header_android.png)

# IMGLY Creative Engine and UI - Android Examples

This repository contains the Android examples for the IMG.LY `Engine`, the core of CE.SDK, as well
as the source code of our mobile editor solutions.
The `Engine` enables you to build any design editing UI, automation and creative workflow in Kotlin.
It offers performant and robust graphics processing capabilities combining the best of layout,
typography and image processing with advanced workflows centered around templating and adaptation.

The `Engine` seamlessly integrates into any Android app whether you are building a photo editor,
template-based design tool or scalable automation of content creation for your app.
The mobile editor is fully built on top of the `Engine`.

## Documentation

The full documentation of the [engine](https://img.ly/docs/cesdk/engine/quickstart?platform=android)
and the [mobile editor](https://img.ly/docs/cesdk/mobile-editor/quickstart?platform=android) can be
found on our website.
There you will learn how to integrate and configure them for your use case.

## License

The `Engine` is a commercial product. To use it you need to unlock the SDK with a license file. You
can purchase a license at https://img.ly/pricing.

### Evaluation Mode

You can run the SDK in evaluation mode by passing `null` or an empty string as the license key. This will add a watermark to all exports but allows you to try out the SDK without a license.

### Using a License Key

In order to run the `Examples` application that lives in the `examples-app` module of this
repository with a license key, use the instructions below:

1. Request a license at https://img.ly/forms/contact-sales/.
2. Copy the license key.
3. Include the license key in the `local.properties` file:

```
license=...
```

## Run the Examples App

Open this repository in Android Studio and run the `examples-app` configuration, or install its
debug build from the command line:

```bash
./gradlew :examples-app:installDebug
```

The app lists the curated starter kits and guides used by the documentation previews. The catalog
shown on the home screen is defined in
`examples-app/src/main/kotlin/ly/img/editor/examples/Destination.kt`. Guide source files live in
the corresponding `editor-guides-*`, `camera-guides-*`, and `engine-guides-*` directories at the
repository root. The catalog app compiles these guides and picks up source changes on the next
build, except for `editor-guides-quickstart`, which remains a standalone sample.

Source code of the mobile editor and camera can be
found [here](https://github.com/imgly/cesdk-android).
