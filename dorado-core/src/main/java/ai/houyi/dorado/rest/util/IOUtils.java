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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;

/**
 * I/O操作实用工具类
 * 
 * @author wangweiping
 * 
 */
public class IOUtils {
	/**
	 * The default buffer size to use.
	 */
	private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;

	private IOUtils() {
	}

	public static String toString(InputStream in) {
		StringWriter writer = new StringWriter();
		InputStreamReader inputStreamReader = new InputStreamReader(in);
		try {
			copyLarge(inputStreamReader, writer);
			return writer.toString();
		} catch (IOException ex) {
			LogUtils.error("", ex);
		} finally {
			closeQuietly(inputStreamReader);
		}
		return null;
	}

	public static String toString(InputStream in, String encoding) {
		if (encoding == null) {
			return toString(in);
		}
		StringWriter writer = new StringWriter();
		InputStreamReader reader = null;
		try {
			reader = new InputStreamReader(in, encoding);
			copyLarge(reader, writer);
			return writer.toString();
		} catch (IOException ex) {
			LogUtils.error("", ex);
		} finally {
			closeQuietly(reader);
		}
		return null;
	}

	public static void closeQuietly(Reader reader) {
		try {
			if (reader != null) {
				reader.close();
			}
		} catch (IOException ex) {
			// ignore ex
		}
	}

	public static void closeQuietly(Writer writer) {
		try {
			if (writer != null) {
				writer.close();
			}
		} catch (IOException ex) {
			// ignore ex
		}
	}

	public static void closeQuietly(InputStream in) {
		try {
			if (in != null) {
				in.close();
			}
		} catch (IOException ex) {
			// ignore ex
		}
	}

	public static void closeQuietly(OutputStream out) {
		try {
			if (out != null) {
				out.close();
			}
		} catch (IOException ex) {
			// ignore ex
		}
	}

	/**
	 * Copy chars from a large (over 2GB) <code>Reader</code> to a
	 * <code>Writer</code>.
	 * <p>
	 * This method buffers the input internally, so there is no need to use a
	 * <code>BufferedReader</code>.
	 * 
	 * @param input
	 *            the <code>Reader</code> to read from
	 * @param output
	 *            the <code>Writer</code> to write to
	 * @return the number of characters copied
	 * @throws NullPointerException
	 *             if the input or output is null
	 * @throws IOException
	 *             if an I/O error occurs
	 * @since Commons IO 1.3
	 */
	public static long copyLarge(Reader input, Writer output) throws IOException {
		char[] buffer = new char[DEFAULT_BUFFER_SIZE];
		long count = 0;
		int n = 0;
		while (-1 != (n = input.read(buffer))) {
			output.write(buffer, 0, n);
			count += n;
		}
		return count;
	}

	public static byte[] readBytes(InputStream input) {
		try {
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
			int n = 0;
			while (-1 != (n = input.read(buffer))) {
				output.write(buffer, 0, n);
			}
			return output.toByteArray();
		} catch (Exception ex) {
			// ignore this ex
		}
		return null;
	}
}
