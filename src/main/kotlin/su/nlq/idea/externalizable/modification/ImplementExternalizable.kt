package su.nlq.idea.externalizable.modification

import com.intellij.psi.CommonClassNames
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiClass

class ImplementExternalizable : PsiClassModification {

    override fun modify(psiClass: PsiClass) {
        val externalizable = TypeFinder(psiClass.project, CommonClassNames.JAVA_IO_EXTERNALIZABLE).get()

        if (!psiClass.implementsListTypes.asList().contains(externalizable)) {
            psiClass.implementsList?.add(
                JavaPsiFacade.getInstance(psiClass.project).elementFactory.createReferenceElementByType(externalizable)
            )
        }
    }
}
