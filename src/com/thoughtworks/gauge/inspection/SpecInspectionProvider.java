package com.thoughtworks.gauge.inspection;

import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.psi.tree.IElementType;
import com.thoughtworks.gauge.language.psi.SpecStep;
import com.thoughtworks.gauge.language.token.SpecTokenTypes;

import java.util.Arrays;
import java.util.List;

public class SpecInspectionProvider extends GaugeInspectionProvider {
    @Override
    PsiElement getElement(PsiElement element) {
        if (element == null) return null;
        if (element instanceof SpecStep) return element;
        List<IElementType> types = Arrays.asList(SpecTokenTypes.SPEC_HEADING, SpecTokenTypes.SCENARIO_HEADING);
        if (element instanceof LeafPsiElement && types.contains(((LeafPsiElement) element).getElementType()))
            return element;
        return getElement(element.getParent());
    }
}
