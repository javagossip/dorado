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
package mobi.f2time.dorado.rest.server;

import mobi.f2time.dorado.rest.util.Constant;

/**
 * 
 * @author wangwp
 */
public final class DoradoServerBuilder {
	private int backlog = Constant.DEFAULT_BACKLOG;
	private int acceptorCount = Constant.DEFAULT_ACCEPTOR_COUNT;
	private int ioWorkerCount = Constant.DEFAULT_IO_WORKER_COUNT;

	private int minWorkerThread = Constant.DEFAULT_MIN_WORKER_THREAD;
	private int maxWorkerThread = Constant.DEFAULT_MAX_WORKER_THREAD;

	private int maxConnection = Integer.MAX_VALUE;
	private int maxPendingRequest = Constant.DEFAULT_MAX_PENDING_REQUEST;
	private int maxIdleTime = Constant.DEFAULT_MAX_IDLE_TIME;

	// 以下参数用于accept到服务器的socket
	private int sendBuffer = Constant.DEFAULT_SEND_BUFFER_SIZE;
	private int recvBuffer = Constant.DEFAULT_RECV_BUFFER_SIZE;

	private int maxPacketLength = Constant.DEFAULT_MAX_PACKET_LENGTH;
	private String scanPackage;

	private final int port;

	private DoradoServerBuilder(int port) {
		this.port = port;
	}

	public static DoradoServerBuilder forPort(int port) {
		return new DoradoServerBuilder(port);
	}

	public DoradoServerBuilder backlog(int backlog) {
		this.backlog = backlog;
		return this;
	}

	public DoradoServerBuilder acceptorCount(int acceptorCount) {
		this.acceptorCount = acceptorCount;
		return this;
	}

	public DoradoServerBuilder ioWorkerCount(int ioWorkerCount) {
		this.ioWorkerCount = ioWorkerCount;
		return this;
	}

	public DoradoServerBuilder minWorkerThread(int minWorkerThread) {
		this.minWorkerThread = minWorkerThread;
		return this;
	}

	public DoradoServerBuilder maxWorkerThread(int maxWorkerThread) {
		this.maxWorkerThread = maxWorkerThread;
		return this;
	}

	public DoradoServerBuilder maxIdleTime(int maxIdleTime) {
		this.maxIdleTime = maxIdleTime;
		return this;
	}

	public DoradoServerBuilder sendBuffer(int sendBuffer) {
		this.sendBuffer = sendBuffer;
		return this;
	}

	public DoradoServerBuilder recvBuffer(int recvBuffer) {
		this.recvBuffer = recvBuffer;
		return this;
	}

	public DoradoServerBuilder maxConnection(int maxConnection) {
		this.maxConnection = maxConnection;
		return this;
	}

	public DoradoServerBuilder maxPendingRequest(int maxPendingRequest) {
		this.maxPendingRequest = maxPendingRequest;
		return this;
	}

	public DoradoServerBuilder setMaxPacketLength(int maxPacketLength) {
		this.maxPacketLength = maxPacketLength;
		return this;
	}

	public DoradoServerBuilder scanPackage(String scanPackage) {
		this.scanPackage = scanPackage;
		return this;
	}

	public int getBacklog() {
		return backlog;
	}

	public int getAcceptorCount() {
		return acceptorCount;
	}

	public int getIoWorkerCount() {
		return ioWorkerCount;
	}

	public int getMinWorkerThread() {
		return minWorkerThread;
	}

	public int getMaxWorkerThread() {
		return maxWorkerThread;
	}

	public int getMaxConnection() {
		return maxConnection;
	}

	public int getMaxPendingRequest() {
		return maxPendingRequest;
	}

	public int getMaxIdleTime() {
		return maxIdleTime;
	}

	public int getSendBuffer() {
		return sendBuffer;
	}

	public int getRecvBuffer() {
		return recvBuffer;
	}

	public int getMaxPacketLength() {
		return maxPacketLength;
	}

	public String scanPackage() {
		return this.scanPackage;
	}

	public int getPort() {
		return port;
	}

	public DoradoServer build() {
		if (scanPackage == null || Constant.BLANK_STRING.equals(scanPackage.trim())) {
			throw new IllegalArgumentException("scanPackage should not be null");
		}
		return new DoradoServer(this);
	}
}
