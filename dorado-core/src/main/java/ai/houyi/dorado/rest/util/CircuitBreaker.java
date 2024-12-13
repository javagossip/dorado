/*
 *
 *  * Copyright 2017 The OpenDSP Project
 *  *
 *  * The OpenDSP Project licenses this file to you under the Apache License,
 *  * version 2.0 (the "License"); you may not use this file except in compliance
 *  * with the License. You may obtain a copy of the License at:
 *  *
 *  *   http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 *  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 *  * License for the specific language governing permissions and limitations
 *  * under the License.
 *
 */

package ai.houyi.dorado.rest.util;

import java.util.LinkedList;

//一个简单的熔断器
public class CircuitBreaker {

    private int failureThreshold;
    private int failureCount;
    private int resetTimeout;
    private long lastOpenTime;
    //5s窗口
    private int timeWindowSize = 5000;
    private LinkedList<Request> timeWindow;

    private class Request {

        boolean success;
        long time;

        public Request(boolean success) {
            this.success = success;
            this.time = System.currentTimeMillis();
        }
    }

    private State state;

    enum State {
        OPEN,
        CLOSED,
        HALF_OPEN
    }

    public CircuitBreaker(int failureThreshold, int resetTimeout, int timeWindowSize) {
        this.failureThreshold = failureThreshold;
        this.resetTimeout = resetTimeout;
        this.state = State.CLOSED;
        this.timeWindowSize = timeWindowSize;
        this.timeWindow = new LinkedList<>();
    }

    public boolean isOpen() {
        return state == State.OPEN;
    }

    public boolean isClosed() {
        return state == State.CLOSED;
    }

    public boolean isHalfOpen() {
        return state == State.HALF_OPEN;
    }

    public synchronized void recordRequest(boolean success) {
        //核心代码，这里记录请求成功或者失败，以及熔断器状态变更，需要滑动窗口机制来统计请求成功和失败数据
        clearTimeWindow();
        if (success) {
            timeWindow.add(new Request(true));
            if(state == State.HALF_OPEN){
                state = State.CLOSED;
            }
        } else {
            failureCount++;
            timeWindow.add(new Request(false));
            if(state == State.HALF_OPEN){
                state = State.OPEN;
            }
            if (failureCount >= failureThreshold) {
                //如果失败次数达到阈值，则熔断器打开
                state = State.OPEN;
                this.lastOpenTime = System.currentTimeMillis();
            }
        }
    }


    private void clearTimeWindow() {
        long now = System.currentTimeMillis();
        while (!timeWindow.isEmpty()) {
            Request request = timeWindow.peek();
            if (now - request.time > timeWindowSize) {
                timeWindow.poll();
                if (!request.success) {
                    failureCount--;
                }
            } else {
                break;
            }
        }
    }

    public void call(Runnable remoteServiceRunnable) {
        if (isOpen()) {
            //熔断器打开，直接抛出异常
            //如果熔断器已经打开超过resetTimeout时间，则熔断器进入半开状态
            if (System.currentTimeMillis() - lastOpenTime > resetTimeout) {
                this.state = State.HALF_OPEN;
            }
            throw new RuntimeException("CircuitBreaker is open");
        } else {
            //熔断器关闭，直接调用远程服务
            try {
                remoteServiceRunnable.run();
                recordRequest(true);
            } catch (Throwable ex) {
                recordRequest(false);
            }
        }
    }
}
