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

package com.thoughtworks.gauge.findUsages;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase;
import com.thoughtworks.gauge.findUsages.helper.ReferenceSearchHelper;
import com.thoughtworks.gauge.language.psi.impl.SpecStepImpl;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ReferenceSearchTest extends LightJavaCodeInsightFixtureTestCase {

    private Project project;
    private SpecStepImpl element;
    private ReferenceSearchHelper helper;
    private StepCollector collector;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        element = mock(SpecStepImpl.class);
        helper = mock(ReferenceSearchHelper.class);
        collector = mock(StepCollector.class);
        project = myFixture.getProject();
    }

    public void testShouldNotFindReferencesOfNonGaugeElement() {
        when(element.getProject()).thenReturn(project);
        ReferencesSearch.SearchParameters searchParameters = new ReferencesSearch.SearchParameters(element, GlobalSearchScope.allScope(project), true);
        when(helper.shouldFindReferences(searchParameters, searchParameters.getElementToSearch())).thenReturn(false);

        new ReferenceSearch(helper).processQuery(searchParameters, psiReference -> false);

        verify(helper, never()).getPsiElements(any(StepCollector.class), any(PsiElement.class));
    }

    public void testShouldFindReferencesOfGaugeElement() {
        when(element.getProject()).thenReturn(project);
        ReferencesSearch.SearchParameters searchParameters = new ReferencesSearch.SearchParameters(element, GlobalSearchScope.allScope(project), true);

        when(helper.shouldFindReferences(searchParameters, searchParameters.getElementToSearch())).thenReturn(true);
        when(helper.getStepCollector(element)).thenReturn(collector);

        new ReferenceSearch(helper).processQuery(searchParameters, psiReference -> false);

        verify(helper, times(1)).getPsiElements(collector, element);
    }
}