plugins {
    id("com.android.application") version "7.2.0-alpha07" apply false
    kotlin("android") version "1.6.10" apply false
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
