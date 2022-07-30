package su.nlq.idea.externalizable.modification

import com.intellij.psi.PsiClass

fun interface PsiClassModification {

    fun modify(psiClass: PsiClass)
}
