package com.thoughtworks.gauge.util;

import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.thoughtworks.gauge.AfterScenario;
import com.thoughtworks.gauge.AfterSpec;
import com.thoughtworks.gauge.AfterStep;
import com.thoughtworks.gauge.AfterSuite;
import com.thoughtworks.gauge.BeforeScenario;
import com.thoughtworks.gauge.BeforeSpec;
import com.thoughtworks.gauge.BeforeStep;
import com.thoughtworks.gauge.BeforeSuite;

import java.util.Arrays;
import java.util.List;

public class HookUtil {
    private static final List<String> hooks = Arrays.asList(
            BeforeSuite.class.getCanonicalName(),
            BeforeSpec.class.getCanonicalName(),
            BeforeScenario.class.getCanonicalName(),
            BeforeStep.class.getCanonicalName(),
            AfterSuite.class.getCanonicalName(),
            AfterSpec.class.getCanonicalName(),
            AfterScenario.class.getCanonicalName(),
            AfterStep.class.getCanonicalName());

    public static boolean isHook(PsiElement element) {
        if (!(element instanceof PsiMethod)) return false;
        for (PsiAnnotation annotation : ((PsiMethod) element).getModifierList().getAnnotations())
            if (hooks.contains(annotation.getQualifiedName()))
                return true;
        return false;
    }
}
