package su.nlq.idea.externalizable.modification

import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiModifier
import com.intellij.psi.codeStyle.JavaCodeStyleManager

class InsertMethods : PsiClassModification {

    override fun modify(psiClass: PsiClass) {
        val elementFactory = JavaPsiFacade.getElementFactory(psiClass.project)
        val codeStyleManager = JavaCodeStyleManager.getInstance(psiClass.project)
        val fields = fields(psiClass)
        MethodTextGenerator.values()
            .map { it.apply(fields) }
            .map { elementFactory.createMethodFromText(it, psiClass) }
            .map { psiClass.add(it) }
            .forEach { codeStyleManager.shortenClassReferences(it) }
    }

    companion object {
        private val forbiddenModifiers = listOf(PsiModifier.TRANSIENT, PsiModifier.STATIC)
    }

    private fun fields(psiClass: PsiClass) = psiClass.fields.filter {
        val modifiers = it.modifierList
        modifiers != null && forbiddenModifiers.none { modifiers.hasModifierProperty(it) }
    }
}
