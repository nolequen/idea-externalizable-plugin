package su.nlq.idea.externalizable.modification

import com.intellij.openapi.project.Project
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiClassType
import com.intellij.psi.search.GlobalSearchScope
import java.util.function.Supplier

class TypeFinder(private val project: Project, private val name: String) : Supplier<PsiClassType> {

  override fun get(): PsiClassType {
    val scope = GlobalSearchScope.allScope(project)
    val factory = JavaPsiFacade.getInstance(project).elementFactory
    return factory.createTypeByFQClassName(name, scope)
  }
}
