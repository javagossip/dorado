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
package ai.houyi.dorado.rest;

import java.io.InputStream;
import java.lang.reflect.Type;

import com.google.protobuf.Message;

import ai.houyi.dorado.exception.DoradoException;
import ai.houyi.dorado.rest.util.IOUtils;
import ai.houyi.dorado.rest.util.SerializeUtils;
import io.netty.util.CharsetUtil;

/**
 * 
 * @author wangwp
 */
public interface MessageBodyConverter<T> {
	byte[] writeMessageBody(T t);

	T readMessageBody(InputStream in, Type clazz);

	MessageBodyConverter<? extends Object> JSON = new MessageBodyConverter<Object>() {
		@Override
		public byte[] writeMessageBody(Object t) {
			if (t.getClass() == String.class) {
				return ((String) t).getBytes(CharsetUtil.UTF_8);
			} else if (t.getClass() == byte[].class) {
				return (byte[]) t;
			}
			return com.alibaba.fastjson2.JSON.toJSONBytes(t);
		}

		@Override
		public Object readMessageBody(InputStream in, Type type) {
            return com.alibaba.fastjson2.JSON.parseObject(IOUtils.toString(in, CharsetUtil.UTF_8.name()), type);
		}
	};

	@SuppressWarnings("rawtypes")
	MessageBodyConverter TEXT_WILDCARD = new MessageBodyConverter<Object>() {
		@Override
		public byte[] writeMessageBody(Object t) {
			return t.toString().getBytes(CharsetUtil.UTF_8);
		}

		@Override
		public Object readMessageBody(InputStream in, Type type) {
			return IOUtils.toString(in, CharsetUtil.UTF_8.name());
		}
	};

	@SuppressWarnings("rawtypes")
	MessageBodyConverter PROTOBUF = new MessageBodyConverter<Message>() {
		@Override
		public byte[] writeMessageBody(Message t) {
			return t.toByteArray();
		}

		@Override
		public Message readMessageBody(InputStream in, Type type) {
			try {
				return (Message) ObjectSerializer.PROTOBUF.deserialize(in, type);
			} catch (Exception ex) {
				throw new DoradoException(ex);
			}
		}
	};

	@SuppressWarnings("rawtypes")
	MessageBodyConverter DEFAULT = new MessageBodyConverter<Object>() {
		@Override
		public byte[] writeMessageBody(Object t) {
			return SerializeUtils.serialize(t);
		}

		@Override
		public Object readMessageBody(InputStream in, Type type) {
			return SerializeUtils.deserialize(in, type);
		}
	};
}
