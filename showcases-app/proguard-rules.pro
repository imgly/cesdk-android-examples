# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# Keep Lombok Generated annotation (referenced by Fal AI client)
-dontwarn lombok.Generated
-keep class lombok.Generated
-keepclassmembers class * {
    @lombok.Generated *;
}

# Keep Fal AI client library classes and methods
-keep class ai.fal.client.** { *; }
-keep class ai.fal.client.kt.** { *; }
-keepclassmembers class ai.fal.client.** {
    *;
}

# Keep builder patterns used by Fal AI client
-keepclassmembers class **$*Builder {
    public <methods>;
}

# Keep Google ML Kit classes (for background removal)
-keep class com.google.mlkit.vision.** { *; }
-keep class com.google.android.gms.internal.mlkit_vision_** { *; }
-dontwarn com.google.mlkit.vision.**

# Keep ML Kit segmentation classes
-keep class com.google.mlkit.vision.segmentation.** { *; }
-keep class com.google.mlkit.vision.segmentation.selfie.** { *; }

# Keep annotation classes
-keepattributes *Annotation*
-keepattributes Signature
-keepattributes Exceptions