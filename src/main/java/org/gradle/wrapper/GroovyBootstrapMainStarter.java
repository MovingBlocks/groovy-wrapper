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
        urls.add(findJars("ivy", gradleHome, "lib/plugins").get(0));
        // JUnit is only needed by GroovyTestCase (see this project's Test.groovy), not to run ordinary
        // Groovy scripts, and Gradle stopped shipping a junit jar in its distribution's lib/ as of
        // Gradle 9. Treat it as optional so its absence doesn't fail the whole bootstrap - same
        // situation as commons-io disappearing in Gradle 6.4 (issue #2).
        urls.addAll(findJarsIfPresent("junit", gradleHome, "lib"));
        try (URLClassLoader contextClassLoader = new URLClassLoader(urls.toArray(new URL[0]), ClassLoader.getSystemClassLoader().getParent())) {
            Thread.currentThread().setContextClassLoader(contextClassLoader);
            Class<?> mainClass = contextClassLoader.loadClass("groovy.ui.GroovyMain");
            Method mainMethod = mainClass.getMethod("main", String[].class);
            mainMethod.invoke(null, new Object[]{args});
        }
    }

    private List<URL> findJars(String fragment, File gradleHome, String subdir) {
        final File gradleSubdir = new File(gradleHome, subdir);
        final File[] files = matchingJars(fragment, gradleSubdir);
        if (files == null) {
            throw new RuntimeException(String.format("Could not locate the JAR for %s; no such directory '%s'.",
                    fragment, gradleSubdir));
        } else if (files.length == 0) {
            throw new RuntimeException(String.format("Could not locate the JAR for %s in Gradle distribution '%s'.",
                    fragment, gradleHome));
        } else {
            return toUrls(files);
        }
    }

    /**
     * As {@link #findJars}, but yields an empty list instead of throwing when the directory or the jar
     * is absent. For dependencies that some Gradle distributions ship and others do not.
     */
    private List<URL> findJarsIfPresent(String fragment, File gradleHome, String subdir) {
        final File[] files = matchingJars(fragment, new File(gradleHome, subdir));
        if (files == null || files.length == 0) {
            return new ArrayList<>();
        }
        return toUrls(files);
    }

    private File[] matchingJars(String fragment, File gradleSubdir) {
        return gradleSubdir.listFiles((d, name) ->
            name.startsWith(fragment + "-") && name.endsWith(".jar")
        );
    }

    private List<URL> toUrls(File[] files) {
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
