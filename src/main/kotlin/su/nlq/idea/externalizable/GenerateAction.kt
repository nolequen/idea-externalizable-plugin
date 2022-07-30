package su.nlq.idea.externalizable

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.command.WriteCommandAction
import su.nlq.idea.externalizable.extractor.PsiClassSupplier
import su.nlq.idea.externalizable.extractor.PsiJavaFileSupplier
import su.nlq.idea.externalizable.extractor.SerializableFieldsSupplier
import su.nlq.idea.externalizable.modification.*
import java.util.*

class GenerateAction : AnAction() {

    override fun actionPerformed(event: AnActionEvent) {
        PsiClassSupplier(event).psiClass()?.let { psiClass ->
            PsiJavaFileSupplier(event).psiJavaFile()?.let { psiJavaFile ->
                val modifications = Arrays.asList(
                    AddImports(psiJavaFile),
                    CleanupMethods(),
                    NoArgsConstructor(),
                    ImplementExternalizable(),
                    InsertMethods(SerializableFieldsSupplier(psiClass).fields())
                )
                WriteCommandAction.writeCommandAction(psiClass.project, psiClass.containingFile).run<Throwable> {
                    modifications.forEach { it.modify(psiClass) }
                }
            }
        }
    }
}
