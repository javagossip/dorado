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
package ai.houyi.dorado.rest.controller;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import com.alibaba.fastjson.annotation.JSONField;

import ai.houyi.dorado.rest.util.TracingThreadPoolExecutor;

/**
 * 
 * @author wangwp
 */
public class DoradoStatus {
	private static DoradoStatus INSTANCE = new DoradoStatus();

	private AtomicInteger connections = new AtomicInteger(0);
	private AtomicInteger pendingRequests = new AtomicInteger(0);
	private AtomicLong totalRequests = new AtomicLong(0);
	private AtomicLong handledRequests = new AtomicLong(0);
	private int workerPoolSize;
	private int activePoolSize;

	@JSONField(serialize = false)
	private TracingThreadPoolExecutor workerPool;

	private DoradoStatus() {
	}

	public static DoradoStatus get() {
		return INSTANCE;
	}

	public DoradoStatus totalRequestsIncrement() {
		totalRequests.incrementAndGet();
		return this;
	}

	public DoradoStatus handledRequestsIncrement() {
		handledRequests.incrementAndGet();
		return this;
	}

	public DoradoStatus connectionIncrement() {
		connections.incrementAndGet();
		return this;
	}

	public DoradoStatus connectionDecrement() {
		connections.decrementAndGet();
		return this;
	}

	public DoradoStatus incrPendingRequests() {
		pendingRequests.incrementAndGet();
		return this;
	}

	public DoradoStatus decrPendingRequests() {
		pendingRequests.decrementAndGet();
		return this;
	}

	public int getConnections() {
		return connections.get();
	}

	public int getPendingRequests() {
		return pendingRequests.get();
	}

	public long getTotalRequests() {
		return totalRequests.get();
	}

	public long getHandledRequests() {
		return handledRequests.get();
	}

	public int getWorkerPoolSize() {
		this.workerPoolSize = workerPool.getPoolSize();
		return workerPoolSize;
	}

	public int getActivePoolSize() {
		this.activePoolSize = workerPool.getActiveCount();
		return activePoolSize;
	}

	public void workerPool(TracingThreadPoolExecutor workerPool) {
		this.workerPool = workerPool;
	}
}
