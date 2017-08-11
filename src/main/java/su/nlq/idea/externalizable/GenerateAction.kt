package su.nlq.idea.externalizable

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.command.WriteCommandAction
import su.nlq.idea.externalizable.extractor.PsiClassExtractor
import su.nlq.idea.externalizable.extractor.PsiJavaFileExtractor
import su.nlq.idea.externalizable.extractor.SerializableFieldsExtractor
import su.nlq.idea.externalizable.modification.*
import java.util.*

class GenerateAction : AnAction() {

  override fun actionPerformed(event: AnActionEvent) {
    PsiClassExtractor(event).get().ifPresent { psiClass ->
      PsiJavaFileExtractor(event).get().ifPresent { psiJavaFile ->
        val modifications = Arrays.asList(
            AddImports(psiJavaFile),
            CleanupMethods(),
            NoArgsConstructor(),
            ImplementExternalizable(),
            InsertMethods(SerializableFieldsExtractor(psiClass).get())
        )
        object : WriteCommandAction.Simple<Any>(psiClass.project, psiClass.containingFile) {
          protected override fun run() {
            modifications.forEach { it.accept(psiClass) }
          }
        }.execute()
      }
    }
  }
}
