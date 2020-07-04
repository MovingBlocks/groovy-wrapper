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

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

public class GroovyBootstrapMainStarter extends BootstrapMainStarter {
    @Override
    public void start(String[] args, File gradleHome) throws Exception {
        final URL[] urls = {
                findJar("groovy-all", gradleHome, "lib"),
                findJar("ivy", gradleHome, "lib/plugins"),
                findJar("junit", gradleHome, "lib/plugins")
        };
        try (URLClassLoader contextClassLoader = new URLClassLoader(urls, Thread.currentThread().getContextClassLoader())) {
            Thread.currentThread().setContextClassLoader(contextClassLoader);
            Class<?> mainClass = contextClassLoader.loadClass("groovy.ui.GroovyMain");
            Method mainMethod = mainClass.getMethod("main", String[].class);
            mainMethod.invoke(null, new Object[]{args});
        }
    }

    private URL findJar(String fragment, File gradleHome, String subdir) {
        final File gradleSubdir = new File(gradleHome, subdir);
        final File[] files = gradleSubdir.listFiles((d, name) ->
            name.startsWith(fragment + "-") && name.endsWith(".jar")
        );
        if (files == null) {
            throw new RuntimeException(String.format("Could not locate the JAR for %s; no such directory '%s'.",
                    fragment, gradleSubdir));
        } else if (files.length == 0) {
            throw new RuntimeException(String.format("Could not locate the JAR for %s in Gradle distribution '%s'.",
                    fragment, gradleHome));
        } else {
            try {
                return files[0].toURI().toURL();
            } catch (java.net.MalformedURLException e) {
                // This is so unlikely, let's keep it out of the signature.
                throw new RuntimeException(e.getMessage(),e);
            }
        }
    }
}
