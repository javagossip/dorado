/*
 * Copyright 2017 The OpenAds Project
 *
 * The OpenAds Project licenses this file to you under the Apache License,
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
package ai.houyi.dorado.example;

import java.io.File;
import java.io.FilenameFilter;

/**
 * @author weiping wang
 *
 */
public class PdfFileNameFilter implements FilenameFilter {
	private String suffix;

	public PdfFileNameFilter() {
		this(".pdf");
	}

	public PdfFileNameFilter(String suffix) {
		this.suffix = suffix;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.FilenameFilter#accept(java.io.File, java.lang.String)
	 */
	@Override
	public boolean accept(File dir, String name) {
		return name.endsWith(this.suffix);
	}

}
