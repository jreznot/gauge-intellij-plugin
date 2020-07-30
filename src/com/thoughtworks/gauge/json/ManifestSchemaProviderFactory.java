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

package com.thoughtworks.gauge.json;

import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.jetbrains.jsonSchema.extension.JsonSchemaFileProvider;
import com.jetbrains.jsonSchema.extension.JsonSchemaProviderFactory;
import com.jetbrains.jsonSchema.extension.SchemaType;
import com.thoughtworks.gauge.util.GaugeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

final class ManifestSchemaProviderFactory implements JsonSchemaProviderFactory {
    @Override
    public @NotNull List<JsonSchemaFileProvider> getProviders(@NotNull Project project) {
        return Collections.singletonList(new ManifestSchemaProvider(project));
    }

    private static class ManifestSchemaProvider implements JsonSchemaFileProvider {

        private final Project project;

        private ManifestSchemaProvider(Project project) {
            this.project = project;
        }

        @Override
        public boolean isAvailable(@NotNull VirtualFile file) {
            return "manifest.json".equals(file.getName()) && ReadAction.compute(() -> {
                Module module = ModuleUtilCore.findModuleForFile(file, project);
                return module != null && GaugeUtil.isGaugeModule(module);
            });
        }

        @Override
        public @NotNull String getName() {
            return "Gauge 1.0 Manifest";
        }

        @Override
        public @Nullable VirtualFile getSchemaFile() {
            return VfsUtil.findFileByURL(getClass().getResource("/schemas/gauge-1.0.json"));
        }

        @Override
        public @NotNull SchemaType getSchemaType() {
            return SchemaType.embeddedSchema;
        }
    }
}
