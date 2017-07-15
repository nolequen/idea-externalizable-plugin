package su.nlq.idea.externalizable.modification;

import com.intellij.openapi.project.Project;
import com.intellij.psi.CommonClassNames;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.codeStyle.CodeStyleSettingsManager;
import com.intellij.psi.impl.source.codeStyle.ImportHelper;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.Optional;

public final class AddImports implements PsiClassModification {
  @NotNull
  private final PsiJavaFile file;

  public AddImports(@NotNull PsiFile file) {
    this.file = (PsiJavaFile) file;
  }

  @Override
  public void accept(@NotNull PsiClass psiClass) {
    final Project project = psiClass.getProject();
    final CodeStyleSettings settings = CodeStyleSettingsManager.getSettings(project);
    final ImportHelper importHelper = new ImportHelper(settings);

    Arrays.asList(
        CommonClassNames.JAVA_IO_EXTERNALIZABLE,
        ObjectInput.class.getName(),
        ObjectOutput.class.getName(),
        IOException.class.getName(),
        ClassNotFoundException.class.getName()
    ).stream()
        .map(className -> new TypeFinder(project, className))
        .map(TypeFinder::get)
        .map(type -> Optional.ofNullable(type.resolve()))
        .forEach(resolved -> resolved.ifPresent(reference -> importHelper.addImport(file, reference)));
  }
}
