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

package com.thoughtworks.gauge.annotator;

import com.intellij.lang.ASTNode;
import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiClass;
import com.thoughtworks.gauge.language.psi.impl.ConceptStepImpl;
import com.thoughtworks.gauge.language.psi.impl.SpecStepImpl;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class StepAnnotatorTest {

    private AnnotationHelper helper;
    private AnnotationHolder holder;
    private TextRange textRange;

    @Before
    public void setUp() {
        holder = mock(AnnotationHolder.class);
        helper = mock(AnnotationHelper.class);
        textRange = mock(TextRange.class);
    }

    @Test
    public void testShouldNotAnnotateNonGaugeElement() {
        PsiClass element = mock(PsiClass.class);
        when(helper.isGaugeModule(element)).thenReturn(true);

        new StepAnnotator(helper).annotate(element, holder);

        verify(holder, never()).createErrorAnnotation(any(TextRange.class), any(String.class));
    }

    @Test
    public void testShouldAnnotateBlankSpecStep() {
        SpecStepImpl element = mock(SpecStepImpl.class);

        when(helper.isGaugeModule(element)).thenReturn(true);
        when(helper.isEmpty(element)).thenReturn(true);
        when(element.getTextRange()).thenReturn(textRange);

        new StepAnnotator(helper).annotate(element, holder);

        verify(holder, times(1)).createErrorAnnotation(textRange, "Step should not be blank");
    }

    @Test
    public void testShouldAnnotateBlankConceptStep() {
        ConceptStepImpl element = mock(ConceptStepImpl.class);

        when(helper.isGaugeModule(element)).thenReturn(true);
        when(helper.isEmpty(any(SpecStepImpl.class))).thenReturn(true);
        when(element.getTextRange()).thenReturn(textRange);
        when(element.getNode()).thenReturn(mock(ASTNode.class));

        new StepAnnotator(helper).annotate(element, holder);

        verify(holder, times(1)).newAnnotation(HighlightSeverity.ERROR, "Step should not be blank");
    }

    @Test
    public void testShouldNotAnnotateInNonGaugeModule() {
        SpecStepImpl element = mock(SpecStepImpl.class);
        when(helper.isGaugeModule(element)).thenReturn(false);

        new StepAnnotator(helper).annotate(element, holder);

        verify(holder, never()).newAnnotation(HighlightSeverity.ERROR, any(String.class));
    }

    @Test
    public void testShouldAnnotateInGaugeModule() {
        SpecStepImpl element = mock(SpecStepImpl.class);
        Module module = mock(Module.class);

        when(helper.isGaugeModule(element)).thenReturn(true);
        when(element.getTextRange()).thenReturn(textRange);
        when(helper.getModule(element)).thenReturn(module);
        when(helper.isEmpty(element)).thenReturn(false);
        when(helper.isImplemented(element, module)).thenReturn(false);
        when(holder.createErrorAnnotation(textRange, "Undefined step")).thenReturn(
                new Annotation(1, 1, new HighlightSeverity("dsf", 1), "", "")
        );

        new StepAnnotator(helper).annotate(element, holder);

        verify(holder, times(1)).newAnnotation(HighlightSeverity.ERROR, "Undefined step");
    }
}