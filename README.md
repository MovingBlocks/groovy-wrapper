# Groovy Wrapper

This is a tiny extension for the [Gradle Wrapper](https://docs.gradle.org/current/userguide/gradle_wrapper.html) that provides a similar `groovyw` utility script to go along with `gradlew`, using the Groovy embedded with the associated version of Gradle to instead execute a plain `.groovy` script.

Executing either `gradlew` or `groovyw` (preceded with `./` if needed) for the first time for a given version will download the Gradle distribution normally. Either script will use the same local files.

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
 
When you have a working setup with the included test script you may want to customize a bit further, otherwise you have to always type `groovyw [scriptname]` and have to keep files in the root of the working directory or include a path in your command. [Terasology](https://github.com/MovingBlocks/Terasology) serves as a useful example. Look at the `groovyw` and `groovyw.bat` files in this repo and the example and you will find something like the following (considering usual differences between Windows and Linux style - adjust both files if you want compatibility on both OS flavors):

* `... org.gradle.wrapper.GroovyWrapperMain "$APP_ARGS"` - this tells Groovy to run whatever you pass on the command line
* `... org.gradle.wrapper.GroovyWrapperMain config/groovy/util.groovy "$APP_ARGS"` - this has both a sub-path _and_ a target script to execute, meaning now the remaining arguments go to the target script directly

As such a command like `groovyw Test` that previously ran a `Test.groovy` in the workspace root now can become `groovyw usage` which targets `config/groovy/util.groovy` and executes the `usage` method therein.

## Versions

### v2.0

As of Gradle v6.4 the code innards the Groovy Wrapper relies on finally changed - this was an expected future and just a question of time.

As a consequence a v2 of this wrapper has been prepared and built, and can be used with Gradle 6.4 and onwards, to the next unknown future point of breakage.

However, that may not work with *older* Gradles, so you may have to use one or the other.

Additionally, the 6.4 Gradle no longer ships with `commons-io` so we can't well put that on the Groovy Wrapper's classpath anymore. Sorry [#2](https://github.com/MovingBlocks/groovy-wrapper/issues/2) !

### v2.1

This bump was needed as Gradle v7 changed how Groovy is embedded, now in multiple modular jars. It should be backwards compatible.

### v2.2

Gradle v8 support. Unsure on backwards compatibility.

## Gradle

Yep, it is Gradles all the way down :-)

The Groovy Wrapper was based on Gradle 3.3 using the wrapper files embedded within this repo (retrieved via regular `gradlew wrapper` update) along with two edited Java source files from https://github.com/gradle/gradle/tree/master/subprojects/wrapper/src/main/java/org/gradle/wrapper that haven't been changed for some time.

While primary testing was done with 3.3 you can vary the target Gradle version via `gradle/wrapper/gradle-wrapper.properties` normally and be using that Gradle (and its embedded Groovy) without trouble at least for some versions both back and forward. Older Gradle wrapper *jar* versions may not function and future changes may also become incompatible with this extension. Any upgrade is likely trivial in effort.

## License

As with Gradle itself this extension is licensed under Apache 2.0 with gratitude to the Gradle project for the great tool. This extension is provided with no warranty or affiliation with https://gradle.org or https://gradle.com
