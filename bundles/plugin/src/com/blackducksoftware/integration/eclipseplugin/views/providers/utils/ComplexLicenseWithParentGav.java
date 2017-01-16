package com.blackducksoftware.integration.eclipseplugin.views.providers.utils;

import com.blackducksoftware.integration.hub.api.component.version.ComplexLicensePlusMeta;
import com.blackducksoftware.integration.hub.buildtool.Gav;

public class ComplexLicenseWithParentGav {

    private final Gav gav;

    private final ComplexLicensePlusMeta complexLicensePlusMeta;

    public ComplexLicenseWithParentGav(Gav gav, ComplexLicensePlusMeta complexLicensePlusMeta) {
        this.gav = gav;
        this.complexLicensePlusMeta = complexLicensePlusMeta;
    }

    public Gav getGav() {
        return this.gav;
    }

    public ComplexLicensePlusMeta getComplexLicensePlusMeta() {
        return this.complexLicensePlusMeta;
    }
}
