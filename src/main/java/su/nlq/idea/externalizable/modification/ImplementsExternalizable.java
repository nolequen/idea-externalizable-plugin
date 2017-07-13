package su.nlq.idea.externalizable.modification;

import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Stream;

public final class ImplementsExternalizable implements PsiClassModification {

  @Override
  public void accept(@NotNull PsiClass psiClass) {
    final Project project = psiClass.getProject();
    final PsiClassType externalizable = new TypeFinder(project, CommonClassNames.JAVA_IO_EXTERNALIZABLE).get();
    if (Stream.of(psiClass.getImplementsListTypes()).anyMatch(externalizable::equals)) {
      return;
    }
    final PsiReferenceList refs = psiClass.getImplementsList();
    if (refs != null) {
      refs.add(JavaPsiFacade.getInstance(project).getElementFactory().createReferenceElementByType(externalizable));
    }
  }
}
