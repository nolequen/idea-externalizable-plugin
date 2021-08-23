package su.nlq.idea.externalizable.modification

import com.intellij.psi.CommonClassNames
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiClass

class ImplementExternalizable : PsiClassModification {

    override fun accept(psiClass: PsiClass) {
        val project = psiClass.project
        val externalizable = TypeFinder(project, CommonClassNames.JAVA_IO_EXTERNALIZABLE).get()

        if (!psiClass.implementsListTypes.asList().contains(externalizable)) {
            psiClass.implementsList?.add(
                JavaPsiFacade.getInstance(project).elementFactory.createReferenceElementByType(externalizable)
            )
        }
    }
}
