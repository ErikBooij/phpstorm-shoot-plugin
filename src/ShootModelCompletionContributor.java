package dev.booij.shoot_plugin;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.util.TextRange;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.ProcessingContext;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.elements.Field;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.jetbrains.twig.TwigLanguage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ShootModelCompletionContributor extends CompletionContributor {
    public ShootModelCompletionContributor() {
        extend(CompletionType.BASIC,
                PlatformPatterns.psiElement(PsiElement.class).withLanguage(TwigLanguage.INSTANCE),
                new CompletionProvider<CompletionParameters>() {
                    public void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet resultSet) {
                        PhpClass modelClass = this.getModelClass(parameters.getOriginalFile());

                        if (modelClass == null) {
                            return;
                        }

                        Field[] modelProperties = modelClass.getFields().toArray(new Field[0]);

                        for (Field field: modelProperties) {
                            if (!field.getModifier().isPrivate()) {
                                resultSet.addElement(LookupElementBuilder.create(field.getName()));
                            }
                        }
                    }

                    @Nullable
                    private PhpClass getModelClass(PsiFile file) {
                        Document document = file.getViewProvider().getDocument();
                        TextRange textRange = new TextRange(document.getLineStartOffset(0), document.getLineEndOffset(0));
                        String firstLine = document.getText(textRange);

                        String patternString = "\\{% model '(.*)' %}";

                        Pattern pattern = Pattern.compile(patternString);

                        Matcher matcher = pattern.matcher(firstLine);

                        if (!matcher.lookingAt()) {
                            return null;
                        }

                        String model = matcher.group(1).replaceAll("\\\\{2}", "\\\\");

                        PhpClass[] classes = PhpIndex.getInstance(file.getProject()).getClassesByFQN(model).toArray(new PhpClass[0]);

                        if (classes.length != 1) {
                            return null;
                        }

                        return classes[0];
                    }
                }
        );
    }
}
