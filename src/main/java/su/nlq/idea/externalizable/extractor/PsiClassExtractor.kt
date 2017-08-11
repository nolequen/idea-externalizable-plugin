package su.nlq.idea.externalizable.extractor

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import java.util.*
import java.util.function.Supplier

class PsiClassExtractor(private val event: AnActionEvent) : Supplier<Optional<PsiClass>> {

  override fun get(): Optional<PsiClass> {
    return offset().flatMap { element(it) }.map { PsiTreeUtil.getParentOfType(it, PsiClass::class.java) }
  }

  private fun offset(): Optional<Int> {
    return Optional.ofNullable(event.getData(CommonDataKeys.EDITOR)?.caretModel?.offset)
  }

  private fun element(offset: Int): Optional<PsiElement> {
    return PsiJavaFileExtractor(event).get().flatMap { Optional.ofNullable(it.findElementAt(offset)) }
  }
}
