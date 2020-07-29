/*----------------------------------------------------------------
 *  Copyright (c) ThoughtWorks, Inc.
 *  Licensed under the Apache License, Version 2.0
 *  See LICENSE.txt in the project root for license information.
 *----------------------------------------------------------------*/

package com.thoughtworks.gauge;

import com.intellij.codeInsight.editorActions.enter.EnterHandlerDelegate;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.thoughtworks.gauge.util.GaugeUtil;
import org.jetbrains.annotations.NotNull;

public class GaugeEnterHandlerDelegate implements EnterHandlerDelegate {
    @Override
    public Result preprocessEnter(@NotNull PsiFile psiFile, @NotNull Editor editor,
                                  @NotNull Ref<Integer> ref,
                                  @NotNull Ref<Integer> ref1, @NotNull DataContext dataContext,
                                  EditorActionHandler editorActionHandler) {
        return Result.Continue;
    }

    @Override
    public Result postProcessEnter(@NotNull PsiFile psiFile, Editor editor, @NotNull DataContext dataContext) {
        Document document = editor.getDocument();
        VirtualFile file = FileDocumentManager.getInstance().getFile(document);
        if (file != null && GaugeUtil.isGaugeFile(file)) {
            FileDocumentManager.getInstance().saveDocumentAsIs(document);
        }
        return null;
    }
}
