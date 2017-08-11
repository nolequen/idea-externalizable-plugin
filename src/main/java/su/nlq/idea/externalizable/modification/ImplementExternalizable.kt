package su.nlq.idea.externalizable.modification

import com.intellij.psi.CommonClassNames
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiClass
import java.util.stream.Stream

class ImplementExternalizable : PsiClassModification {

  override fun accept(psiClass: PsiClass) {
    val project = psiClass.project
    val externalizable = TypeFinder(project, CommonClassNames.JAVA_IO_EXTERNALIZABLE).get()
    if (Stream.of(*psiClass.implementsListTypes).anyMatch { externalizable == it }) {
      return
    }
    psiClass.implementsList?.add(JavaPsiFacade.getInstance(project).elementFactory.createReferenceElementByType(externalizable))
  }
}
