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
import org.junit.Test;

public class GaugeVersionInfoTest {
    @Test
    public void testIsGreaterThanWithGreaterVersionsToInstalledVersion() {
        Assert.assertTrue(new GaugeVersionInfo("0.4.7").isGreaterOrEqual(new GaugeVersionInfo("0.4.4")));
        Assert.assertTrue(new GaugeVersionInfo("0.4.6").isGreaterOrEqual(new GaugeVersionInfo("0.3.6")));
        Assert.assertTrue(new GaugeVersionInfo("2.4.0").isGreaterOrEqual(new GaugeVersionInfo("1.6.3")));
        Assert.assertTrue(new GaugeVersionInfo("0.4.0").isGreaterOrEqual(new GaugeVersionInfo("0.4.0")));
    }

    @Test
    public void testIsGreaterThanWithLowerVersionsToInstalledVersion() {
        Assert.assertFalse(new GaugeVersionInfo("0.4.2").isGreaterOrEqual(new GaugeVersionInfo("0.4.3")));
        Assert.assertFalse(new GaugeVersionInfo("0.4.1.nightly-2016-05-12").isGreaterOrEqual(new GaugeVersionInfo("0.4.2")));
        Assert.assertFalse(new GaugeVersionInfo("0.4.2").isGreaterOrEqual(new GaugeVersionInfo("0.5.0")));
        Assert.assertFalse(new GaugeVersionInfo("0.4.0").isGreaterOrEqual(new GaugeVersionInfo("0.5.5")));
        Assert.assertFalse(new GaugeVersionInfo("2.4.0").isGreaterOrEqual(new GaugeVersionInfo("5.5.3")));
    }
}