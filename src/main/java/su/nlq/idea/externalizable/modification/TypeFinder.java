package su.nlq.idea.externalizable.modification;

import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.search.GlobalSearchScope;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public final class TypeFinder implements Supplier<PsiClassType> {
  @NotNull
  private final Project project;
  @NotNull
  private final String name;

  public TypeFinder(@NotNull Project project, @NotNull String name) {
    this.project = project;
    this.name = name;
  }

  @NotNull
  @Override
  public PsiClassType get() {
    final GlobalSearchScope scope = GlobalSearchScope.allScope(project);
    final PsiElementFactory factory = JavaPsiFacade.getInstance(project).getElementFactory();
    return factory.createTypeByFQClassName(name, scope);
  }
}
