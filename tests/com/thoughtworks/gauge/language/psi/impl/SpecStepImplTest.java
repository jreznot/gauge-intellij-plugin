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

package com.thoughtworks.gauge.language.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiReference;
import com.thoughtworks.gauge.helper.ModuleHelper;
import com.thoughtworks.gauge.reference.StepReference;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SpecStepImplTest {
    @Test
    public void testShouldGetReferenceInGaugeModule() {
        ModuleHelper helper = mock(ModuleHelper.class);
        ASTNode node = mock(ASTNode.class);
        SpecStepImpl specStep = new SpecStepImpl(node, helper);
        when(helper.isGaugeModule(specStep)).thenReturn(true);

        PsiReference reference = specStep.getReference();

        assertEquals(reference.getClass(), StepReference.class);
    }

    @Test
    public void testShouldNotGetReferenceInNonGaugeModule() {
        ModuleHelper helper = mock(ModuleHelper.class);
        ASTNode node = mock(ASTNode.class);
        SpecStepImpl specStep = new SpecStepImpl(node, helper);
        when(helper.isGaugeModule(specStep)).thenReturn(false);

        PsiReference reference = specStep.getReference();

        assertNull(reference);
    }
}