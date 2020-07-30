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

package com.thoughtworks.gauge.language;

import com.intellij.openapi.fileTypes.LanguageFileType;
import com.thoughtworks.gauge.Constants;
import com.thoughtworks.gauge.idea.icon.GaugeIcon;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class SpecFileType extends LanguageFileType {
    public static final SpecFileType INSTANCE = new SpecFileType();

    private SpecFileType() {
        super(Specification.INSTANCE);
    }

    @NotNull
    @Override
    public String getName() {
        return "Specification";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Gauge specification file";
    }

    @NotNull
    @Override
    public String getDefaultExtension() {
        return Constants.SPEC_EXTENSION;
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return GaugeIcon.GAUGE_SPEC_FILE_ICON;
    }
}
