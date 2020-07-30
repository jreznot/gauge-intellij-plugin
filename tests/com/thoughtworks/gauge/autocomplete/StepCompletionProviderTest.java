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

package com.thoughtworks.gauge.autocomplete;

import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.openapi.util.TextRange;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import com.thoughtworks.gauge.StepValue;
import com.thoughtworks.gauge.connection.GaugeConnection;
import com.thoughtworks.gauge.core.Gauge;
import com.thoughtworks.gauge.core.GaugeService;

import java.io.File;
import java.util.List;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class StepCompletionProviderTest extends LightCodeInsightFixtureTestCase {

    private GaugeConnection mockGaugeConnection;
    private Process mockProcess;

    @Override
    protected void setUp() throws Exception {
        mockProcess = mock(Process.class);
        mockGaugeConnection = mock(GaugeConnection.class);
        super.setUp();
    }

    @Override
    protected String getTestDataPath() {
        return new File("testdata", "stepAutocomplete").getAbsolutePath();
    }

    public void testStepAutoCompletion() throws Exception {
        myFixture.configureByFiles("SimpleSpec.spec", "SimpleSpec.txt");
        Gauge.addModule(myFixture.getModule(), new GaugeService(mockProcess, mockGaugeConnection));
        StepValue stepValue1 = new StepValue("This is a step", "This is a step");
        StepValue stepValue2 = new StepValue("Hello", "Hello");
        StepValue stepValue3 = new StepValue("The step", "The step");
        when(mockGaugeConnection.fetchAllSteps()).thenReturn(asList(stepValue1, stepValue2, stepValue3));
        myFixture.getEditor().getCaretModel().moveToOffset(49);
        myFixture.complete(CompletionType.BASIC, 1);
        List<String> strings = myFixture.getLookupElementStrings();
        assertEquals(2, strings.size());
        assertTrue(strings.containsAll(asList("This is a step", "The step")));
    }

    public void testStepAutoCompletionWithParams() throws Exception {
        myFixture.configureByFiles("SimpleSpec.spec", "SimpleSpec.txt");
        Gauge.addModule(myFixture.getModule(), new GaugeService(mockProcess, mockGaugeConnection));
        StepValue stepValue1 = new StepValue("This is a step with {}", "This is a step with <param>", asList("param"));
        when(mockGaugeConnection.fetchAllSteps()).thenReturn(asList(stepValue1));
        myFixture.getEditor().getCaretModel().moveToOffset(49);
        myFixture.complete(CompletionType.BASIC, 1);
        String expected = "*This is a step with \"param\"";
        String actual = myFixture.getEditor().getDocument().getText(new TextRange(47, 75));
        assertEquals(expected, actual);
    }

    public void testStepAutoCompleteShouldReplaceBracesOfOnlyParams() throws Exception {
        myFixture.configureByFiles("SimpleSpec.spec", "SimpleSpec.txt");
        Gauge.addModule(myFixture.getModule(), new GaugeService(mockProcess, mockGaugeConnection));
        StepValue stepValue1 = new StepValue("This is a step with {} and >", "This is a step with <param> and >", asList("param"));
        when(mockGaugeConnection.fetchAllSteps()).thenReturn(asList(stepValue1));
        myFixture.getEditor().getCaretModel().moveToOffset(49);
        myFixture.complete(CompletionType.BASIC, 1);
        String expected = "*This is a step with \"param\" and >";
        String actual = myFixture.getEditor().getDocument().getText(new TextRange(47, 81));
        assertEquals(expected, actual);
    }
}
