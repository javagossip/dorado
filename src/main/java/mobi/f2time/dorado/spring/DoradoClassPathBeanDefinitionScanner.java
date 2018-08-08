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
package mobi.f2time.dorado.spring;

import java.io.IOException;

import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import mobi.f2time.dorado.rest.annotation.Controller;

/**
 * 
 * @author wangwp
 */
public class DoradoClassPathBeanDefinitionScanner extends ClassPathBeanDefinitionScanner {

	public DoradoClassPathBeanDefinitionScanner(BeanDefinitionRegistry registry, boolean useDefaultFilters,
			Environment environment, ResourceLoader resourceLoader) {
		super(registry, useDefaultFilters, environment, resourceLoader);
	}

	public DoradoClassPathBeanDefinitionScanner(BeanDefinitionRegistry registry, boolean useDefaultFilters,
			Environment environment) {
		super(registry, useDefaultFilters, environment);
	}

	public DoradoClassPathBeanDefinitionScanner(BeanDefinitionRegistry registry, boolean useDefaultFilters) {
		super(registry, useDefaultFilters);
	}

	public DoradoClassPathBeanDefinitionScanner(BeanDefinitionRegistry registry) {
		super(registry);
	}

	@Override
	protected void registerDefaultFilters() {
		// super.registerDefaultFilters();
		this.addIncludeFilter(new AnnotationTypeFilter(Controller.class));
	}

	@Override
	protected boolean isCandidateComponent(MetadataReader metadataReader) throws IOException {
		return super.isCandidateComponent(metadataReader);
	}

	@Override
	protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
		return super.isCandidateComponent(beanDefinition)
				&& beanDefinition.getMetadata().hasAnnotation(Controller.class.getName());
	}

}
