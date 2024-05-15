/*
 * Copyright 2012 The OpenDSP Project
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
package ai.houyi.dorado.swagger;

import ai.houyi.dorado.Dorado;
import ai.houyi.dorado.rest.ResourceRegister;
import ai.houyi.dorado.rest.router.trie.UriRoutingRegistry;
import ai.houyi.dorado.swagger.controller.SwaggerV2Controller;

/**
 * @author weiping wang
 *
 */
public class SwaggerResourceRegister implements ResourceRegister {

	@Override
	public void register() {
		Class<?> mainClass = Dorado.mainClass;
		EnableSwagger enableSwagger = mainClass.getAnnotation(EnableSwagger.class);

		//如果不启用swagger,则不需要注册swagger相关服务
		if (enableSwagger == null)
			return;
		UriRoutingRegistry.getInstance().register(SwaggerV2Controller.class);
	}

}
