/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.hippo4j.core.springboot.starter.refresher;

import cn.hippo4j.core.springboot.starter.config.BootstrapConfigProperties;
import com.alibaba.nacos.api.annotation.NacosInjected;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.Executor;

/**
 * Nacos refresher handler.
 */
@Slf4j
public class NacosRefresherHandler extends AbstractCoreThreadPoolDynamicRefresh {

    @NacosInjected
    private ConfigService configService;

    public NacosRefresherHandler(BootstrapConfigProperties bootstrapConfigProperties) {
        super(bootstrapConfigProperties);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Map<String, String> nacosConfig = bootstrapConfigProperties.getNacos();

        configService.addListener(nacosConfig.get("data-id"), nacosConfig.get("group"),
                new Listener() {

                    @Override
                    public Executor getExecutor() {
                        return dynamicRefreshExecutorService;
                    }

                    @Override
                    public void receiveConfigInfo(String configInfo) {
                        dynamicRefresh(configInfo);
                    }
                });
        log.info("Dynamic thread pool refresher, add nacos listener success. data-id: {}, group: {}", nacosConfig.get("data-id"), nacosConfig.get("group"));

    }
}
