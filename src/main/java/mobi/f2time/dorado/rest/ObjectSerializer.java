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

import java.io.IOException;
import java.io.InputStream;

import com.alibaba.fastjson.JSONObject;
import com.google.protobuf.Message;

import io.netty.util.CharsetUtil;
import mobi.f2time.dorado.rest.util.IOUtils;
import mobi.f2time.dorado.rest.util.ProtobufMessageDescriptors;

/**
 * 
 * @author wangwp
 */
public interface ObjectSerializer {
	byte[] serialize(Object t);

	@SuppressWarnings("rawtypes")
	Object deserialize(InputStream in, Class type);

	ObjectSerializer JSON = new ObjectSerializer() {
		@Override
		public byte[] serialize(Object t) {
			return com.alibaba.fastjson.JSON.toJSONString(t).getBytes(CharsetUtil.UTF_8);
		}

		@SuppressWarnings({ "rawtypes", "unchecked" })
		@Override
		public Object deserialize(InputStream in, Class type) {
			String text = IOUtils.toString(in, CharsetUtil.UTF_8.name());
			return JSONObject.parseObject(text, type);
		}
	};

	ObjectSerializer PROTOBUF = new ObjectSerializer() {
		@Override
		public byte[] serialize(Object t) {
			Message message = (Message) t;
			return message.toByteArray();
		}

		@SuppressWarnings("rawtypes")
		@Override
		public Object deserialize(InputStream in, Class type) {
			return ProtobufMessageDescriptors.newMessageForType(in, type);
		}
	};

	ObjectSerializer DEFAULT = new ObjectSerializer() {
		@Override
		public byte[] serialize(Object t) {
			return com.alibaba.fastjson.JSON.toJSONBytes(t);
		}

		@SuppressWarnings("rawtypes")
		@Override
		public Object deserialize(InputStream in, Class type) {
			try {
				return JSONObject.parseObject(in, type);
			} catch (IOException ex) {
				//ignore exception
			}
			return null;
		}
	};
}
