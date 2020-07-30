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
import com.thoughtworks.gauge.reference.ConceptReference;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ConceptStepImplTest {
    @Test
    public void testShouldGetReferenceInGaugeModule() throws Exception {
        ModuleHelper helper = mock(ModuleHelper.class);
        ASTNode node = mock(ASTNode.class);
        ConceptStepImpl conceptStep = new ConceptStepImpl(node, helper);
        when(helper.isGaugeModule(conceptStep)).thenReturn(true);

        PsiReference reference = conceptStep.getReference();

        assertEquals(reference.getClass(), ConceptReference.class);
    }

    @Test
    public void testShouldNotGetReferenceInNonGaugeModule() throws Exception {
        ModuleHelper helper = mock(ModuleHelper.class);
        ASTNode node = mock(ASTNode.class);
        ConceptStepImpl conceptStep = new ConceptStepImpl(node, helper);
        when(helper.isGaugeModule(conceptStep)).thenReturn(false);

        PsiReference reference = conceptStep.getReference();

        assertNull(reference);
    }
}