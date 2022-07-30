package su.nlq.idea.externalizable.modification

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiClassType
import com.intellij.psi.PsiMethod
import java.util.function.Predicate
import java.util.stream.Stream

class CleanupMethods : PsiClassModification {

    override fun modify(psiClass: PsiClass) {
        val project = psiClass.project
        Stream.of(*psiClass.methods)
            .filter(
                MethodNameFilter("readExternal")
                    .and(SingleParameterTypeFilter(project, "java.io.ObjectInput"))
                    .or(
                        MethodNameFilter("writeExternal")
                            .and(SingleParameterTypeFilter(project, "java.io.ObjectOutput"))
                    )
            )
            .forEach { it.delete() }
    }

    private class MethodNameFilter(private val name: String) : Predicate<PsiMethod> {

        override fun test(method: PsiMethod) = method.name == name
    }

    private class SingleParameterTypeFilter(project: Project, type: String) : Predicate<PsiMethod> {
        private val type: PsiClassType = TypeFinder(project, type).get()

        override fun test(method: PsiMethod): Boolean {
            val parameters = method.parameterList.parameters
            return parameters.size == 1 && parameters[0].type == type
        }
    }
}
