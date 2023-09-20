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

#-------------------------------------------------------------------------------------------------------------------------------------------

# Retrofit 2.X
## https://square.github.io/retrofit/ ##
# https://github.com/krschultz/android-proguard-snippets/blob/master/libraries/proguard-square-retrofit2.pro

-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions

-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}

-dontwarn okhttp3.**
-keep class okhttp3.** {*;}


#-------------------------------------------------------------------------------------------------------------------------------------------

-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
-keep class com.bumptech.**{ *; }
-dontwarn com.bumptech.**

-dontwarn okio.**

#-------------------------------------------------------------------------------------------------------------------------------------------

-keepattributes InnerClasses
-keepattributes EnclosingMethod

#-------------------------------------------------------------------------------------------------------------------------------------------

# remove logs
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
}

#-------------------------------------------------------------------------------------------------------------------------------------------

# JSOUP
-keeppackagenames org.jsoup.nodes

#-------------------------------------------------------------------------------------------------------------------------------------------

# For Crashlytics
-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable
-keep public class * extends java.lang.Exception
-keep class com.crashlytics.** { *; }
-dontwarn com.crashlytics.**

#-------------------------------------------------------------------------------------------------------------------------------------------

# For ARCore
-dontwarn com.google.ar.sceneform.animation.AnimationEngine
-dontwarn com.google.ar.sceneform.animation.AnimationLibraryLoader

#-------------------------------------------------------------------------------------------------------------------------------------------

# For Social Login Module
-keepclassmembernames class com.webkul.mobikul.activities.LoginAndSignUpActivity { *; }
-keepclassmembers class com.webkul.mobikul.activities.LoginAndSignUpActivity { *; }
-keep class com.webkul.mobikul.activities.LoginAndSignUpActivity { *; }
-keepclassmembernames class com.webkul.mobikul.mobikulsociallogin.MobikulSocialLoginHelper { *; }
-keepclassmembers class com.webkul.mobikul.mobikulsociallogin.MobikulSocialLoginHelper { *; }
-keep class com.webkul.mobikul.mobikulsociallogin.MobikulSocialLoginHelper { *; }

-keepclassmembernames class com.webkul.mobikul.handlers.LoginBottomSheetHandler { *; }
-keepclassmembers class  com.webkul.mobikul.handlers.LoginBottomSheetHandler{ *; }
-keep class  com.webkul.mobikul.handlers.LoginBottomSheetHandler { *; }
#-------------------------------------------------------------------------------------------------------------------------------------------

-dontwarn com.google.maps.**
-dontwarn org.joda.time.**
-dontwarn org.slf4j.**

# Jackson
-keep @com.fasterxml.jackson.annotation.JsonIgnoreProperties class * { *; }
-keep class com.fasterxml.** { *; }
-keep class org.codehaus.** { *; }
-keepnames class com.fasterxml.jackson.** { *; }
-keepclassmembers public final enum com.fasterxml.jackson.annotation.JsonAutoDetect$Visibility {
    public static final com.fasterxml.jackson.annotation.JsonAutoDetect$Visibility *;
}
-keep class kotlin.Metadata { *; }
-keep class com.webkul.mobikul.models.user.AddressFormResponseModel { *; }
-keep class kotlin.Metadata { *; }


# General
-keepattributes SourceFile,LineNumberTable,*Annotation*,EnclosingMethod,Signature,Exceptions,InnerClasses



-keep class com.webkul.mobikul.models.**{ *; }
-keepclassmembernames class com.webkul.mobikul.models.**{ *; }
-keepclassmembers class com.webkul.mobikul.models.**{ *; }
-dontwarn com.webkul.mobikul.models.**

-keep class com.webkul.mobikul.helpers.**{ *; }
-keepclassmembernames class com.webkul.mobikul.helpers.**{ *; }
-keepclassmembers class com.webkul.mobikul.helpers.**{ *; }
-dontwarn com.webkul.mobikul.helpers.**

-keep class com.bumptech.**{ *; }
-dontwarn com.bumptech.**

-keep class com.webkul.mlkit.**{ *; }
-keepclassmembernames class com.webkul.mlkit.**{ *; }
-keepclassmembers class com.webkul.mlkit.**{ *; }
-dontwarn com.webkul.mlkit.**


-keep class com.google.firebase.ml.vision.label.**{ *; }
-keepclassmembernames class com.google.firebase.ml.vision.label.**{ *; }
-keepclassmembers class com.google.firebase.ml.vision.label.**{ *; }
-dontwarn   com.google.firebase.ml.vision.label.**

-keep class com.webkul.mlkit.adapters.**{ *; }
-keepclassmembernames class com.webkul.mlkit.adapters.**{ *; }
-keepclassmembers class com.webkul.mlkit.adapters.**{ *; }
-dontwarn   com.webkul.mlkit.adapters.**

-keep class com.webkul.mobikulmp.**{ *; }
-keepclassmembernames class com.webkul.mobikulmp.**{ *; }
-keepclassmembers classcom.webkul.mobikulmp.**{ *; }
-dontwarn  com.webkul.mobikulmp.**
-keep class com.webkul.mobikul.launcherAlias.DefaultLauncherAlias { *; }
-keep class com.webkul.mobikul.launcherAlias.FifthLauncherAlias { *; }
-keep class com.webkul.mobikul.launcherAlias.FirstLauncherAlias { *; }
-keep class com.webkul.mobikul.launcherAlias.FourthLauncherAlias { *; }
-keep class com.webkul.mobikul.launcherAlias.SecondLauncherAlias { *; }
-keep class com.webkul.mobikul.launcherAlias.ThirdLauncherAlias { *; }

#Crop Image
-keep class com.theartofdev.edmodo.cropper.**{ *; }

#RazoPay
-keepclassmembers class * {    @android.webkit.JavascriptInterface <methods>;}
-keepattributes JavascriptInterface
-keepattributes *Annotation*
-dontwarn com.razorpay.**
-keep class com.razorpay.** {*;}
-optimizations !method/inlining/*
-keepclasseswithmembers class * {  public void onPayment*(...);}

-keep class com.webkul.mobikul.models.user.AddressFormResponseModel { *; }


