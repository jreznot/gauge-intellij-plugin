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

package com.thoughtworks.gauge.util;

import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifierList;
import com.thoughtworks.gauge.BeforeStep;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HookUtilTest {
    @Test
    public void TestIsHook() throws Exception {
        PsiMethod method = mock(PsiMethod.class);

        PsiModifierList list = mock(PsiModifierList.class);
        PsiAnnotation annotation = mock(PsiAnnotation.class);
        when(annotation.getQualifiedName()).thenReturn(BeforeStep.class.getCanonicalName());
        when(list.getAnnotations()).thenReturn(new PsiAnnotation[]{annotation});
        when(method.getModifierList()).thenReturn(list);

        assertTrue(HookUtil.isHook(method));
    }

    @Test
    public void TestIsHookWhenElementIsNotMethod() throws Exception {
        PsiElement element = mock(PsiElement.class);

        assertFalse(HookUtil.isHook(element));
    }

    @Test
    public void TestIsHookWhenNonHookAnnotation() throws Exception {
        PsiMethod method = mock(PsiMethod.class);

        PsiModifierList list = mock(PsiModifierList.class);
        PsiAnnotation annotation = mock(PsiAnnotation.class);
        when(annotation.getQualifiedName()).thenReturn("Unequal");
        when(list.getAnnotations()).thenReturn(new PsiAnnotation[]{annotation});
        when(method.getModifierList()).thenReturn(list);

        assertFalse(HookUtil.isHook(method));
    }

    @Test
    public void TestIsHookWhenNoAnnotationPresent() throws Exception {
        PsiMethod method = mock(PsiMethod.class);

        PsiModifierList list = mock(PsiModifierList.class);
        when(list.getAnnotations()).thenReturn(new PsiAnnotation[]{});
        when(method.getModifierList()).thenReturn(list);

        assertFalse(HookUtil.isHook(method));
    }
}