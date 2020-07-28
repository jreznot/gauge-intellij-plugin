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

package com.thoughtworks.gauge.inspection;

import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.thoughtworks.gauge.language.psi.SpecStep;
import com.thoughtworks.gauge.language.token.SpecTokenTypes;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SpecInspectionProviderTest {
    @Test
    public void testGetElementReturnsStep() {
        SpecStep step = mock(SpecStep.class);

        PsiElement element = new SpecInspectionProvider().getElement(step);

        assertEquals(step, element);
    }

    @Test
    public void testGetElementReturnsNullIfElementNotPresent() {
        PsiElement element = new SpecInspectionProvider().getElement(null);

        assertNull(element);
    }

    @Test
    public void testGetElementReturnsScenarioHeading() {
        PsiElement e = mock(PsiElement.class);
        LeafPsiElement leafPsiElement = mock(LeafPsiElement.class);

        when(leafPsiElement.getElementType()).thenReturn(SpecTokenTypes.SCENARIO_HEADING);
        when(e.getParent()).thenReturn(leafPsiElement);

        PsiElement element = new SpecInspectionProvider().getElement(e);

        assertEquals(leafPsiElement, element);
    }

    @Test
    public void testGetElementReturnsSpecHeading() {
        PsiElement e = mock(PsiElement.class);
        LeafPsiElement leafPsiElement = mock(LeafPsiElement.class);

        when(leafPsiElement.getElementType()).thenReturn(SpecTokenTypes.SPEC_HEADING);
        when(e.getParent()).thenReturn(leafPsiElement);

        PsiElement element = new SpecInspectionProvider().getElement(e);

        assertEquals(leafPsiElement, element);
    }
}