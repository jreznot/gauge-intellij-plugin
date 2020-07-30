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

package com.thoughtworks.gauge.parser;

import com.intellij.testFramework.ParsingTestCase;

import java.io.File;

public class SpecParsingTestCase extends ParsingTestCase {
    public SpecParsingTestCase() {
        super("", "spec", new SpecParserDefinition());
    }

    public void testSimpleSpec() {
        doTest(true);
    }

    public void testSpecWithDataTable() {
        doTest(true);
    }

    public void testSpecWithEmptyTableHeaders() {
        doTest(true);
    }

    @Override
    protected String getTestDataPath() {
        return new File("testdata", "specParser").getAbsolutePath();
    }

    @Override
    protected boolean skipSpaces() {
        return false;
    }

    @Override
    protected boolean includeRanges() {
        return true;
    }
}