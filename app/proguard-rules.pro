# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Users\navendu\AppData\Local\Android\Sdk/tools/proguard/proguard-android.txt
# You can edit the include path and appleSeqNumber by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
-dontwarn okio.**
-dontwarn com.squareup.okhttp.**
-dontwarn com.squareup.picasso.**
-dontwarn com.fasterxml.jackson.**

-keepclassmembers class com.sparshik.yogicapple.model.** {
*;
}

-keepattributes Signature
-keepattributes *Annotation*
-keep class org.apache.http.** { *; }
-dontwarn org.apache.http.**
-keep class android.support.v7.widget.SearchView { *; }
-dontwarn android.net.**
-keepattributes InnerClasses,EnclosingMethod
# Basic ProGuard rules for Firebase Android SDK 2.0.0+
-keep class com.firebase.** { *; }

-keep class com.newrelic.** { *; }
-dontwarn com.newrelic.**
-keepattributes Exceptions, Signature, InnerClasses, LineNumberTable
