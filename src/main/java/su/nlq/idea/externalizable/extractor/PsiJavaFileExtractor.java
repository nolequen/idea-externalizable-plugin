package su.nlq.idea.externalizable.extractor;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Supplier;

public final class PsiJavaFileExtractor implements Supplier<Optional<PsiJavaFile>> {
  @NotNull
  private final AnActionEvent event;

  public PsiJavaFileExtractor(@NotNull AnActionEvent event) {
    this.event = event;
  }

  @NotNull
  @Override
  public Optional<PsiJavaFile> get() {
    final PsiFile psiFile = event.getData(CommonDataKeys.PSI_FILE);
    return psiFile instanceof PsiJavaFile ? Optional.of((PsiJavaFile) psiFile) : Optional.empty();
  }
}
