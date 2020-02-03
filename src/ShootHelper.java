package dev.booij.shoot_plugin;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiFile;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.psi.elements.PhpClass;
import com.jetbrains.twig.TwigFile;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ShootHelper {
    @Nullable
    public static PhpClass findModel(PsiFile psiFile) {
        if (!(psiFile instanceof TwigFile)) {
            return null;
        }

        Document twigDocument = psiFile.getViewProvider().getDocument();

        if (twigDocument == null) {
            return null;
        }

        String firstLine = twigDocument.getText(
                new TextRange(
                        twigDocument.getLineStartOffset(0),
                        twigDocument.getLineEndOffset(0)
                )
        );

        Pattern pattern = Pattern.compile("\\{% model '(.*)' %}");
        Matcher matcher = pattern.matcher(firstLine);

        if (!matcher.lookingAt()) {
            return null;
        }

        return getModelClassFromString(matcher.group(1), psiFile.getProject());
    }

    public static PhpClass getModelClassFromString(String modelClass, Project project) {
        String searchTarget = modelClass.replaceAll("\\\\{2}", "\\\\");

        PhpIndex index = PhpIndex.getInstance(project);

        PhpClass[] classes = index.getClassesByFQN(searchTarget).toArray(new PhpClass[0]);

        if (classes.length != 1) {
            return null;
        }

        return classes[0];
    }
}
