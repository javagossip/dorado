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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * 
 * @author wangwp
 */
public final class FileUtils {

	public static List<File> listFiles(final File dir, final String suffix, final boolean recursive) {
		List<File> files = new ArrayList<>();
		if (!dir.isDirectory())
			return files;

		File[] childrenFiles = dir.listFiles(f -> f.isDirectory() || (f.isFile() && f.getName().endsWith(suffix)));

		for (File childFile : childrenFiles) {
			if (childFile.isFile())
				files.add(childFile);
			files.addAll(listFiles(childFile, suffix, recursive));
		}
		return files;
	}

	public static List<Path> recurseListDirs(Path root) throws IOException {
		final List<java.nio.file.Path> results = new ArrayList<>();

		try (Stream<java.nio.file.Path> pathStream = Files.walk(root)) {
			pathStream.filter(p -> p.toFile().isDirectory()).forEach(p -> results.add(p));
		} catch (IOException ex) {
			throw ex;
		}
		return results;
	}

	public static void main(String[] args) throws Exception {
		System.out.println(recurseListDirs(Paths.get(ClassLoaderUtils.getPath(""))));
	}
}
