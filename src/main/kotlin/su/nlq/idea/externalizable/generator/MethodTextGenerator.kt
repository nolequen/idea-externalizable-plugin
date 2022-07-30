package su.nlq.idea.externalizable.generator

import com.intellij.psi.CommonClassNames
import com.intellij.psi.PsiField
import com.intellij.psi.PsiPrimitiveType
import com.intellij.psi.PsiType
import com.intellij.psi.util.InheritanceUtil
import com.intellij.psi.util.TypeConversionUtil
import org.apache.commons.lang.StringUtils.capitalize
import java.util.function.Function

enum class MethodTextGenerator : Function<Iterable<PsiField>, String> {

    Read {
        override fun apply(fields: Iterable<PsiField>): String {
            val builder = StringBuilder()
                .append("@Override\n")
                .append("public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {\n")
            fields.forEach {
                builder.append(generate(it, { generator -> { name, type -> generator.read(name, type) } }))
            }
            builder.append('}')
            return builder.toString()
        }
    },

    Write {
        override fun apply(fields: Iterable<PsiField>): String {
            val builder = StringBuilder()
                .append("@Override\n")
                .append("public void writeExternal(ObjectOutput out) throws IOException {\n")
            fields.forEach {
                builder.append(generate(it, { generator -> { name, type -> generator.write(name, type) } }))
            }
            builder.append('}')
            return builder.toString()
        }
    };

    protected final fun generate(field: PsiField, operation: (FieldGenerator) -> (String, PsiType) -> String) =
        operation(FieldGenerator.of(field.type))(field.name, field.type)

    protected enum class FieldGenerator {

        Primitive {
            override fun read(name: String, type: PsiType) = "$name = in.read${capitalize(type.canonicalText)}();\n"

            override fun write(name: String, type: PsiType) = "out.write${capitalize(type.canonicalText)}($name);\n"
        },

        Boxed {
            override fun read(name: String, type: PsiType) = readNullable(Primitive.read(name, unbox(type)))

            override fun write(name: String, type: PsiType) = writeNullable(name, Primitive.write(name, unbox(type)))

            private fun unbox(type: PsiType) = PsiPrimitiveType.getOptionallyUnboxedType(type) ?: type
        },

        Externalizable {
            override fun read(name: String, type: PsiType) =
                readNullable("$name = new ${type.canonicalText}();\n$name.readExternal(in);\n")

            override fun write(name: String, type: PsiType) = writeNullable(name, "$name.writeExternal(out);\n")
        },

        Literal {
            override fun read(name: String, type: PsiType) = readNullable("$name = in.readUTF();\n")

            override fun write(name: String, type: PsiType) = writeNullable(name, "out.writeUTF($name);\n")
        },

        Enum {
            override fun read(name: String, type: PsiType) =
                readNullable("$name = ${type.canonicalText}.values()[in.readShort()];\n")

            override fun write(name: String, type: PsiType) =
                writeNullable(name, "out.writeShort((short) $name.ordinal());\n")
        },

        OrdinaryObject {
            override fun read(name: String, type: PsiType): String {
                val typeText = type.canonicalText
                val castText = if (typeText == CommonClassNames.JAVA_LANG_OBJECT) "" else "($typeText) "
                return readNullable("$name = ${castText}in.readObject();\n")
            }

            override fun write(name: String, type: PsiType) = writeNullable(name, "out.writeObject($name);\n")
        };

        abstract fun read(name: String, type: PsiType): String

        abstract fun write(name: String, type: PsiType): String

        companion object {

            fun of(type: PsiType) = when {
                TypeConversionUtil.isPrimitiveAndNotNull(type) -> Primitive
                PsiPrimitiveType.getUnboxedType(type) != null -> Boxed
                type.canonicalText == CommonClassNames.JAVA_LANG_STRING -> Literal
                TypeConversionUtil.isEnumType(type) -> Enum
                InheritanceUtil.isInheritor(type, CommonClassNames.JAVA_IO_EXTERNALIZABLE) -> Externalizable
                else -> OrdinaryObject
            }

            private fun readNullable(readText: String) = "if (in.readBoolean()) {\n$readText}\n"

            private fun writeNullable(name: String, writeText: String) =
                "if ($name == null) {\n" +
                        "out.writeBoolean(false);\n" +
                        "} else {\n" +
                        "out.writeBoolean(true);\n" +
                        "$writeText" +
                        "}\n"
        }
    }
}
