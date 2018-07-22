/*
 * This is  a part of the Video Resource System(VRS).
 * Copyright (C) 2010-2011 iqiyi.com Corporation
 * All rights reserved.
 *
 * Licensed under the iqiyi.com private License.
 */
package mobi.f2time.dorado.rest.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * I/O操作实用工具类
 * 
 * @author wangweiping
 * 
 */
public class IOUtils {
	private static final Logger logger = LoggerFactory.getLogger(IOUtils.class);
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
			logger.error("", ex);
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
			logger.error("", ex);
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
}
