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

import com.intellij.find.findUsages.FindUsagesHandler;
import com.intellij.find.findUsages.FindUsagesOptions;
import com.intellij.find.findUsages.JavaFindUsagesHandler;
import com.intellij.find.findUsages.JavaFindUsagesHandlerFactory;
import com.intellij.ide.util.SuperMethodWarningUtil;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.usageView.UsageInfo;
import com.intellij.util.Processor;
import org.jetbrains.annotations.NotNull;

public class StepFindUsagesHandler extends FindUsagesHandler {

    protected StepFindUsagesHandler(@NotNull PsiElement psiElement) {
        super(psiElement);
    }

    @Override
    public boolean processElementUsages(@NotNull PsiElement psiElement,
                                        @NotNull Processor<? super UsageInfo> processor,
                                        @NotNull FindUsagesOptions findUsagesOptions) {
        ApplicationManager.getApplication().invokeLater(() -> runFindUsageReadAction(psiElement, processor, findUsagesOptions));
        return true;
    }

    private void runFindUsageReadAction(final PsiElement psiElement, final Processor<? super UsageInfo> processor, final FindUsagesOptions findUsagesOptions) {
        ApplicationManager.getApplication().runReadAction(() -> {
            if (psiElement instanceof PsiMethod) {
                PsiMethod[] psiMethods = SuperMethodWarningUtil.checkSuperMethods((PsiMethod) psiElement, CustomFUH.getActionString());
                if (psiMethods.length < 1) return;
                for (PsiElement method : psiMethods)
                    StepFindUsagesHandler.this.processUsages(method, processor, findUsagesOptions);
            }
            StepFindUsagesHandler.this.processUsages(psiElement, processor, findUsagesOptions);
        });
    }

    public void processUsages(final PsiElement psiElement, final Processor<? super UsageInfo> processor, final FindUsagesOptions findUsagesOptions) {
        super.processElementUsages(psiElement, processor, findUsagesOptions);
    }

    private static class CustomFUH extends JavaFindUsagesHandler {
        public CustomFUH(@NotNull PsiElement psiElement, @NotNull JavaFindUsagesHandlerFactory javaFindUsagesHandlerFactory) {
            super(psiElement, javaFindUsagesHandlerFactory);
        }

        public static @NotNull String getActionString() {
            return ACTION_STRING;
        }
    }
}
