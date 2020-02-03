package dev.booij.shoot_plugin;

import com.intellij.codeInsight.navigation.actions.GotoDeclarationHandlerBase;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.jetbrains.php.lang.psi.elements.Field;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.jetbrains.twig.TwigFileType;
import com.jetbrains.twig.TwigTokenTypes;
import org.jetbrains.annotations.Nullable;


public class ShootModelGotoDeclarationHandler extends GotoDeclarationHandlerBase {
    @Nullable
    @Override
    public PsiElement getGotoDeclarationTarget(@Nullable PsiElement psiElement, Editor editor) {
        if (psiElement == null) {
            return null;
        }

        if (!(psiElement instanceof LeafPsiElement)) {
            return null;
        }

        if (!psiElement.getContainingFile().getFileType().equals(TwigFileType.INSTANCE)) {
            return null;
        }

        if (((LeafPsiElement) psiElement).getElementType() == TwigTokenTypes.IDENTIFIER) {
            return this.getModelVariable(psiElement);
        }

        if (((LeafPsiElement) psiElement).getElementType() == TwigTokenTypes.STRING_TEXT) {
            PhpClass modelClass = dev.booij.shoot_plugin.ShootHelper.getModelClassFromString(psiElement.getText(), psiElement.getProject());

            if (modelClass == null) {
                return null;
            }

            return modelClass.getContainingFile();
        }

        return null;
    }

    @Nullable
    private PsiElement getModelVariable(PsiElement psiElement) {
        PhpClass model = dev.booij.shoot_plugin.ShootHelper.findModel(psiElement.getContainingFile());

        if (model == null) {
            return null;
        }

        String variableName = psiElement.getText();

        for (Field field: model.getFields()) {
            if (field.getName().equals(variableName) && !field.getModifier().isPrivate()) {
                return field;
            }
        }

        return null;
    }
}
