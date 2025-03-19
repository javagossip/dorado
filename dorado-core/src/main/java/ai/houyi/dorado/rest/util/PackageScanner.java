/*
 * Copyright 2017 The OpenDSP Project
 *
 * The OpenDSP Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package ai.houyi.dorado.rest.util;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import ai.houyi.dorado.exception.DoradoException;

/**
 * @author wangwp
 */
public class PackageScanner {

    public static List<Class<?>> scan(String packageName) throws ClassNotFoundException {
        List<Class<?>> classes = new LinkedList<>();
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            Enumeration<URL> urls = classLoader.getResources(packageName.replace('.', '/'));
            while (urls.hasMoreElements()) {
                URI uri = urls.nextElement().toURI();
                switch (uri.getScheme().toLowerCase()) {
                    case "jar":
                        scanFromJarProtocol(classLoader, classes, uri.getRawSchemeSpecificPart());
                        break;
                    case "file":
                        scanFromFileProtocol(classLoader, classes, new File(uri).getPath(), packageName);
                        break;
                    default:
                        throw new URISyntaxException(uri.getScheme(), "unknown schema " + uri.getScheme());
                }
            }
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
        }
        return classes;
    }

    private static Class<?> loadClass(ClassLoader loader, String classPath) {
        try {
            classPath = classPath.substring(0, classPath.length() - 6);
            return loader.loadClass(classPath);
        } catch (Throwable cause) {
            LogUtils.error("", cause);
        }
        return null;
    }

    private static void scanFromFileProtocol(ClassLoader loader,
            List<Class<?>> classes,
            String dir,
            String packageName) {
        try {
            List<String> classNames = listAllClassNames(dir);
            classNames.forEach(className -> {
                try {
                    classes.add(loadClass(loader, String.format("%s.%s", packageName, className)));
                } catch (Throwable e) {
                    LogUtils.error(e.getMessage());
                }
            });
        } catch (Exception ex) {
            throw new DoradoException(ex);
        }
    }

    private static void scanFromJarProtocol(ClassLoader loader, List<Class<?>> classes, String fullPath) {
        final String jar = fullPath.substring(0, fullPath.indexOf('!'));
        final String parent = fullPath.substring(fullPath.indexOf('!') + 2).replace("!", "");
        JarEntry e;

        JarInputStream jarReader = null;
        try {
            jarReader = new JarInputStream(URI.create(jar).toURL().openStream());
            while ((e = jarReader.getNextJarEntry()) != null) {
                String className = e.getName();
                if (!e.isDirectory() && className.startsWith(parent) && className.endsWith(Constant.CLASS_SUFFIX) &&
                        !className.contains("$")) {
                    className = className.replace('/', '.').replace("BOOT-INF.classes.", "").replace("BOOT-INF.lib", "");
                    classes.add(loadClass(loader, className));
                }
                jarReader.closeEntry();
            }
        } catch (IOException error) {
            error.printStackTrace();
        } finally {
            try {
                if (jarReader != null) {
                    jarReader.close();
                }
            } catch (IOException exp) {
                exp.printStackTrace();
            }
        }
    }

    public static List<String> listAllClassNames(String classpath) {
        List<String> classNames = new ArrayList<>();

        List<File> allClassFiles = FileUtils.listFiles(new File(classpath), Constant.CLASS_SUFFIX, true);
        int index = classpath.endsWith(File.separator) ? classpath.length() : classpath.length() + 1;

        for (File classFile : allClassFiles) {
            String className = classFile.getAbsolutePath().substring(index).replace(File.separatorChar, '.');
            classNames.add(className);
        }
        return classNames;
    }
}
