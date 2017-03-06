/*
 * Copyright (C) 2017 Black Duck Software Inc.
 * http://www.blackducksoftware.com/
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Black Duck Software ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Black Duck Software.
 */
package com.blackducksoftware.integration.eclipseplugin.common.constants;

public class NatureIDs {

    public static final String GRADLE_NATURE = "org.eclipse.buildship.core.gradleprojectnature";

    public static final String MAVEN_NATURE = "org.eclipse.m2e.core.maven2Nature";

    public static final String[] SUPPORTED_NATURES = {
            GRADLE_NATURE,
            MAVEN_NATURE
    };

}
