package su.nlq.idea.externalizable.modification

import com.intellij.openapi.project.Project
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.search.GlobalSearchScope

fun Project.type(name: String) = JavaPsiFacade.getInstance(this).elementFactory.createTypeByFQClassName(
    name,
    GlobalSearchScope.allScope(this)
)
