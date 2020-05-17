# Groovy Wrapper

This is a tiny extension for the [Gradle Wrapper](https://docs.gradle.org/current/userguide/gradle_wrapper.html) that provides a similar `groovyw` utility script to go along with `gradlew`, using the Groovy embedded with the associated version of Gradle to instead execute a plain `.groovy` script.

Executing either `gradlew` or `groovyw` for the first time for a given version will download the Gradle distribution normally. Either script will use the same local files.

This should make using a mix of Gradle and Groovy scripts in a project very convenient as you do not need to manually install Gradle *or* Groovy, and both will be using the same version of Groovy.

## To use

Pick one of the below options:

* Simply grab the files from this repo and add them to an existing project backed by a Gradle Wrapper
    * `gradle/wrapper/groovy-wrapper.jar` should live next to the `gradle-wrapper.jar`
    * `groovyw` and/or `groovyw.bat` should live next to their Gradle variants in the root of a project
* Download a zip file from the Releases tab, extract, place the files accordingly into a local workspace
* Build your own by cloning this repo and running `gradlew jar` to get a fresh `groovy-wrapper.jar` of your own
    * You can run `gradlew prep` to both build and place the jar under `gradle/wrapper` where it can be tested with `groovyw Test`
    * For convenience a built version is already maintained there for easy testing

## Gradle

Yep, it is Gradles all the way down :-)

The Groovy Wrapper was based on Gradle 3.3 using the wrapper files embedded within this repo (retrieved via regular `gradlew wrapper` update) along with two edited Java source files from https://github.com/gradle/gradle/tree/master/subprojects/wrapper/src/main/java/org/gradle/wrapper that haven't been changed for some time.

While primary testing was done with 3.3 you can vary the target Gradle version via `gradle/wrapper/gradle-wrapper.properties` normally and be using that Gradle (and its embedded Groovy) without trouble at least for some versions both back and forward. Older Gradle wrapper *jar* versions may not function and future changes may also become incompatible with this extension. Any upgrade is likely trivial in effort.

## License

As with Gradle itself this extension is licensed under Apache 2.0 with gratitude to the Gradle project for the great tool. This extension is provided with no warranty or affiliation with https://gradle.org or https://gradle.com 
