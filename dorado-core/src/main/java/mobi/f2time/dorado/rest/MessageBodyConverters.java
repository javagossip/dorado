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
package mobi.f2time.dorado.rest;

import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;

import mobi.f2time.dorado.rest.annotation.Produce;
import mobi.f2time.dorado.rest.util.StringUtils;

/**
 * 
 * @author wangwp
 */
public class MessageBodyConverters {
	@SuppressWarnings("rawtypes")
	private static Map<MediaType, MessageBodyConverter> messageBodyConverterHolder = new ConcurrentHashMap<>();

	static {
		messageBodyConverterHolder.put(MediaType.TEXT_HTML_TYPE, MessageBodyConverter.TEXT_WILDCARD);
		messageBodyConverterHolder.put(MediaType.TEXT_PLAIN_TYPE, MessageBodyConverter.TEXT_WILDCARD);
		messageBodyConverterHolder.put(MediaType.APPLICATION_JSON_TYPE, MessageBodyConverter.JSON);
		messageBodyConverterHolder.put(MediaType.APPLICATION_PROTOBUF_TYPE, MessageBodyConverter.PROTOBUF);
		messageBodyConverterHolder.put(MediaType.APPLICATION_OCTET_STREAM_TYPE, MessageBodyConverter.DEFAULT);
		messageBodyConverterHolder.put(MediaType.WILDCARD_TYPE, MessageBodyConverter.DEFAULT);

		@SuppressWarnings("rawtypes")
		ServiceLoader<MessageBodyConverter> extMessageBodyConverters = ServiceLoader.load(MessageBodyConverter.class);
		extMessageBodyConverters.forEach(converter -> {
			Produce produce = converter.getClass().getAnnotation(Produce.class);
			if (produce != null && StringUtils.isBlank(produce.value()))
				messageBodyConverterHolder.put(MediaType.valueOf(produce.value()), converter);
		});
	}

	@SuppressWarnings("rawtypes")
	public static MessageBodyConverter getMessageBodyConverter(MediaType mediaType) {
		MessageBodyConverter messageBodyConverter = messageBodyConverterHolder.get(mediaType);
		return messageBodyConverter == null ? MessageBodyConverter.DEFAULT : messageBodyConverter;
	}
}
