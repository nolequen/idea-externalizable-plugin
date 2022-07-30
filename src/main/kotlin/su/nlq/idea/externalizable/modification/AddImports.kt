package su.nlq.idea.externalizable.modification

import com.intellij.application.options.CodeStyle
import com.intellij.psi.CommonClassNames
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiJavaFile
import com.intellij.psi.codeStyle.JavaCodeStyleSettings
import com.intellij.psi.impl.source.codeStyle.ImportHelper
import java.io.IOException
import java.io.ObjectInput
import java.io.ObjectOutput
import java.util.*

class AddImports(private val file: PsiJavaFile) : PsiClassModification {

    override fun modify(psiClass: PsiClass) {
        val project = psiClass.project
        val importHelper = ImportHelper(JavaCodeStyleSettings(CodeStyle.getSettings(project)))

        Arrays.asList(
            CommonClassNames.JAVA_IO_EXTERNALIZABLE,
            ObjectInput::class.java.name,
            ObjectOutput::class.java.name,
            IOException::class.java.name,
            ClassNotFoundException::class.java.name
        ).stream()
            .map { TypeFinder(project, it) }
            .map { it.get() }
            .map { it.resolve() }
            .forEach { it?.let { reference -> importHelper.addImport(file, reference) } }
    }
}
