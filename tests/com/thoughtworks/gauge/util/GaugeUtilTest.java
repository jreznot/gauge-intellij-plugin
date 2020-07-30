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
package com.thoughtworks.gauge.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;

import com.intellij.openapi.module.Module;

public class GaugeUtilTest {

    @Test
    public void getProjectDirForGradleProjectShouldReturnCorrectPathForAllModules() {

        final String rootModule = "/Users/gauge/workspace-idea/project/.idea/modules/project-root.iml";
        final String projectModule = "/Users/gauge/workspace-idea/project/.idea/modules/@project-subproject/@project-subproject.iml";
        final String subModule = "/Users/gauge/workspace-idea/project/.idea/modules/@project-subproject/project-submodule/project-submodule.iml";
        final String subModuleMain = "/Users/gauge/workspace-idea/project/.idea/modules/@project-subproject/project-submodule/project-submodule_main.iml";
        final String subModuleTest = "/Users/gauge/workspace-idea/project/.idea/modules/@project-subproject/project-submodule/project-submodule_test.iml";

        assertThat(getPathForModule(rootModule)).satisfiesAnyOf(
            path -> assertThat(path).isEqualTo("/Users/gauge/workspace-idea/project"), // Unix based OS
            path -> assertThat(path).matches("[A-Z]:\\\\Users\\\\gauge\\\\workspace-idea\\\\project") // Windows
        );

        assertThat(getPathForModule(projectModule)).satisfiesAnyOf(
            path -> assertThat(path).isEqualTo("/Users/gauge/workspace-idea/project/@project-subproject"), // Unix based OS
            path -> assertThat(path).matches("[A-Z]:\\\\Users\\\\gauge\\\\workspace-idea\\\\project\\\\@project-subproject") // Windows
        );

        assertThat(getPathForModule(subModule)).satisfiesAnyOf(
            path -> assertThat(path).isEqualTo("/Users/gauge/workspace-idea/project/@project-subproject/project-submodule"), // Unix based OS
            path -> assertThat(path).matches("[A-Z]:\\\\Users\\\\gauge\\\\workspace-idea\\\\project\\\\@project-subproject\\\\project-submodule") // Windows
        );

        assertThat(getPathForModule(subModuleMain)).satisfiesAnyOf(
            path -> assertThat(path).isEqualTo("/Users/gauge/workspace-idea/project/@project-subproject/project-submodule"), // Unix based OS
            path -> assertThat(path).matches("[A-Z]:\\\\Users\\\\gauge\\\\workspace-idea\\\\project\\\\@project-subproject\\\\project-submodule") // Windows
        );

        assertThat(getPathForModule(subModuleTest)).satisfiesAnyOf(
            path -> assertThat(path).isEqualTo("/Users/gauge/workspace-idea/project/@project-subproject/project-submodule"), // Unix based OS
            path -> assertThat(path).matches("[A-Z]:\\\\Users\\\\gauge\\\\workspace-idea\\\\project\\\\@project-subproject\\\\project-submodule") // Windows
        );
    }

    private String getPathForModule(String path) {

        final Module module = mock(Module.class);
        when(module.getModuleFilePath()).thenReturn(path);

        return GaugeUtil.getProjectDirForGradleProject(module).getAbsolutePath();
    }
}