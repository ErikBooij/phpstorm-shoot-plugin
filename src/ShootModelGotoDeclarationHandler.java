import com.intellij.codeInsight.navigation.actions.GotoDeclarationHandlerBase;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import org.jetbrains.annotations.Nullable;

public class ShootModelGotoDeclarationHandler extends GotoDeclarationHandlerBase {
    @Nullable
    @Override
    public PsiElement getGotoDeclarationTarget(@Nullable PsiElement psiElement, Editor editor) {
        if (psiElement == null) {
            return null;
        }

        if (!psiElement.getContainingFile().getFileType().getName().equals("Twig")) {
            return null;
        }

        String searchTarget = psiElement.getText();
        searchTarget = searchTarget.substring(searchTarget.lastIndexOf('\\') + 1).trim();
        Project project = psiElement.getProject();

        PsiFile[] files = FilenameIndex.getFilesByName(project, searchTarget + ".php", GlobalSearchScope.allScope(project));

        if (files.length != 1) {
            return null;
        }

        return files[0];
    }
}