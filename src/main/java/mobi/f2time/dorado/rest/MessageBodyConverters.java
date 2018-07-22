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
import java.util.concurrent.ConcurrentHashMap;

/**
 * 
 * @author wangwp
 */
public class MessageBodyConverters {
	@SuppressWarnings("rawtypes")
	private static Map<String, MessageBodyConverter> messageBodyConverterHolder = new ConcurrentHashMap<>();

	static {
		messageBodyConverterHolder.put(MimeTypes.TEXT_PLAIN, MessageBodyConverter.TEXT_PLAIN);
		messageBodyConverterHolder.put(MimeTypes.APPLICATION_JSON, MessageBodyConverter.JSON);
		messageBodyConverterHolder.put(MimeTypes.APPLICATION_PROTOBUF, MessageBodyConverter.PROTOBUF);
		messageBodyConverterHolder.put(MimeTypes.ALL, MessageBodyConverter.DEFAULT);
	}

	@SuppressWarnings("rawtypes")
	public static MessageBodyConverter getMessageBodyConverter(String produce) {
		MessageBodyConverter messageBodyConverter = messageBodyConverterHolder.get(produce);
		return messageBodyConverter == null ? MessageBodyConverter.DEFAULT : messageBodyConverter;
	}

}
