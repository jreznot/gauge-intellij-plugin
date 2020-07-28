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

import com.thoughtworks.gauge.execution.runner.event.ExecutionEvent;
import org.junit.Test;

import static org.junit.Assert.assertTrue;


public class NotificationEventProcessorTest {
    @Test
    public void canProcess() {
        NotificationEventProcessor processor = new NotificationEventProcessor(null, null);
        ExecutionEvent event = new ExecutionEvent();
        event.type = ExecutionEvent.NOTIFICATION;
        assertTrue(processor.canProcess(event));
    }
}