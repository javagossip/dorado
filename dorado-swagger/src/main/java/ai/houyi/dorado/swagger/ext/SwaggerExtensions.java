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
package ai.houyi.dorado.swagger.ext;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;

/**
 * 
 * @author wangwp
 */
public class SwaggerExtensions {
	private static List<SwaggerExtension> extensions = null;

    private SwaggerExtensions() {
    }

	public static List<SwaggerExtension> getExtensions() {
		return extensions;
	}

	public static void setExtensions(List<SwaggerExtension> ext) {
		extensions = ext;
	}

	public static Iterator<SwaggerExtension> chain() {
		return extensions.iterator();
	}

	static {
		extensions = new ArrayList<>();
		ServiceLoader<SwaggerExtension> loader = ServiceLoader.load(SwaggerExtension.class);
		for (SwaggerExtension ext : loader) {
			extensions.add(ext);
		}
		extensions.add(new DefaultParameterExtension());
	}
}
