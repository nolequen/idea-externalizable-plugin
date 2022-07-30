package su.nlq.idea.externalizable.extractor

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.psi.PsiClass
import com.intellij.psi.util.PsiTreeUtil

class PsiClassSupplier(private val event: AnActionEvent) {

    fun psiClass() =
        event.getData(CommonDataKeys.EDITOR)?.caretModel?.offset
            ?.let { PsiJavaFileSupplier(event).psiJavaFile()?.findElementAt(it) }
            ?.let { PsiTreeUtil.getParentOfType(it, PsiClass::class.java) }
}
