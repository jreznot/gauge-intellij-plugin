package com.thoughtworks.gauge.execution.runner;

import com.intellij.execution.Executor;
import com.intellij.execution.PsiLocation;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.testframework.TestConsoleProperties;
import com.intellij.execution.testframework.actions.AbstractRerunFailedTestsAction;
import com.intellij.execution.testframework.sm.SMCustomMessagesParsing;
import com.intellij.execution.testframework.sm.runner.OutputToGeneralTestEventsConverter;
import com.intellij.execution.testframework.sm.runner.SMTRunnerConsoleProperties;
import com.intellij.execution.testframework.sm.runner.SMTestLocator;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.thoughtworks.gauge.Constants;
import com.thoughtworks.gauge.execution.GaugeRunConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;

public class GaugeConsoleProperties extends SMTRunnerConsoleProperties implements SMCustomMessagesParsing {

    private final ProcessHandler handler;

    public GaugeConsoleProperties(GaugeRunConfiguration config, String gauge, Executor executor, ProcessHandler handler) {
        super(config, gauge, executor);
        this.handler = handler;
        setIdBasedTestTree(true);
        setValueOf(HIDE_PASSED_TESTS, false);
        setValueOf(HIDE_IGNORED_TEST, false);
        setValueOf(SCROLL_TO_SOURCE, true);
        setValueOf(SHOW_INLINE_STATISTICS, false);
        setValueOf(SHOW_STATISTICS, false);
    }

    @Override
    public OutputToGeneralTestEventsConverter createTestEventsConverter(@NotNull String testFrameworkName, @NotNull TestConsoleProperties consoleProperties) {
        return new GaugeOutputToGeneralTestEventsProcessor(testFrameworkName, consoleProperties, handler);
    }

    @Nullable
    @Override
    public AbstractRerunFailedTestsAction createRerunFailedTestsAction(ConsoleView consoleView) {
        GaugeRerunFailedAction action = new GaugeRerunFailedAction(consoleView);
        action.init(this);
        return action;
    }

    @Override
    public boolean isIdBasedTestTree() {
        return true;
    }

    @Nullable
    @Override
    public SMTestLocator getTestLocator() {
        return (protocol, path, project, globalSearchScope) -> {
            try {
                String[] fileInfo = path.split(Constants.SPEC_SCENARIO_DELIMITER);
                VirtualFile file = LocalFileSystem.getInstance().findFileByPath(fileInfo[0]);
                if (file == null) return new ArrayList<>();
                PsiFile psiFile = PsiManager.getInstance(project).findFile(file);
                if (psiFile == null) return new ArrayList<>();
                Document document = PsiDocumentManager.getInstance(project).getDocument(psiFile);
                if (document == null) return new ArrayList<>();
                int line = Integer.parseInt(fileInfo[1]);
                PsiElement element = psiFile.findElementAt(document.getLineStartOffset(line));
                if (element == null) return new ArrayList<>();
                return Collections.singletonList(new PsiLocation<>(element));
            } catch (Exception e) {
                return new ArrayList<>();
            }
        };
    }
}
