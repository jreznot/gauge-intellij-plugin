/*
 * Copyright (C) 2020 ThoughtWorks, Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.thoughtworks.gauge.execution;

import com.intellij.execution.configuration.ConfigurationFactoryEx;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationTypeBase;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.extensions.Extensions;
import com.intellij.openapi.project.Project;
import com.intellij.util.containers.ContainerUtil;

public class GaugeRunTaskConfigurationType extends ConfigurationTypeBase {
    public GaugeRunTaskConfigurationType() {
        super("executeSpecs", "Gauge Execution", "Execute the gauge tests", AllIcons.Actions.Execute);
        final ConfigurationFactory scenarioConfigFactory = new ConfigurationFactoryEx(this) {
            @Override
            public RunConfiguration createTemplateConfiguration(Project project) {
                return new GaugeRunConfiguration("Gauge Execution", project, this);
            }
        };

        addFactory(scenarioConfigFactory);
    }

    public GaugeRunTaskConfigurationType getInstance() {
        return ContainerUtil.findInstance(Extensions.getExtensions(CONFIGURATION_TYPE_EP), GaugeRunTaskConfigurationType.class);
    }
}
