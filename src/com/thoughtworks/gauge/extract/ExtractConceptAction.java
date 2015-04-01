package com.thoughtworks.gauge.extract;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.thoughtworks.gauge.util.StepUtil;
import org.jetbrains.annotations.NotNull;

public class ExtractConceptAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        DataContext dataContext = anActionEvent.getDataContext();
        Editor editor = CommonDataKeys.EDITOR.getData(dataContext);
        PsiFile file = CommonDataKeys.PSI_FILE.getData(dataContext);
        Project project = CommonDataKeys.PROJECT.getData(dataContext);
        if (file == null || project == null || editor == null || !StepUtil.isGaugeFileExtension(file.getVirtualFile().getExtension())) {
            return;
        }
        new ExtractConceptHandler().invoke(project,editor,file,dataContext);
    }
}
