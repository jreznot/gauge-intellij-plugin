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

package com.thoughtworks.gauge.extract;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogBuilder;
import com.intellij.ui.TextFieldWithAutoCompletionListProvider;
import com.thoughtworks.gauge.Constants;
import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

public class ExtractConceptDialog extends JDialog {
    private JPanel contentPane;
    private com.intellij.ui.TextFieldWithAutoCompletion conceptName;
    private JTextArea steps;
    private JComboBox<String> files;
    private JTextField newFile;
    private JButton OKButton;
    private JButton cancelButton;
    private JLabel errors;
    private Project project;
    private List<String> args;
    private boolean cancelled = true;
    private DialogBuilder builder;

    public ExtractConceptDialog(Project project, List<String> args) {
        this.project = project;
        this.args = args;
        setContentPane(contentPane);
        setModal(true);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });
        setProperties();
    }

    public void setData(String data, List<String> files, DialogBuilder builder) {
        this.builder = builder;
        this.steps.setColumns(50);
        this.steps.setRows(10);
        this.steps.setEditable(false);
        this.steps.setText(data);
        for (String file : files) this.files.addItem(file);
    }

    public ExtractConceptInfo getInfo() {
        String fileName = this.files.getSelectedItem().toString();
        if (fileName.equals(ExtractConceptInfoCollector.CREATE_NEW_FILE)) fileName = this.newFile.getText();
        return new ExtractConceptInfo(this.conceptName.getText(), fileName.trim(), cancelled);
    }

    private void setProperties() {
        contentPane.registerKeyboardAction(getCancelAction(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        this.newFile.setVisible(false);
        this.conceptName.setPlaceholder("Enter Concept Name. Example: my new \"concept\"");
        this.files.addActionListener(e -> {
            ExtractConceptDialog.this.newFile.setVisible(false);
            if (ExtractConceptDialog.this.files.getSelectedItem().toString().equals(ExtractConceptInfoCollector.CREATE_NEW_FILE))
                ExtractConceptDialog.this.newFile.setVisible(true);
        });
        this.cancelButton.addActionListener(getCancelAction());
        this.OKButton.addActionListener(getOKAction());
    }

    @NotNull
    private ActionListener getCancelAction() {
        return e -> onCancel();
    }

    @NotNull
    private ActionListener getOKAction() {
        return e -> {
            if (conceptName.getText().trim().equals(""))
                errors.setText("Please enter concept name.");
            else if (newFile.isVisible() && (FilenameUtils.removeExtension(newFile.getText().trim()).isEmpty() ||
                    !FilenameUtils.getExtension(newFile.getText().trim()).equals(Constants.CONCEPT_EXTENSION)))
                errors.setText("Please select filename from the dropdown or provide a new valid file name with `.cpt` extension.");
            else {
                cancelled = false;
                builder.getWindow().setVisible(false);
            }
        };
    }

    private void onCancel() {
        builder.getWindow().setVisible(false);
        dispose();
    }

    private void createUIComponents() {
        this.conceptName = new com.intellij.ui.TextFieldWithAutoCompletion<>(this.project, getAutoCompleteTextField(this.args), true, "");
    }

    private TextFieldWithAutoCompletionListProvider<String> getAutoCompleteTextField(final List<String> dirNames) {
        return new TextFieldWithAutoCompletionListProvider<String>(dirNames) {
            @Nullable
            @Override
            protected Icon getIcon(String o) {
                return null;
            }

            @NotNull
            @Override
            protected String getLookupString(String o) {
                return o;
            }

            @Nullable
            @Override
            protected String getTailText(String o) {
                return null;
            }

            @Nullable
            @Override
            protected String getTypeText(String o) {
                return null;
            }

            @Override
            public int compare(String o, String t1) {
                return 0;
            }
        };
    }
}