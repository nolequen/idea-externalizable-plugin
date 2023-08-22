package su.nlq.idea.externalizable.modification

import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiClass
import com.intellij.psi.codeStyle.JavaCodeStyleManager

class InsertMethods : PsiClassModification {

    override fun modify(psiClass: PsiClass) {
        val elementFactory = JavaPsiFacade.getElementFactory(psiClass.project)
        val codeStyleManager = JavaCodeStyleManager.getInstance(psiClass.project)
        val generator = MethodTextGenerator(psiClass)
        listOf(generator.write(), generator.read())
                .forEach { psiClass.add(elementFactory.createMethodFromText(it, psiClass)) }
        codeStyleManager.shortenClassReferences(psiClass)
    }
}
