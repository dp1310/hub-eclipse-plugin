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

public class ConnectionStatus {
    public static final String NO_SELECTED_PROJECT = "No open project selected";

    public static final String PROJECT_INSPECTION_ACTIVE = "Inspection results pending - project is scheduled for inspection";

    public static final String PROJECT_INSPECTION_INACTIVE = "Inspection not activated for current project";

    public static final String PROJECT_NEEDS_INSPECTION = "No inspection results found - project has not yet been inspected";

    public static final String CONNECTION_DISCONNECTED = "No inspection results found - cannot connect to Hub instance";

    public static final String CONNECTION_OK = "Connected to Hub instance - double-click any component to open it in the Hub";;
}
