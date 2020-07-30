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

package com.thoughtworks.gauge.core;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class GaugeVersionTest {

    private List<Plugin> plugins;

    @Before
    public void setup() {
        Plugin java = new Plugin();
        java.name = "java";
        Plugin spectacle = new Plugin();
        spectacle.name = "spectacle";
        plugins = Arrays.asList(java, spectacle);
    }

    @Test
    public void testIsPluginInstalledForInstalledPlugin() throws Exception {
        Assert.assertTrue(new GaugeVersionInfo("0.4.0", plugins).isPluginInstalled(plugins.get(0).name));
    }

    @Test
    public void testIsPluginInstalledForUnInstalledPlugin() throws Exception {
        Assert.assertFalse(new GaugeVersionInfo("0.4.0", plugins).isPluginInstalled("haha"));
    }
}