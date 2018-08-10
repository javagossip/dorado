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

package mobi.f2time.dorado.springboot;

import org.springframework.boot.context.properties.ConfigurationProperties;

import mobi.f2time.dorado.rest.util.Constant;

/**
 * @author weiping wang
 *
 */
@ConfigurationProperties("dorado")
public class DoradoConfig {
	private int port = 18888;
	
	private int backlog = Constant.DEFAULT_BACKLOG;
	private int acceptors = Constant.DEFAULT_ACCEPTOR_COUNT;
	private int ioWorkers = Constant.DEFAULT_IO_WORKER_COUNT;

	private int minWorkers = Constant.DEFAULT_MIN_WORKER_THREAD;
	private int maxWorkers = Constant.DEFAULT_MAX_WORKER_THREAD;

	private int maxConnections = 100000;
	private int maxPendingRequest = Constant.DEFAULT_MAX_PENDING_REQUEST;
	private int maxIdleTime = 8 * 3600;

	private int sendBuffer = Constant.DEFAULT_SEND_BUFFER_SIZE;
	private int recvBuffer = Constant.DEFAULT_RECV_BUFFER_SIZE;
	private int maxPacketLength = Constant.DEFAULT_MAX_PACKET_LENGTH;

	private String[] scanPackages;

	public int getBacklog() {
		return backlog;
	}

	public void setBacklog(int backlog) {
		this.backlog = backlog;
	}

	public int getAcceptors() {
		return acceptors;
	}

	public String[] getScanPackages() {
		return scanPackages;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public void setScanPackages(String[] scanPackages) {
		this.scanPackages = scanPackages;
	}

	public void setAcceptors(int acceptors) {
		this.acceptors = acceptors;
	}

	public int getIoWorkers() {
		return ioWorkers;
	}

	public void setIoWorkers(int ioWorkers) {
		this.ioWorkers = ioWorkers;
	}

	public int getMinWorkers() {
		return minWorkers;
	}

	public void setMinWorkers(int minWorkers) {
		this.minWorkers = minWorkers;
	}

	public int getMaxWorkers() {
		return maxWorkers;
	}

	public void setMaxWorkers(int maxWorkers) {
		this.maxWorkers = maxWorkers;
	}

	public int getMaxConnections() {
		return maxConnections;
	}

	public void setMaxConnections(int maxConnections) {
		this.maxConnections = maxConnections;
	}

	public int getMaxPendingRequest() {
		return maxPendingRequest;
	}

	public void setMaxPendingRequest(int maxPendingRequest) {
		this.maxPendingRequest = maxPendingRequest;
	}

	public int getMaxIdleTime() {
		return maxIdleTime;
	}

	public void setMaxIdleTime(int maxIdleTime) {
		this.maxIdleTime = maxIdleTime;
	}

	public int getSendBuffer() {
		return sendBuffer;
	}

	public void setSendBuffer(int sendBuffer) {
		this.sendBuffer = sendBuffer;
	}

	public int getRecvBuffer() {
		return recvBuffer;
	}

	public void setRecvBuffer(int recvBuffer) {
		this.recvBuffer = recvBuffer;
	}

	public int getMaxPacketLength() {
		return maxPacketLength;
	}

	public void setMaxPacketLength(int maxPacketLength) {
		this.maxPacketLength = maxPacketLength;
	}

	@Override
	public String toString() {
		return "DoradoProperties [backlog=" + backlog + ", acceptors=" + acceptors + ", ioWorkers=" + ioWorkers
				+ ", minWorkers=" + minWorkers + ", maxWorkers=" + maxWorkers + ", maxConnections=" + maxConnections
				+ ", maxPendingRequest=" + maxPendingRequest + ", maxIdleTime=" + maxIdleTime + ", sendBuffer="
				+ sendBuffer + ", recvBuffer=" + recvBuffer + ", maxPacketLength=" + maxPacketLength + "]";
	}

}
