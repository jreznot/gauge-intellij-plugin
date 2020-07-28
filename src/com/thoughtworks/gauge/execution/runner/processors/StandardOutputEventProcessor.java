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

import com.thoughtworks.gauge.execution.runner.MessageProcessor;
import com.thoughtworks.gauge.execution.runner.TestsCache;
import com.thoughtworks.gauge.execution.runner.event.ExecutionEvent;
import org.apache.commons.lang3.StringUtils;

public class StandardOutputEventProcessor extends GaugeEventProcessor {

    public StandardOutputEventProcessor(MessageProcessor processor, TestsCache cache) {
        super(processor, cache);
    }

    @Override
    protected Boolean onStart(ExecutionEvent event) {
        return true;
    }

    @Override
    protected Boolean onEnd(ExecutionEvent event) {
        getProcessor().process(StringUtils.appendIfMissing(event.message, "\n"));
        getProcessor().processLineBreak();
        return true;
    }

    @Override
    public Boolean canProcess(ExecutionEvent event) {
        return event.type.equalsIgnoreCase(ExecutionEvent.STANDARD_OUTPUT);
    }
}
