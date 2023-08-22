package su.nlq.idea.externalizable.modification

import com.intellij.psi.*
import com.intellij.psi.util.InheritanceUtil
import com.intellij.psi.util.TypeConversionUtil
import org.apache.commons.lang.StringUtils.capitalize

class MethodTextGenerator(psiClass: PsiClass) {

    companion object {
        private val forbiddenModifiers = listOf(PsiModifier.TRANSIENT, PsiModifier.STATIC)
    }

    private val fields = psiClass.fields
            .filter {
                val modifiers = it.modifierList
                modifiers != null && forbiddenModifiers.none { modifiers.hasModifierProperty(it) }
            }.map {
                when {
                    TypeConversionUtil.isPrimitiveAndNotNull(it.type) -> Primitive(it.name, it.type)
                    PsiPrimitiveType.getUnboxedType(it.type) != null -> Nullable(it.name, Boxed(it.name, it.type))
                    it.type.canonicalText == CommonClassNames.JAVA_LANG_STRING -> Nullable(it.name, Literal(it.name))
                    TypeConversionUtil.isEnumType(it.type) -> Nullable(it.name, Enum(it.name, it.type))
                    InheritanceUtil.isInheritor(
                            it.type,
                            CommonClassNames.JAVA_IO_EXTERNALIZABLE
                    ) -> Nullable(it.name, Externalizable(it.name, it.type))

                    else -> Nullable(it.name, OrdinaryObject(it.name, it.type))
                }
            }

    fun read(): String {
        val builder = StringBuilder()
                .append("@Override\n")
                .append("public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {\n")
        fields.forEach { builder.append(it.read()) }
        builder.append('}')
        return builder.toString()
    }

    fun write(): String {
        val builder = StringBuilder()
                .append("@Override\n")
                .append("public void writeExternal(ObjectOutput out) throws IOException {\n")
        fields.forEach { builder.append(it.write()) }
        builder.append('}')
        return builder.toString()
    }
}

private sealed class Field() {

    abstract fun read(): String

    abstract fun write(): String
}

private class Nullable(
        private val name: String,
        private val field: Field
) : Field() {

    override fun read() = "if (in.readBoolean()) {\n${field.read()}}\n"

    override fun write() = "if ($name == null) {\n" +
            "out.writeBoolean(false);\n" +
            "} else {\n" +
            "out.writeBoolean(true);\n" +
            "${field.write()}" +
            "}\n"
}

private class Primitive(
        private val name: String,
        private val type: PsiType
) : Field() {

    override fun read() = "$name = in.read${capitalize(type.canonicalText)}();"

    override fun write() = "out.write${capitalize(type.canonicalText)}($name);"
}

private class Boxed(name: String, type: PsiType) : Field() {

    private val primitive = Primitive(name, PsiPrimitiveType.getOptionallyUnboxedType(type) ?: type)

    override fun read() = primitive.read()

    override fun write() = primitive.write()
}

private class Externalizable(
        private val name: String,
        private val type: PsiType
) : Field() {

    override fun read() = "$name = new ${type.canonicalText}();\n$name.readExternal(in);"

    override fun write() = "$name.writeExternal(out);"
}

private class Literal(
        private val name: String
) : Field() {

    override fun read() = "$name = in.readUTF();"

    override fun write() = "out.writeUTF($name);"
}

private class Enum(
        private val name: String,
        private val type: PsiType
) : Field() {

    override fun read() = "$name = ${type.canonicalText}.values()[in.readShort()];"

    override fun write() = "out.writeShort((short) $name.ordinal());"
}

private class OrdinaryObject(
        private val name: String,
        private val type: PsiType
) : Field() {

    override fun read(): String {
        val typeText = type.canonicalText
        val castText = if (typeText == CommonClassNames.JAVA_LANG_OBJECT) "" else "($typeText) "
        return "$name = ${castText}in.readObject();"
    }

    override fun write() = "out.writeObject($name);"
}
