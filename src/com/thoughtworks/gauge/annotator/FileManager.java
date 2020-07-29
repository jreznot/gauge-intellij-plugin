package com.thoughtworks.gauge.annotator;

import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.indexing.FileBasedIndex;
import com.thoughtworks.gauge.Constants;
import com.thoughtworks.gauge.language.ConceptFileType;
import com.thoughtworks.gauge.language.SpecFileType;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static com.intellij.psi.search.GlobalSearchScope.moduleScope;

public class FileManager {
    public static List<PsiFile> getAllJavaFiles(Module module) {
        Collection<VirtualFile> javaVirtualFiles = FileTypeIndex.getFiles(JavaFileType.INSTANCE, moduleScope(module));
        List<PsiFile> javaFiles = new ArrayList<>();

        for (VirtualFile javaVFile : javaVirtualFiles) {
            PsiFile file = PsiManager.getInstance(module.getProject()).findFile(javaVFile);
            if (file != null && PsiTreeUtil.findChildrenOfType(file, PsiClass.class).size() > 0) {
                javaFiles.add(file);
            }
        }
        javaFiles.sort((o1, o2) -> FileManager.getJavaFileName(o1).compareToIgnoreCase(FileManager.getJavaFileName(o2)));
        return javaFiles;
    }

    public static List<PsiFile> getAllConceptFiles(Project project) {
        Collection<VirtualFile> virtualFiles = FileTypeIndex.getFiles(ConceptFileType.INSTANCE, GlobalSearchScope.projectScope(project));
        List<PsiFile> files = new ArrayList<>();

        for (VirtualFile ConceptVFile : virtualFiles) {
            PsiFile file = PsiManager.getInstance(project).findFile(ConceptVFile);
            if (file != null) {
                files.add(file);
            }
        }
        files.sort((o1, o2) -> o1.getName().compareToIgnoreCase(o2.getName()));
        return files;
    }

    public static String getJavaFileName(PsiFile value) {
        PsiJavaFile javaFile = (PsiJavaFile) value;
        if (!javaFile.getPackageName().equals("")) {
            return javaFile.getPackageName() + "." + javaFile.getName();
        }
        return javaFile.getName();
    }

    public static List<VirtualFile> getAllSpecFiles(Project project) {
        Collection<VirtualFile> virtualFiles = FileTypeIndex.getFiles(SpecFileType.INSTANCE, GlobalSearchScope.projectScope(project));
        return new ArrayList<>(virtualFiles);
    }

    public static List<VirtualFile> getConceptFiles(Project project) {
        Collection<VirtualFile> virtualFiles = FileTypeIndex.getFiles(ConceptFileType.INSTANCE, GlobalSearchScope.projectScope(project));
        return new ArrayList<>(virtualFiles);
    }
}
