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
package mobi.f2time.dorado.rest.util;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

/**
 * 
 * @author wangwp
 */
public class SimpleLRUCacheTest {

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * Test method for {@link mobi.f2time.dorado.rest.util.SimpleLRUCache#create(int)}.
	 */
	@Test
	public void testCreate() {
		SimpleLRUCache<String, String> cache = SimpleLRUCache.create(2);
		assertNotNull(cache);
	}

	/**
	 * Test method for {@link mobi.f2time.dorado.rest.util.SimpleLRUCache#put(java.lang.Object, java.lang.Object)}.
	 */
	@Test
	public void testPut() {
		SimpleLRUCache<String, String> cache = SimpleLRUCache.create(2);
		cache.put("k1", "v1");
		cache.put("k2", "v2");
		
		cache.get("k1");
		cache.get("k1");
		
		cache.put("k3", "v3");
		
		assertNotNull(cache.get("k1"));
		assertNull(cache.get("k2"));
	}

	/**
	 * Test method for {@link mobi.f2time.dorado.rest.util.SimpleLRUCache#get(java.lang.Object)}.
	 */
	@Test
	public void testGet() {
		SimpleLRUCache<String, String> cache = SimpleLRUCache.create(2);
		cache.put("k1", "v1");
		cache.put("k2", "v2");
		
		cache.get("k2");
		cache.get("k2");
		cache.get("k1");
		
		cache.put("k3", "v3");
		assertNotNull(cache.get("k3"));
		assertNotNull(cache.get("k1"));
		assertNull(cache.get("k2"));
	}
}
