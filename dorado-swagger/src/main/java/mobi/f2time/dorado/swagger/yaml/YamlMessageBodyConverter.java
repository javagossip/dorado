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
package mobi.f2time.dorado.swagger.yaml;

import java.io.IOException;
import java.io.InputStream;

import com.fasterxml.jackson.core.JsonProcessingException;

import io.swagger.util.Yaml;
import mobi.f2time.dorado.rest.MessageBodyConverter;
import mobi.f2time.dorado.rest.annotation.Produce;
import mobi.f2time.dorado.rest.util.LogUtils;

/**
 * 
 * @author wangwp
 */
@Produce("application/yaml")
public class YamlMessageBodyConverter implements MessageBodyConverter<Object> {

	@Override
	public byte[] writeMessageBody(Object t) {
		try {
			return Yaml.mapper().writeValueAsBytes(t);
		} catch (JsonProcessingException ex) {
			LogUtils.error(ex.getMessage(), ex);
		}
		return null;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Object readMessageBody(InputStream in, Class clazz) {
		try {
			return Yaml.mapper().readValue(in, clazz);
		} catch (IOException ex) {
			LogUtils.error(ex.getMessage(), ex);
		}
		return null;
	}
}
