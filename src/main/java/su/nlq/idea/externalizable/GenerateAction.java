package su.nlq.idea.externalizable;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.command.WriteCommandAction;
import org.jetbrains.annotations.NotNull;
import su.nlq.idea.externalizable.extractor.PsiClassExtractor;
import su.nlq.idea.externalizable.extractor.PsiJavaFileExtractor;
import su.nlq.idea.externalizable.extractor.SerializableFieldsExtractor;
import su.nlq.idea.externalizable.modification.*;

import java.util.Arrays;
import java.util.Collection;

public final class GenerateAction extends AnAction {

  @Override
  public void actionPerformed(@NotNull AnActionEvent event) {
    new PsiClassExtractor(event).get().ifPresent(psiClass ->
        new PsiJavaFileExtractor(event).get().ifPresent(psiJavaFile -> {
          final Collection<PsiClassModification> modifications = Arrays.asList(
              new AddImports(psiJavaFile),
              new CleanupMethods(),
              new NoArgsConstructor(),
              new ImplementExternalizable(),
              new InsertMethods(new SerializableFieldsExtractor(psiClass).get())
          );
          new WriteCommandAction.Simple(psiClass.getProject(), psiClass.getContainingFile()) {
            @Override
            protected void run() {
              modifications.forEach(modification -> modification.accept(psiClass));
            }
          }.execute();
        })
    );
  }
}
