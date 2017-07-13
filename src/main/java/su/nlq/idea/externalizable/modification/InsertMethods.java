package su.nlq.idea.externalizable.modification;

import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiField;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import org.jetbrains.annotations.NotNull;
import su.nlq.idea.externalizable.generator.MethodTextGenerator;

import java.util.Arrays;
import java.util.Collection;

public final class InsertMethods implements PsiClassModification {
  @NotNull
  private static final MethodTextGenerator[] methodGenerators = MethodTextGenerator.values();
  @NotNull
  private final Collection<PsiField> fields;

  public InsertMethods(@NotNull Collection<PsiField> fields) {
    this.fields = fields;
  }

  @Override
  public void accept(@NotNull PsiClass psiClass) {
    final Project project = psiClass.getProject();
    final PsiElementFactory elementFactory = JavaPsiFacade.getElementFactory(project);
    final JavaCodeStyleManager codeStyleManager = JavaCodeStyleManager.getInstance(project);
    Arrays.stream(methodGenerators)
        .map(generator -> generator.apply(fields))
        .map(methodText -> elementFactory.createMethodFromText(methodText, psiClass))
        .map(psiClass::add)
        .forEach(codeStyleManager::shortenClassReferences);
  }
}
