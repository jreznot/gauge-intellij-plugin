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

package com.thoughtworks.gauge.findUsages.helper;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.SearchScope;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase;
import com.thoughtworks.gauge.StepValue;
import com.thoughtworks.gauge.findUsages.StepCollector;
import com.thoughtworks.gauge.helper.ModuleHelper;
import com.thoughtworks.gauge.language.psi.impl.ConceptStepImpl;
import com.thoughtworks.gauge.language.psi.impl.SpecStepImpl;

import java.util.ArrayList;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ReferenceSearchHelperTest extends LightJavaCodeInsightFixtureTestCase {

    private Project project;
    private ModuleHelper moduleHelper;
    private SearchScope scope;
    private StepCollector collector;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        project = myFixture.getProject();
        moduleHelper = mock(ModuleHelper.class);
        scope = mock(SearchScope.class);
        collector = mock(StepCollector.class);
    }

    public void testShouldFindReferencesOfGaugeElement() {
        SpecStepImpl element = mock(SpecStepImpl.class);
        when(element.getProject()).thenReturn(project);
        ReferencesSearch.SearchParameters searchParameters = new ReferencesSearch.SearchParameters(element, GlobalSearchScope.allScope(project), true);

        when(moduleHelper.isGaugeModule(element)).thenReturn(true);

        boolean shouldFindReferences = new ReferenceSearchHelper(moduleHelper).shouldFindReferences(searchParameters, searchParameters.getElementToSearch());

        assertTrue("Should find reference for SpecStep(Expected: true, Actual: false)", shouldFindReferences);
    }

    public void testShouldNotFindReferencesOfNonGaugeElement() {
        PsiClass element = mock(PsiClass.class);
        when(element.getProject()).thenReturn(project);
        ReferencesSearch.SearchParameters searchParameters = new ReferencesSearch.SearchParameters(element, GlobalSearchScope.allScope(project), true);

        when(moduleHelper.isGaugeModule(element)).thenReturn(true);

        ReferenceSearchHelper referenceSearchHelper = new ReferenceSearchHelper(moduleHelper);
        boolean shouldFindReferences = referenceSearchHelper.shouldFindReferences(searchParameters, searchParameters.getElementToSearch());

        assertFalse("Should find reference for PsiClass(Expected: false, Actual: true)", shouldFindReferences);
    }

    public void testShouldNotFindReferencesWhenUnknownScope() {
        PsiClass element = mock(PsiClass.class);
        when(element.getProject()).thenReturn(project);
        ReferencesSearch.SearchParameters searchParameters = new ReferencesSearch.SearchParameters(element, scope, true);

        when(moduleHelper.isGaugeModule(element)).thenReturn(true);
        when(scope.getDisplayName()).thenReturn(ReferenceSearchHelper.UNKNOWN_SCOPE);

        ReferenceSearchHelper referenceSearchHelper = new ReferenceSearchHelper(moduleHelper);
        boolean shouldFindReferences = referenceSearchHelper.shouldFindReferences(searchParameters, searchParameters.getElementToSearch());

        assertFalse("Should find reference When unknown scope(Expected: false, Actual: true)", shouldFindReferences);
    }

    public void testShouldFindReferencesWhenNotUnknownScope() {
        PsiClass element = mock(PsiClass.class);
        when(element.getProject()).thenReturn(project);
        ReferencesSearch.SearchParameters searchParameters = new ReferencesSearch.SearchParameters(element, scope, true);

        when(moduleHelper.isGaugeModule(element)).thenReturn(true);
        when(scope.getDisplayName()).thenReturn("Other Scope");

        ReferenceSearchHelper referenceSearchHelper = new ReferenceSearchHelper(moduleHelper);
        boolean shouldFindReferences = referenceSearchHelper.shouldFindReferences(searchParameters, searchParameters.getElementToSearch());

        assertFalse("Should find reference When scope is not unknown(Expected: true, Actual: false)", shouldFindReferences);
    }

    public void testGetReferenceElements() {
        SpecStepImpl element = mock(SpecStepImpl.class);
        when(element.getProject()).thenReturn(project);
        StepValue stepValue = new StepValue("hello", "", new ArrayList<>());
        ReferencesSearch.SearchParameters searchParameters = new ReferencesSearch.SearchParameters(element, scope, true);

        when(element.getStepValue()).thenReturn(stepValue);

        ReferenceSearchHelper referenceSearchHelper = new ReferenceSearchHelper();
        referenceSearchHelper.getPsiElements(collector, searchParameters.getElementToSearch());

        verify(collector, times(1)).get("hello");
    }

    public void testGetReferenceElementsForConceptStep() {
        ConceptStepImpl element = mock(ConceptStepImpl.class);
        when(element.getProject()).thenReturn(project);
        StepValue stepValue = new StepValue("# hello", "", new ArrayList<>());
        ReferencesSearch.SearchParameters searchParameters = new ReferencesSearch.SearchParameters(element, scope, true);

        when(element.getStepValue()).thenReturn(stepValue);

        ReferenceSearchHelper referenceSearchHelper = new ReferenceSearchHelper();
        referenceSearchHelper.getPsiElements(collector, searchParameters.getElementToSearch());

        verify(collector, times(1)).get("hello");
    }

    public void testShouldNotFindReferencesIfNotGaugeModule() {
        SpecStepImpl element = mock(SpecStepImpl.class);
        when(element.getProject()).thenReturn(project);
        ReferencesSearch.SearchParameters searchParameters = new ReferencesSearch.SearchParameters(element, scope, true);

        when(moduleHelper.isGaugeModule(element)).thenReturn(false);

        ReferenceSearchHelper referenceSearchHelper = new ReferenceSearchHelper(moduleHelper);
        boolean shouldFindReferences = referenceSearchHelper.shouldFindReferences(searchParameters, searchParameters.getElementToSearch());

        assertFalse("Should find reference for non Gauge Module(Expected: false, Actual: true)", shouldFindReferences);

        verify(scope, never()).getDisplayName();
    }

    public void testShouldFindReferencesIfGaugeModule() {
        SpecStepImpl element = mock(SpecStepImpl.class);
        when(element.getProject()).thenReturn(project);
        ReferencesSearch.SearchParameters searchParameters = new ReferencesSearch.SearchParameters(element, scope, true);

        when(moduleHelper.isGaugeModule(element)).thenReturn(true);
        when(scope.getDisplayName()).thenReturn("Other Scope");

        ReferenceSearchHelper referenceSearchHelper = new ReferenceSearchHelper(moduleHelper);
        referenceSearchHelper.shouldFindReferences(searchParameters, searchParameters.getElementToSearch());

        verify(scope, times(1)).getDisplayName();
    }
}