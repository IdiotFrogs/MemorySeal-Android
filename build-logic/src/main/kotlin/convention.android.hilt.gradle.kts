import com.idiotfrogs.memoryseal.libs

plugins {
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
}

dependencies {
    "implementation"(libs.findLibrary("hilt-android").get())
    "ksp"(libs.findLibrary("hilt-compiler").get())
}