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
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

/**
 * @author weiping wang
 *
 */
public class ListFilesTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		File dir = new File("E:\\work_documents");
		
		
		File[] files = dir.listFiles(new PdfFileNameFilter());
		
		for(File f:files) {
			System.out.println(f.getName());
		}
		
		System.out.println("================================================");
		files = dir.listFiles();
		File[] pdfFiles = null;
		
		List<File> fileList = new ArrayList<>();
		
		for(File f:files) {
			if(f.getName().endsWith(".pdf")) {
				fileList.add(f);
				//System.out.println(f.getName());
			}
		}
		
		files = fileList.toArray(new File[] {});
		
		for(File f:files) {
			System.out.println(f.getName());
		}
		
		System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++");
		files = dir.listFiles(new PdfFileNameFilter(".pptx"));
		for(File f:files) {
			System.out.println(f.getName());
		}
		
		System.out.println("||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||");
		//使用dir.listFiles(FileFilter f)这个api完成上面相同的功能
		//dir.listFiles(filter);
	}
}
