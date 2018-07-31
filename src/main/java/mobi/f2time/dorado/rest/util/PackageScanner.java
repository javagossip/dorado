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
package mobi.f2time.dorado.rest.util;

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

/**
 * 
 * @author wangwp
 */
public class PackageScanner {

	public static List<Class<?>> scanClassesWithClasspath(String classpath) throws Exception {
		ClassLoader cl = Thread.currentThread().getContextClassLoader();

		List<Class<?>> allClasses = new ArrayList<>();
		List<File> allClassFiles = FileUtils.listFiles(new File(classpath), ".class", true);
		for (File classFile : allClassFiles) {
			String className = classFile.getAbsolutePath().substring(classpath.length()).replace('/', '.');
			allClasses.add(loadClass(cl, className));
		}
		return allClasses;
	}

	public static List<Class<?>> scan(String packageName) throws ClassNotFoundException {

		List<Class<?>> classes = new LinkedList<>();

		try {
			ClassLoader loader = Thread.currentThread().getContextClassLoader();
			Enumeration<URL> urls = loader.getResources(packageName.replace('.', '/'));
			while (urls.hasMoreElements()) {
				URI uri = urls.nextElement().toURI();
				switch (uri.getScheme().toLowerCase()) {
				case "jar":
					scanFromJarProtocol(loader, classes, uri.getRawSchemeSpecificPart());
					break;
				case "file":
					scanFromFileProtocol(loader, classes, uri.getPath(), packageName);
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

	private static Class<?> loadClass(ClassLoader loader, String classPath) throws ClassNotFoundException {
		classPath = classPath.substring(0, classPath.length() - 6);
		return loader.loadClass(classPath);
	}

	private static void scanFromFileProtocol(ClassLoader loader, List<Class<?>> classes, String dir, String packageName)
			throws ClassNotFoundException {
		File directory = new File(dir);
		File[] files = directory.listFiles();
		if (directory.isDirectory() && files != null) {
			for (File classFile : files) {
				if (!classFile.isDirectory() && classFile.getName().endsWith(".class")
						&& !classFile.getName().contains("$")) {
					String className = String.format("%s.%s", packageName, classFile.getName());
					classes.add(loadClass(loader, className));
				}
			}
		}
	}

	private static void scanFromJarProtocol(ClassLoader loader, List<Class<?>> classes, String fullPath)
			throws ClassNotFoundException {
		final String jar = fullPath.substring(0, fullPath.lastIndexOf('!'));
		final String parent = fullPath.substring(fullPath.lastIndexOf('!') + 2);
		JarEntry e = null;

		JarInputStream jarReader = null;
		try {
			jarReader = new JarInputStream(new URL(jar).openStream());
			while ((e = jarReader.getNextJarEntry()) != null) {
				String className = e.getName();
				if (!e.isDirectory() && className.startsWith(parent) && className.endsWith(".class")
						&& !className.contains("$")) {
					className = className.replace('/', '.');
					classes.add(loadClass(loader, className));
				}
				jarReader.closeEntry();
			}
		} catch (IOException error) {
			error.printStackTrace();
		} finally {
			try {
				if (jarReader != null)
					jarReader.close();
			} catch (IOException exp) {
				exp.printStackTrace();
			}
		}
	}
}
