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

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameterList;
import com.thoughtworks.gauge.StepValue;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static java.util.Collections.singletonList;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ParamAnnotatorTest {

    private AnnotationHolder holder;
    private AnnotationHelper helper;
    private TextRange textRange;
    private PsiMethod element;
    private PsiParameterList parameterList;

    @Before
    public void setUp() {
        holder = mock(AnnotationHolder.class);
        helper = mock(AnnotationHelper.class);
        textRange = mock(TextRange.class);
        element = mock(PsiMethod.class);
        parameterList = mock(PsiParameterList.class);
    }

    @Test
    public void testShouldNotAnnotateNonGaugeElement() {
        PsiElement element = mock(PsiClass.class);
        when(helper.isGaugeModule(element)).thenReturn(true);

        new ParamAnnotator(helper).annotate(element, holder);

        verify(holder, never()).newAnnotation(HighlightSeverity.ERROR, any(String.class))
                .range(any(TextRange.class))
                .create();
    }

    @Test
    public void testShouldNotAnnotateInNonGaugeModule() {
        when(helper.isGaugeModule(element)).thenReturn(false);

        new ParamAnnotator(helper).annotate(element, holder);

        verify(holder, never()).newAnnotation(HighlightSeverity.ERROR, any(String.class))
                .range(any(TextRange.class))
                .create();
    }

    @Test
    public void testShouldAnnotateWhenParamMismatch() {
        when(helper.isGaugeModule(element)).thenReturn(true);
        when(helper.getStepValues(element)).thenReturn(singletonList(new StepValue("step", "step", singletonList("a"))));
        when(element.getParameterList()).thenReturn(parameterList);
        when(parameterList.getTextRange()).thenReturn(textRange);
        when(parameterList.getParametersCount()).thenReturn(2);

        new ParamAnnotator(helper).annotate(element, holder);

        verify(holder, times(1)).newAnnotation(HighlightSeverity.ERROR,
                "Parameter count mismatch(found [2] expected [1]) with step annotation : \"step\". ")
                .range(textRange)
                .create();
    }

    @Test
    public void testShouldAnnotateWhenParamMismatchInAlias() {
        when(helper.isGaugeModule(element)).thenReturn(true);
        when(helper.getStepValues(element)).thenReturn(Arrays.asList(new StepValue("step", "step", Arrays.asList("a", "b")), new StepValue("step1", "step1", singletonList("a"))));
        when(element.getParameterList()).thenReturn(parameterList);
        when(parameterList.getTextRange()).thenReturn(textRange);
        when(parameterList.getParametersCount()).thenReturn(2);

        new ParamAnnotator(helper).annotate(element, holder);

        verify(holder, times(1)).newAnnotation(HighlightSeverity.ERROR,
                "Parameter count mismatch(found [2] expected [1]) with step annotation : \"step1\". ")
                .range(textRange)
                .create();
    }

    @Test
    public void testShouldNotAnnotateWhenThereIsNoParamMismatch() {
        when(helper.isGaugeModule(element)).thenReturn(true);
        when(helper.getStepValues(element)).thenReturn(singletonList(new StepValue("step", "step", singletonList("a"))));
        when(element.getParameterList()).thenReturn(parameterList);
        when(parameterList.getTextRange()).thenReturn(textRange);
        when(parameterList.getParametersCount()).thenReturn(1);

        new ParamAnnotator(helper).annotate(element, holder);

        verify(holder, never()).newAnnotation(HighlightSeverity.ERROR, any(String.class))
                .range(any(TextRange.class))
                .create();
    }
}