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

package com.thoughtworks.gauge.execution.runner.processors;

import com.intellij.execution.testframework.sm.ServiceMessageBuilder;
import com.thoughtworks.gauge.execution.runner.MessageProcessor;
import com.thoughtworks.gauge.execution.runner.TestsCache;
import com.thoughtworks.gauge.execution.runner.event.ExecutionError;
import com.thoughtworks.gauge.execution.runner.event.ExecutionEvent;
import com.thoughtworks.gauge.execution.runner.event.ExecutionResult;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class ScenarioEventProcessorTest {
    @Test
    public void onEndWithoutFailure() throws Exception {
        MessageProcessor mockProcessor = mock(MessageProcessor.class);
        ScenarioEventProcessor processor = new ScenarioEventProcessor(mockProcessor, new TestsCache());
        ExecutionEvent event = new ExecutionEvent();
        event.type = ExecutionEvent.SCENARIO_END;
        event.name = "name";
        event.result = new ExecutionResult();
        event.result.status = "pass";
        event.result.time = 1;

        processor.onEnd(event);

        ServiceMessageBuilder builder = ServiceMessageBuilder.testFinished("name");
        builder.addAttribute("duration", "1");
        verify(mockProcessor, times(1)).process(argThat(new ServiceMessageBuilderMatcher<>(builder)), any(Integer.class), any(Integer.class));
    }

    @Test
    public void onEndWithFailure() throws Exception {
        MessageProcessor mockProcessor = mock(MessageProcessor.class);
        ScenarioEventProcessor processor = new ScenarioEventProcessor(mockProcessor, new TestsCache());
        ExecutionEvent event = new ExecutionEvent();
        event.type = ExecutionEvent.SCENARIO_END;
        event.name = "name";
        event.result = new ExecutionResult();
        event.result.status = ExecutionEvent.FAIL;
        event.result.time = 1;
        event.result.errors = new ExecutionError[]{getError("text", "message", "filename", "123", "stacktrace")};

        processor.onEnd(event);

        ServiceMessageBuilder failed = ServiceMessageBuilder.testFailed("name");
        failed.addAttribute("message", "Failed: text\nFilename: filename:123\nMessage: message\nStack Trace:\nstacktrace");
        ServiceMessageBuilder finished = ServiceMessageBuilder.testFinished("name");
        finished.addAttribute("duration", "1");
        verify(mockProcessor, times(1)).process(argThat(new ServiceMessageBuilderMatcher<>(failed)), any(Integer.class), any(Integer.class));
        verify(mockProcessor, times(1)).process(argThat(new ServiceMessageBuilderMatcher<>(finished)), any(Integer.class), any(Integer.class));
    }

    @Test
    public void onEndWithSkippedTest() throws Exception {
        MessageProcessor mockProcessor = mock(MessageProcessor.class);
        ScenarioEventProcessor processor = new ScenarioEventProcessor(mockProcessor, new TestsCache());
        ExecutionEvent event = new ExecutionEvent();
        event.type = ExecutionEvent.SCENARIO_END;
        event.name = "name";
        event.result = new ExecutionResult();
        event.result.status = ExecutionEvent.SKIP;
        event.result.time = 1;
        event.result.errors = new ExecutionError[]{getError("text", "message", "filename", "123", "stacktrace")};

        processor.onEnd(event);

        ServiceMessageBuilder skipped = ServiceMessageBuilder.testIgnored("name");
        skipped.addAttribute("message", "Skipped: text\nFilename: filename:123\nMessage: message\nStack Trace:\nstacktrace");
        ServiceMessageBuilder finished = ServiceMessageBuilder.testFinished("name");
        finished.addAttribute("duration", "1");
        verify(mockProcessor, times(1)).process(argThat(new ServiceMessageBuilderMatcher<>(skipped)), any(Integer.class), any(Integer.class));
        verify(mockProcessor, times(1)).process(argThat(new ServiceMessageBuilderMatcher<>(finished)), any(Integer.class), any(Integer.class));
    }

    @Test
    public void onEndWithHookFailure() throws Exception {
        MessageProcessor mockProcessor = mock(MessageProcessor.class);
        ScenarioEventProcessor processor = new ScenarioEventProcessor(mockProcessor, new TestsCache());
        ExecutionEvent event = new ExecutionEvent();
        event.type = ExecutionEvent.SPEC_END;
        event.name = "name";
        event.result = new ExecutionResult();
        event.result.status = ExecutionEvent.FAIL;
        event.result.beforeHookFailure = getError("text", "message", "filename", "", "stacktrace");
        event.result.afterHookFailure = getError("text1", "message1", "filename1", "", "stacktrace1");
        event.result.time = 1;

        processor.onEnd(event);

        ServiceMessageBuilder failed = ServiceMessageBuilder.testFailed("name");
        failed.addAttribute("message", "Failed: text\nFilename: filename\nMessage: message\nStack Trace:\nstacktrace\n\nFailed: text1\nFilename: filename1\nMessage: message1\nStack Trace:\nstacktrace1");
        ServiceMessageBuilder finished = ServiceMessageBuilder.testFinished("name");
        finished.addAttribute("duration", "1");
        verify(mockProcessor, times(1)).process(argThat(new ServiceMessageBuilderMatcher<>(failed)), any(Integer.class), any(Integer.class));
        verify(mockProcessor, times(1)).process(argThat(new ServiceMessageBuilderMatcher<>(finished)), any(Integer.class), any(Integer.class));
    }

    @Test
    public void canProcess() throws Exception {
        ScenarioEventProcessor processor = new ScenarioEventProcessor(null, null);
        ExecutionEvent event = new ExecutionEvent();
        event.type = ExecutionEvent.SCENARIO_START;
        assertTrue(processor.canProcess(event));

        event.type = ExecutionEvent.SCENARIO_END;
        assertTrue(processor.canProcess(event));
    }

    @Test
    public void onStart() throws Exception {
        MessageProcessor mockProcessor = mock(MessageProcessor.class);
        ScenarioEventProcessor processor = new ScenarioEventProcessor(mockProcessor, new TestsCache());
        ExecutionEvent event = new ExecutionEvent();
        event.type = ExecutionEvent.SCENARIO_START;
        event.name = "name";
        event.result = new ExecutionResult();

        processor.onStart(event);

        ServiceMessageBuilder started = ServiceMessageBuilder.testStarted("name");
        verify(mockProcessor, times(1)).process(argThat(new ServiceMessageBuilderMatcher<>(started)), any(Integer.class), any(Integer.class));
    }

    public static ExecutionError getError(String text, String message, String filename, String line, String stacktrace) {
        ExecutionError error = new ExecutionError();
        error.text = text;
        error.message = message;
        error.filename = filename;
        error.lineNo = line;
        error.stackTrace = stacktrace;
        return error;
    }

}