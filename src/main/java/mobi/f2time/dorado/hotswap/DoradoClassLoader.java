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
package mobi.f2time.dorado.hotswap;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

import mobi.f2time.dorado.rest.util.Constant;
import mobi.f2time.dorado.rest.util.IOUtils;
import mobi.f2time.dorado.rest.util.PackageScanner;

/**
 * hotswap classloader, for dev mode
 * 
 * @author wangwp
 */
public class DoradoClassLoader extends ClassLoader {
	private final List<String> hotswappedClassNames;
	private static ClassLoader parent;

	static {
		try {
			parent = DoradoClassLoader.class.getClassLoader();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public DoradoClassLoader() {
		String classpath = parent.getResource("").getPath();
		this.hotswappedClassNames = PackageScanner.listAllClassNames(classpath);
	}

	@Override
	public Class<?> loadClass(String name) throws ClassNotFoundException {
		Class<?> c = findLoadedClass(name);
		if (c != null)
			return c;
		
		if (hotswappedClassNames.contains(name)) {
			String file = parent.getResource("").getPath() + File.separator + name.replace('.', '/')
					+ Constant.CLASS_SUFFIX;
			byte[] data = readBytesFromFile(file);
			return defineClass(name, data, 0, data.length);
		} else {
			return super.loadClass(name);
		}
	}

	private byte[] readBytesFromFile(String file) {
		try {
			return IOUtils.readBytes(new FileInputStream(file));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
}
