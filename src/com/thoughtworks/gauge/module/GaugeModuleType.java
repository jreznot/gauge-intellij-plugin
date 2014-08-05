package com.thoughtworks.gauge.module;

import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.module.ModuleTypeManager;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class GaugeModuleType extends ModuleType<GaugeModuleBuilder> {
    public static final String MODULE_TYPE_ID = "Gauge_Module";
    private static final String GAUGE_MODULE = "Gauge Module";

    public GaugeModuleType() {
        super(MODULE_TYPE_ID);
    }

    public static GaugeModuleType getInstance() {
        return (GaugeModuleType) ModuleTypeManager.getInstance().findByID(MODULE_TYPE_ID);
    }

    @NotNull
    @Override
    public GaugeModuleBuilder createModuleBuilder() {
        return new GaugeModuleBuilder();
    }

    @NotNull
    @Override
    public String getName() {
        return GAUGE_MODULE;
    }

    @NotNull
    @Override
    public String getDescription() {
        return "module supported for writing gauge tests";
    }

    @Override
    public Icon getBigIcon() {
        return null;
    }

    @Override
    public Icon getNodeIcon(@Deprecated boolean isOpened) {
        return null;
    }
}
