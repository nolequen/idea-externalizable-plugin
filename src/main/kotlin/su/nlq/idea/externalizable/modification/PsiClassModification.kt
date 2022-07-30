package su.nlq.idea.externalizable.modification

import com.intellij.psi.PsiClass

interface PsiClassModification {

    fun modify(psiClass: PsiClass)
}
