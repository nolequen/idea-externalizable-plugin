package su.nlq.idea.externalizable.modification

import com.intellij.psi.CommonClassNames
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiJavaFile
import com.intellij.psi.codeStyle.CodeStyleSettingsManager
import com.intellij.psi.impl.source.codeStyle.ImportHelper
import java.io.IOException
import java.io.ObjectInput
import java.io.ObjectOutput
import java.util.*

class AddImports(file: PsiFile) : PsiClassModification {
  private val file: PsiJavaFile

  init {
    this.file = file as PsiJavaFile
  }

  override fun accept(psiClass: PsiClass) {
    val project = psiClass.project
    val settings = CodeStyleSettingsManager.getSettings(project)
    val importHelper = ImportHelper(settings)

    Arrays.asList(
        CommonClassNames.JAVA_IO_EXTERNALIZABLE,
        ObjectInput::class.java.name,
        ObjectOutput::class.java.name,
        IOException::class.java.name,
        ClassNotFoundException::class.java.name
    ).stream()
        .map { TypeFinder(project, it) }
        .map { it.get() }
        .map { Optional.ofNullable(it.resolve()) }
        .forEach { it.ifPresent { reference -> importHelper.addImport(file, reference) } }
  }
}
