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

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import ai.houyi.dorado.rest.controller.DoradoStatus;

/**
 * @author weiping wang
 *
 */
public class TracingThreadPoolExecutor extends ThreadPoolExecutor {
	private final AtomicInteger pendingTasks = new AtomicInteger();
	private final DoradoStatus serverStatus = DoradoStatus.get();

	public TracingThreadPoolExecutor(int corePoolSize, int maximumPoolSize, 
			BlockingQueue<Runnable> workQueue) {
		super(corePoolSize, maximumPoolSize, 0L, TimeUnit.MILLISECONDS, workQueue);
		setThreadFactory(new NamedThreadFactory("dorado-worker", true));
		serverStatus.workerPool(this);
	}

	@Override
	public void execute(Runnable command) {
		super.execute(command);
	}

	@Override
	protected void beforeExecute(Thread t, Runnable r) {
		serverStatus.incrPendingRequests();
	}

	@Override
	protected void afterExecute(Runnable r, Throwable t) {
		serverStatus.decrPendingRequests();
	}

	public int getPendingTasks() {
		return pendingTasks.get();
	}
}
