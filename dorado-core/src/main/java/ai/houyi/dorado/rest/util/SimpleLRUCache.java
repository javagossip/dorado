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

import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 一个简单的LRU cache实现，不支持过期操作
 * 
 * @author wangwp
 */
public final class SimpleLRUCache<K, V> {
	private final LinkedHashMap<K, V> cache;
	private final Lock r;
	private final Lock w;

	private SimpleLRUCache(int capacity) {
		this.cache = new LRUMap<K, V>(capacity);
		ReadWriteLock lock = new ReentrantReadWriteLock();

		this.r = lock.readLock();
		this.w = lock.writeLock();
	}

	public static <K, V> SimpleLRUCache<K, V> create(int capacity) {
		return new SimpleLRUCache<K, V>(capacity);
	}

	public void put(K key, V value) {
		w.lock();
		try {
			this.cache.put(key, value);
		} finally {
			w.unlock();
		}
	}

	public V get(K key) {
		r.lock();
		try {
			return this.cache.get(key);
		} finally {
			r.unlock();
		}
	}

	public void remove(K key) {
		w.lock();
		try {
			this.cache.remove(key);
		} finally {
			w.unlock();
		}
	}

	public void clear() {
		w.lock();
		try {
			this.cache.clear();
		} finally {
			w.unlock();
		}
	}

	static class LRUMap<K, V> extends LinkedHashMap<K, V> {
		private static final long serialVersionUID = 1L;
		private final int capacity;

		public LRUMap(int capacity) {
			super((int) Math.ceil(capacity / 0.75) + 1, 0.75f, true);
			this.capacity = capacity;

		}

		@Override
		protected boolean removeEldestEntry(Entry<K, V> eldest) {
			return size() > capacity;
		}
	}
}
