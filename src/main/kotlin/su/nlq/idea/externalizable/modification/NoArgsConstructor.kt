package su.nlq.idea.externalizable.modification

import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiClass
import java.util.stream.Stream

class NoArgsConstructor : PsiClassModification {

    override fun accept(psiClass: PsiClass) {
        if (Stream.of(*psiClass.constructors).noneMatch { it.getParameterList().getParametersCount() == 0 }) {
            val elementFactory = JavaPsiFacade.getElementFactory(psiClass.project)
            psiClass.add(elementFactory.createConstructor("public ${psiClass.name!!}", psiClass))
        }
    }
}
