package su.nlq.idea.externalizable

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiJavaFile
import com.intellij.psi.util.PsiTreeUtil
import su.nlq.idea.externalizable.modification.*

class GenerateAction : AnAction() {

    override fun actionPerformed(event: AnActionEvent) {
        psiClass(event)?.let { psiClass ->
            psiJavaFile(event)?.let { psiJavaFile ->
                WriteCommandAction.writeCommandAction(psiClass.project, psiClass.containingFile).run<Throwable> {
                    listOf(
                        AddImports(psiJavaFile),
                        CleanupMethods(),
                        NoArgsConstructor(),
                        ImplementExternalizable(),
                        InsertMethods()
                    )
                        .forEach { it.modify(psiClass) }
                }
            }
        }
    }

    private fun psiClass(event: AnActionEvent) =
        event.getData(CommonDataKeys.EDITOR)?.caretModel?.offset
            ?.let { psiJavaFile(event)?.findElementAt(it) }
            ?.let { PsiTreeUtil.getParentOfType(it, PsiClass::class.java) }

    private fun psiJavaFile(event: AnActionEvent) = event.getData(CommonDataKeys.PSI_FILE) as? PsiJavaFile
}
