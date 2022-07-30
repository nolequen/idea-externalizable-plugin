package su.nlq.idea.externalizable.extractor

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.psi.PsiJavaFile

class PsiJavaFileSupplier(private val event: AnActionEvent) {

    fun psiJavaFile() = event.getData(CommonDataKeys.PSI_FILE) as? PsiJavaFile
}
