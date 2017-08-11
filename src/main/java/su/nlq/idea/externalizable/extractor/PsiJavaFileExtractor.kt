package su.nlq.idea.externalizable.extractor

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.psi.PsiJavaFile
import java.util.*
import java.util.function.Supplier

class PsiJavaFileExtractor(private val event: AnActionEvent) : Supplier<Optional<PsiJavaFile>> {

  override fun get(): Optional<PsiJavaFile> {
    return Optional.ofNullable(event.getData(CommonDataKeys.PSI_FILE) as? PsiJavaFile)
  }
}
