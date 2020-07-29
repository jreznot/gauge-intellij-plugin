package com.thoughtworks.gauge.extract;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.thoughtworks.gauge.extract.stepBuilder.StepsBuilder;
import com.thoughtworks.gauge.undo.UndoHandler;
import gauge.messages.Api;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ExtractConceptHandler {
    private StepsBuilder builder;
    private PsiFile psiFile;

    public void invoke(@NotNull final Project project, @NotNull final Editor editor, @NotNull final PsiFile psiFile) {
        this.psiFile = psiFile;
        try {
            List<PsiElement> steps = getSteps(editor, this.psiFile);
            if (steps.size() == 0)
                throw new RuntimeException("Cannot Extract to Concept, selected text contains invalid elements");
            ExtractConceptInfoCollector collector = new ExtractConceptInfoCollector(editor, builder.getTextToTableMap(), steps, project);
            ExtractConceptInfo info = collector.getAllInfo();
            if (!info.cancelled) {
                VirtualFileManager.getInstance().refreshWithoutFileWatcher(false);
                FileDocumentManager.getInstance().saveAllDocuments();
                Api.ExtractConceptResponse response = makeExtractConceptRequest(steps, info.fileName, info.conceptName, false, psiFile);
                if (!response.getIsSuccess()) throw new RuntimeException(response.getError());
                new UndoHandler(response.getFilesChangedList(), project, "Extract Concept").handle();
            }
        } catch (Exception e) {
            Messages.showErrorDialog(e.getMessage(), "Extract To Concept");
        }
    }

    private Api.ExtractConceptResponse makeExtractConceptRequest(List<PsiElement> specSteps, String fileName,
                                                                 String concept, boolean refactorOtherUsages,
                                                                 PsiFile element) {
        PsiElement firstStep = specSteps.get(0);
        int startLine = StringUtil.offsetToLineNumber(psiFile.getText(), firstStep.getTextOffset()) + 1;
        PsiElement lastStep = specSteps.get(specSteps.size() - 1);
        int endLine = StringUtil.offsetToLineNumber(psiFile.getText(), lastStep.getTextOffset() + lastStep.getTextLength());
        Api.textInfo textInfo = gauge.messages.Api.textInfo.newBuilder().setFileName(psiFile.getVirtualFile().getPath()).setStartingLineNo(startLine).setEndLineNo(endLine).build();
        ExtractConceptRequest request = new ExtractConceptRequest(fileName, concept, refactorOtherUsages, textInfo);
        request.convertToSteps(specSteps, builder.getTableMap());
        return request.makeExtractConceptRequest(element);
    }

    private List<PsiElement> getSteps(Editor editor, PsiFile psiFile) {
        builder = StepsBuilder.getBuilder(editor, psiFile);
        return builder.build();
    }
}