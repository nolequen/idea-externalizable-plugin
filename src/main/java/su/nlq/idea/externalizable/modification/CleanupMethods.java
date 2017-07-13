package su.nlq.idea.externalizable.modification;

import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;
import java.util.stream.Stream;

public final class CleanupMethods implements PsiClassModification {

  @Override
  public void accept(@NotNull PsiClass psiClass) {
    final Project project = psiClass.getProject();
    Stream.of(psiClass.getMethods())
        .filter(
            new MethodNameFilter("readExternal").and(new SingleParameterTypeFilter(project, "java.io.ObjectInput"))
                .or(new MethodNameFilter("writeExternal").and(new SingleParameterTypeFilter(project, "java.io.ObjectOutput"))))
        .forEach(PsiElement::delete);
  }

  private static final class MethodNameFilter implements Predicate<PsiMethod> {
    @NotNull
    private final String name;

    public MethodNameFilter(@NotNull String name) {
      this.name = name;
    }

    @Override
    public boolean test(@NotNull PsiMethod method) {
      return method.getName().equals(name);
    }
  }

  private static final class SingleParameterTypeFilter implements Predicate<PsiMethod> {
    @NotNull
    private final PsiClassType type;

    public SingleParameterTypeFilter(@NotNull Project project, @NotNull String type) {
      this.type = new TypeFinder(project, type).get();
    }

    @Override
    public boolean test(@NotNull PsiMethod method) {
      final PsiParameter[] parameters = method.getParameterList().getParameters();
      return parameters.length == 1 && parameters[0].getType().equals(type);
    }
  }
}
