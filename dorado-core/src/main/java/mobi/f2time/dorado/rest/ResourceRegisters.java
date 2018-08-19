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
package mobi.f2time.dorado.rest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ServiceLoader;

/**
 * @author weiping wang <javagossip@gmail.com>
 *
 */
public class ResourceRegisters {
	private static ResourceRegisters instance = new ResourceRegisters();
	
	private static List<ResourceRegister> resourceRegisters = new ArrayList<>();
	
	static {
		ServiceLoader<ResourceRegister> serviceLoader = ServiceLoader.load(ResourceRegister.class);
		for(ResourceRegister register: serviceLoader) {
			resourceRegisters.add(register);
		}
	}
	
	public static ResourceRegisters getInstance() {
		return instance;
	}
	
	public List<ResourceRegister> getResourceRegisters(){
		return Collections.unmodifiableList(resourceRegisters);
	}
	
	public void addResourceRegister(ResourceRegister resourceRegister) {
		resourceRegisters.add(resourceRegister);
	}
}
