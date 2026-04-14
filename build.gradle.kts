plugins {
  `java-library`
  application
  eclipse
  id("com.diffplug.spotless") version "8.4.0"
}

java.toolchain.languageVersion = JavaLanguageVersion.of(17)

version = "0.2"

tasks.jar {
  manifest.attributes(
      "Implementation-Title" to "WALA Start",
      "Implementation-Version" to archiveVersion,
  )
}

repositories {
  mavenCentral()
  mavenLocal()
}

dependencies {
  val walaVersion = "1.7.0"

  implementation("com.ibm.wala:com.ibm.wala.shrike:${walaVersion}")
  implementation("com.ibm.wala:com.ibm.wala.util:${walaVersion}")
  implementation("com.ibm.wala:com.ibm.wala.core:${walaVersion}")
  implementation("com.ibm.wala:com.ibm.wala.cast:${walaVersion}")
  implementation("com.ibm.wala:com.ibm.wala.cast.js:${walaVersion}")
  implementation("com.ibm.wala:com.ibm.wala.cast.js.rhino:${walaVersion}")
  // Duplicate implementation in original build.gradle
  // implementation("com.ibm.wala:com.ibm.wala.cast.js:${walaVersion}")
  // implementation("com.ibm.wala:com.ibm.wala.cast.js.rhino:${walaVersion}")
  implementation("com.ibm.wala:com.ibm.wala.cast.java:${walaVersion}")
  implementation("com.ibm.wala:com.ibm.wala.cast.java.ecj:${walaVersion}")
  implementation("com.ibm.wala:com.ibm.wala.dalvik:${walaVersion}")
  implementation("com.ibm.wala:com.ibm.wala.scandroid:${walaVersion}")
  testImplementation("junit:junit:4.+")
}

application.mainClass =
    project.findProperty("mainClass") as? String?
        ?: "com.ibm.wala.examples.drivers.PDFTypeHierarchy"

spotless {
  java { googleJavaFormat() }
  kotlinGradle { ktfmt() }
}
