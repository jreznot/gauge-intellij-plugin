package com.thoughtworks.gauge.idea.template;

import com.intellij.codeInsight.template.TemplateActionContext;
import com.intellij.codeInsight.template.TemplateContextType;
import com.thoughtworks.gauge.language.ConceptFileType;
import com.thoughtworks.gauge.language.SpecFileType;
import org.jetbrains.annotations.NotNull;

public class LiveTemplateContext extends TemplateContextType {
    protected LiveTemplateContext() {
        super("GAUGE", "Gauge");
    }

    @Override
    public boolean isInContext(@NotNull TemplateActionContext ctx) {
        return ctx.getFile().getFileType() instanceof SpecFileType
                || ctx.getFile().getFileType() instanceof ConceptFileType;
    }
}
