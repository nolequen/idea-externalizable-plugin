package su.nlq.idea.externalizable.modification;

import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElementFactory;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Stream;

public final class NoArgsConstructor implements PsiClassModification {

  @Override
  public void accept(@NotNull PsiClass psiClass) {
    if (Stream.of(psiClass.getConstructors()).noneMatch(c -> c.getParameterList().getParametersCount() == 0)) {
      final PsiElementFactory elementFactory = JavaPsiFacade.getElementFactory(psiClass.getProject());
      psiClass.add(elementFactory.createConstructor("public " + psiClass.getName(), psiClass));
    }
  }
}
