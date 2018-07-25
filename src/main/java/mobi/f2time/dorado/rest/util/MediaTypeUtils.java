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

import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;

import com.google.protobuf.Message;

import io.netty.util.CharsetUtil;
import mobi.f2time.dorado.rest.MediaType;

/**
 * 
 * @author wangwp
 */
public final class MediaTypeUtils {

	public static MediaType forType(Class<?> type) {
		if (ClassUtils.isPrimitiveOrWrapper(type) || String.class == type 
				|| BigDecimal.class == type
				|| BigInteger.class == type) {
			return MediaType.TEXT_HTML_TYPE.withCharset(CharsetUtil.UTF_8.name());
		}

		if (byte[].class == type || InputStream.class.isAssignableFrom(type)) {
			return MediaType.APPLICATION_OCTET_STREAM_TYPE;
		}

		if (Message.class.isAssignableFrom(type)) {
			return MediaType.APPLICATION_PROTOBUF_TYPE;
		}

		return MediaType.APPLICATION_JSON_TYPE.withCharset(CharsetUtil.UTF_8.name());
	}

	public static MediaType defaultForType(Class<?> type, String defaultType) {
		if (MediaType.WILDCARD.equals(defaultType)) {
			return forType(type);
		}
		return MediaType.valueOf(defaultType);
	}
}
