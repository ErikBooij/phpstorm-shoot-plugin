package dev.booij.shoot_plugin;

import com.intellij.codeInsight.navigation.actions.GotoDeclarationHandlerBase;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.jetbrains.twig.TwigFileType;
import org.jetbrains.annotations.Nullable;


public class ShootModelGotoDeclarationHandler extends GotoDeclarationHandlerBase {
    @Nullable
    @Override
    public PsiElement getGotoDeclarationTarget(@Nullable PsiElement psiElement, Editor editor) {
        if (psiElement == null) {
            return null;
        }

        if (!psiElement.getContainingFile().getFileType().equals(TwigFileType.INSTANCE)) {
            return null;
        }


        String searchTarget = psiElement.getText().replaceAll("\\\\\\\\", "\\\\");

        PhpIndex index = PhpIndex.getInstance(psiElement.getProject());

        PhpClass[] classes = index.getClassesByFQN(searchTarget).toArray(new PhpClass[0]);

        if (classes.length != 1) {
            return null;
        }

        return classes[0].getContainingFile();
    }
}