-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt

-keep,includedescriptorclasses class com.smartcity.greenpassport.**$$serializer { *; }
-keepclassmembers class com.smartcity.greenpassport.** {
    *** Companion;
}
-keepclasseswithmembers class com.smartcity.greenpassport.** {
    kotlinx.serialization.KSerializer serializer(...);
}
