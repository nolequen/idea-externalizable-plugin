package su.nlq.idea.externalizable.extractor

import com.intellij.psi.PsiClass
import com.intellij.psi.PsiField
import com.intellij.psi.PsiModifier
import com.intellij.util.containers.stream
import java.util.*
import java.util.function.Predicate
import kotlin.streams.toList

class SerializableFieldsSupplier(private val psiClass: PsiClass) {

    fun fields() = psiClass.fields.stream().filter(ModifiersFilter()).toList()

    private class ModifiersFilter : Predicate<PsiField> {

        private val forbidden = Arrays.asList(PsiModifier.TRANSIENT, PsiModifier.STATIC)

        override fun test(psiField: PsiField): Boolean {
            val modifiers = psiField.modifierList
            return modifiers != null && forbidden.stream().noneMatch { modifiers.hasModifierProperty(it) }
        }
    }
}
