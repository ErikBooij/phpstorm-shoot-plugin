package dev.booij.shoot_plugin;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.util.ProcessingContext;
import com.jetbrains.php.lang.psi.elements.Field;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.jetbrains.twig.TwigLanguage;
import org.jetbrains.annotations.NotNull;

public class ShootModelCompletionContributor extends CompletionContributor {
    public ShootModelCompletionContributor() {
        extend(CompletionType.BASIC,
                PlatformPatterns.psiElement(PsiElement.class).withLanguage(TwigLanguage.INSTANCE),
                new CompletionProvider<CompletionParameters>() {
                    public void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet resultSet) {
                        PhpClass modelClass = dev.booij.shoot_plugin.ShootHelper.findModel(parameters.getOriginalFile());

                        if (modelClass == null) {
                            return;
                        }

                        for (Field field : modelClass.getFields()) {
                            if (!field.getModifier().isPrivate()) {
                                resultSet.addElement(LookupElementBuilder.create(field.getName()));
                            }
                        }
                    }
                }
        );
    }
}
