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

In order to run the `Showcases` application that lives in the `showcases-app` module of this
repository use the instructions below:

1. Get a free trial license at https://img.ly/forms/free-trial.
2. Copy the license key.
3. Include the license key in the `local.properties` file:

```
license=...
```

Note that failing to provide the license key-value pairing will display an error when opening any of
the showcases.

Source code of the mobile editor and camera can be
found [here](https://github.com/imgly/cesdk-android).
