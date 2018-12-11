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
package ai.houyi.dorado.rest.http;

/**
 * @author weiping wang
 *
 */
public interface ControllerMethodReturnValueHandler {
	/**
	 * 方法调用正常返回结果处理器
	 * 
	 * @param value 方法执行结果
	 * @return 处理后的返回结果
	 */
	Object handleMethodReturnValue(Object value);

	/**
	 * 异常统一处理器
	 * 
	 * @param exception 方法执行抛出异常
	 * @return 包装异常为自定义的响应实体对象
	 */
	Object handleException(Exception exception, HttpResponse response);
}
