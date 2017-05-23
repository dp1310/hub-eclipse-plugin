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

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.blackducksoftware.integration.hub.rest.RestConnection;
import com.blackducksoftware.integration.validator.FieldEnum;
import com.blackducksoftware.integration.validator.ValidationResult;
import com.blackducksoftware.integration.validator.ValidationResults;

public class AuthorizationResponse {
    private final RestConnection connection;

    private final String responseMessage;

    private final Set<Object> invalidFields;

    public AuthorizationResponse(final RestConnection connection, final String responseMessage) {
        this.invalidFields = new HashSet<>();
        this.connection = connection;
        this.responseMessage = responseMessage;
    }

    public AuthorizationResponse(final String responseMessage) {
        this(null, responseMessage);
    }

    public AuthorizationResponse(final ValidationResults responseResults) {
        this.invalidFields = new HashSet<>();
        this.connection = null;
        this.responseMessage = parseResponseMessageFromValidationResults(responseResults);
    }

    public RestConnection getConnection() {
        return connection;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public Set<Object> getInvalidFields() {
        return invalidFields;
    }

    private String parseResponseMessageFromValidationResults(final ValidationResults responseResults) {
        final StringBuilder errBuilder = new StringBuilder();
        final Set<String> results = new LinkedHashSet<>();
        final Map<FieldEnum, Set<ValidationResult>> responseMap = responseResults.getResultMap();
        for (final Entry<FieldEnum, Set<ValidationResult>> result : responseMap.entrySet()) {
            final String fieldResults = StringUtils.join(result.getValue(), ", ");
            results.add(fieldResults);
            invalidFields.add(result.getKey());
        }
        for (final String result : results) {
            errBuilder.append(result);
            errBuilder.append(System.lineSeparator());
        }
        String responseString = errBuilder.toString();
        responseString = responseString.replaceAll("ERROR,", "");
        responseString = responseString.substring(0, responseString.lastIndexOf(System.lineSeparator()));
        return responseString;
    }
}
