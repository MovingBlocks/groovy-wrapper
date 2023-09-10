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
import java.util.ArrayList;
import java.util.List;

public class GroovyBootstrapMainStarter extends BootstrapMainStarter {
    @Override
    public void start(String[] args, File gradleHome) throws Exception {
        final List<URL> urls = new ArrayList<>();
        urls.addAll(findJars("groovy", gradleHome, "lib"));
        urls.add(findJars("ivy", gradleHome, "lib").get(0));
        urls.add(findJars("junit", gradleHome, "lib").get(0));
        try (URLClassLoader contextClassLoader = new URLClassLoader(urls.toArray(new URL[0]), ClassLoader.getSystemClassLoader().getParent())) {
            Thread.currentThread().setContextClassLoader(contextClassLoader);
            Class<?> mainClass = contextClassLoader.loadClass("groovy.ui.GroovyMain");
            Method mainMethod = mainClass.getMethod("main", String[].class);
            mainMethod.invoke(null, new Object[]{args});
        }
    }

    private List<URL> findJars(String fragment, File gradleHome, String subdir) {
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
                final List<URL> urls = new ArrayList<>();
                for (File file : files) {
                    urls.add(file.toURI().toURL());
                }
                return urls;
            } catch (java.net.MalformedURLException e) {
                // This is so unlikely, let's keep it out of the signature.
                throw new RuntimeException(e.getMessage(),e);
            }
        }
    }
}
