/*
 * Copyright 2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.gradle.wrapper;

import java.io.Closeable;
import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

public class GroovyBootstrapMainStarter extends BootstrapMainStarter {
    @Override
    public void start(String[] args, File gradleHome) throws Exception {
        File groovyJar = findGroovyJar(gradleHome);
        File ivyJar = findIvyJar(gradleHome);
        URLClassLoader contextClassLoader = new URLClassLoader(new URL[]{groovyJar.toURI().toURL(),ivyJar.toURI().toURL()}, ClassLoader.getSystemClassLoader().getParent());
        Thread.currentThread().setContextClassLoader(contextClassLoader);
        Class<?> mainClass = contextClassLoader.loadClass("groovy.ui.GroovyMain");
        Method mainMethod = mainClass.getMethod("main", String[].class);
        mainMethod.invoke(null, new Object[]{args});
        if (contextClassLoader instanceof Closeable) {
            ((Closeable) contextClassLoader).close();
        }
    }

    private File findGroovyJar(File gradleHome) {
        for (File file : new File(gradleHome, "lib").listFiles()) {
            if (file.getName().matches("groovy-all-.*\\.jar")) {
                return file;
            }
        }
        throw new RuntimeException(String.format("Could not locate the Groovy JAR in Gradle distribution '%s'.", gradleHome));
    }

    private File findIvyJar(File gradleHome) {
        for (File file : new File(gradleHome, "lib/plugins").listFiles()) {
            if (file.getName().matches("ivy-.*\\.jar")) {
                return file;
            }
        }
        throw new RuntimeException(String.format("Could not locate the Ivy JAR in Gradle distribution '%s'.", gradleHome));
    }
}
