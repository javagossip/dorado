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

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;

import ai.houyi.dorado.exception.DoradoException;

/**
 * 
 * @author wangwp
 */
public class ProtobufMessageDescriptors {
	private static final Map<Class<?>, ProtobufMessageDescriptor> messageDescriptorHolder = new ConcurrentHashMap<>();

	public static void registerMessageDescriptor(ProtobufMessageDescriptor messageDescriptor) {
		messageDescriptorHolder.put(messageDescriptor.messageType, messageDescriptor);
	}

	public static ProtobufMessageDescriptor messageDescriptorForType(Class<?> type) {
		return messageDescriptorHolder.get(type);
	}

	public static void registerMessageDescriptorForType(Class<?> type) {
		messageDescriptorHolder.put(type, new ProtobufMessageDescriptor(type));
	}

	public static Message newMessageForType(byte[] data, Class<?> type) {
		ProtobufMessageDescriptor messageDescriptor = messageDescriptorHolder.get(type);
		if (messageDescriptor == null) {
			return null;
		}
		return messageDescriptor.mergeFrom(data);
	}

	public static Message newMessageForType(InputStream in, Class<?> type) {
		ProtobufMessageDescriptor messageDescriptor = messageDescriptorHolder.get(type);
		if (messageDescriptor == null) {
			return null;
		}
		return messageDescriptor.mergeFrom(in);
	}

	public static class ProtobufMessageDescriptor {
		private static final String METHOD_GET_DEFAULT_INSTANCE = "getDefaultInstance";

		private Class<?> messageType;
		private Message defaultMessageInstance;

		public ProtobufMessageDescriptor(Class<?> messageType) throws DoradoException {
			this.messageType = messageType;

			try {
				Method method = messageType.getMethod(METHOD_GET_DEFAULT_INSTANCE, (Class[]) null);
				if (!method.isAccessible()) {
					method.setAccessible(true);
				}
				defaultMessageInstance = (Message) method.invoke(messageType, (Object[]) null);
			} catch (Throwable ex) {
				throw new DoradoException(ex);
			}
		}

		public Message mergeFrom(byte[] data) {
			try {
				return defaultMessageInstance.newBuilderForType().mergeFrom(data).build();
			} catch (InvalidProtocolBufferException ex) {
				throw new DoradoException(ex);
			}
		}

		public Message mergeFrom(InputStream in) {
			try {
				return defaultMessageInstance.newBuilderForType().mergeFrom(in).build();
			} catch (IOException ex) {
				throw new DoradoException(ex);
			}
		}

		public Class<?> getMessageType() {
			return this.messageType;
		}
	}
}
