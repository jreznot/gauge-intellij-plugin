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
import com.thoughtworks.gauge.language.psi.ConceptConceptHeading;
import com.thoughtworks.gauge.language.psi.ConceptStep;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ConceptInspectionProviderTest {
    @Test
    public void testGetElementReturnsStep() throws Exception {
        ConceptStep step = mock(ConceptStep.class);

        PsiElement element = new ConceptInspectionProvider().getElement(step);

        assertEquals(step, element);
    }

    @Test
    public void testGetElementReturnsNullIfElementNotPresent() throws Exception {
        PsiElement element = new ConceptInspectionProvider().getElement(null);

        assertEquals(null, element);
    }

    @Test
    public void testGetElementReturnsConceptHeading() throws Exception {
        PsiElement e = mock(ConceptConceptHeading.class);

        when(e.getParent()).thenReturn(e);

        PsiElement element = new ConceptInspectionProvider().getElement(e);

        assertEquals(e, element);
    }
}