package su.nlq.idea.externalizable.extractor;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Supplier;

public final class PsiClassExtractor implements Supplier<Optional<PsiClass>> {
  @NotNull
  private final AnActionEvent event;

  public PsiClassExtractor(@NotNull AnActionEvent event) {
    this.event = event;
  }

  @NotNull
  @Override
  public Optional<PsiClass> get() {
    return offset().flatMap(this::element).map(element -> PsiTreeUtil.getParentOfType(element, PsiClass.class));
  }

  @NotNull
  private Optional<Integer> offset() {
    final Editor editor = event.getData(CommonDataKeys.EDITOR);
    return editor == null ? Optional.empty() : Optional.of(editor.getCaretModel().getOffset());
  }

  @NotNull
  private Optional<PsiElement> element(int offset) {
    final PsiFile psiFile = event.getData(CommonDataKeys.PSI_FILE);
    return psiFile == null ? Optional.empty() : Optional.ofNullable(psiFile.findElementAt(offset));
  }
}
