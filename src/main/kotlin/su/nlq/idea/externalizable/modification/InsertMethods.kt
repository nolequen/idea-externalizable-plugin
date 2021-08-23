package su.nlq.idea.externalizable.modification

import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiField
import com.intellij.psi.codeStyle.JavaCodeStyleManager
import su.nlq.idea.externalizable.generator.MethodTextGenerator
import java.util.*

class InsertMethods(private val fields: Collection<PsiField>) : PsiClassModification {

    override fun accept(psiClass: PsiClass) {
        val project = psiClass.project
        val elementFactory = JavaPsiFacade.getElementFactory(project)
        val codeStyleManager = JavaCodeStyleManager.getInstance(project)
        Arrays.stream(methodGenerators)
            .map { it.apply(fields) }
            .map { elementFactory.createMethodFromText(it, psiClass) }
            .map { psiClass.add(it) }
            .forEach { codeStyleManager.shortenClassReferences(it) }
    }

    companion object {
        private val methodGenerators = MethodTextGenerator.values()
    }
}
