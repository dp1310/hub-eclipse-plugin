/**
 * hub-eclipse-plugin
 *
 * Copyright (C) 2017 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.blackducksoftware.integration.eclipseplugin.internal;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;

import com.blackducksoftware.integration.eclipseplugin.common.services.DependencyInformationService;
import com.blackducksoftware.integration.eclipseplugin.views.providers.utils.ComponentModel;
import com.blackducksoftware.integration.exception.IntegrationException;
import com.blackducksoftware.integration.hub.buildtool.Gav;

public class ComponentCache {

    private final int TTL_IN_MILLIS = 3600000;

    private ConcurrentHashMap<Gav, ComponentModel> cache;

    private ConcurrentHashMap<Gav, Timestamp> cacheKeyTTL;

    private Timestamp oldestKeyAge;

    private final int cacheCapacity;

    private final DependencyInformationService dependencyInformationService;

    public ComponentCache(final int cacheCapacity, final DependencyInformationService dependencyInformationService) {
        this.cacheCapacity = cacheCapacity;
        this.dependencyInformationService = dependencyInformationService;
        cache = buildCache();
    }

    private ConcurrentHashMap<Gav, ComponentModel> buildCache() {
        cache = new ConcurrentHashMap<>();
        cacheKeyTTL = new ConcurrentHashMap<>();
        return cache;
    }

    public ComponentModel get(Gav gav) throws IntegrationException {
        ComponentModel model = cache.get(gav);
        Timestamp stalestamp = new Timestamp(System.currentTimeMillis() - TTL_IN_MILLIS);
        if (oldestKeyAge != null && oldestKeyAge.before(stalestamp)) {
            removeStaleKeys(stalestamp);
        }
        if (model == null) {
            try {
                model = dependencyInformationService.load(gav);
            } catch (IOException | URISyntaxException e) {
                throw new IntegrationException(e);
            }
            // If over capacity, pop least recently used
            if (cache.size() == cacheCapacity) {
                removeLeastRecentlyUsedKey();
            }
            cache.put(gav, model);
            cacheKeyTTL.put(gav, new Timestamp(System.currentTimeMillis()));
        }
        return model;
    }

    public void removeLeastRecentlyUsedKey() {
        cache.remove(Collections.min(cacheKeyTTL.entrySet(),
                (entry1, entry2) -> entry1.getValue().getNanos() - entry2.getValue().getNanos()).getKey());
    }

    public void removeStaleKeys(Timestamp stalestamp) {
        oldestKeyAge = null;
        cacheKeyTTL.forEach(cacheCapacity, (livingGav, timestamp) -> {
            if (timestamp.before(stalestamp)) {
                cache.remove(livingGav);
                cacheKeyTTL.remove(livingGav);
            } else {
                oldestKeyAge = (oldestKeyAge == null || oldestKeyAge.after(timestamp)) ? timestamp : oldestKeyAge;
            }
        });
    }

}
