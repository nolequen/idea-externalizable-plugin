package su.nlq.idea.externalizable.generator

import com.intellij.psi.CommonClassNames
import com.intellij.psi.PsiField
import com.intellij.psi.PsiPrimitiveType
import com.intellij.psi.PsiType
import com.intellij.psi.util.InheritanceUtil
import com.intellij.psi.util.TypeConversionUtil
import org.apache.commons.lang.StringUtils.capitalize
import java.util.*
import java.util.function.BiFunction
import java.util.function.Function

enum class MethodTextGenerator : Function<Iterable<PsiField>, String> {

    Read {
        override fun apply(fields: Iterable<PsiField>): String {
            val builder = StringBuilder()
                .append("@Override\n")
                .append("public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {\n")
            fields.forEach {
                builder.append(generate(it, Function<FieldGenerator, BiFunction<Optional<String>, PsiType, String>> {
                    FieldGenerator.read(it)
                }))
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
                builder.append(generate(it, Function<FieldGenerator, BiFunction<Optional<String>, PsiType, String>> {
                    FieldGenerator.write(it)
                }))
            }
            builder.append('}')
            return builder.toString()
        }
    };

    protected final fun generate(
        field: PsiField,
        operation: Function<FieldGenerator, BiFunction<Optional<String>, PsiType, String>>
    ): String {
        val type = field.type
        return operation.apply(FieldGenerator.of(type)).apply(Optional.ofNullable(field.name), type)
    }

    protected enum class FieldGenerator {

        Primitive {
            override fun read(name: String, type: PsiType): String {
                return "$name = in.read${capitalize(type.canonicalText)}();\n"
            }

            override fun write(name: String, type: PsiType): String {
                return "out.write${capitalize(type.canonicalText)}($name);\n"
            }
        },

        Boxed {
            override fun read(name: String, type: PsiType): String {
                return readNullable(Primitive.read(name, unbox(type)))
            }

            override fun write(name: String, type: PsiType): String {
                return writeNullable(name, Primitive.write(name, unbox(type)))
            }

            private fun unbox(type: PsiType): PsiType {
                return Optional.ofNullable<PsiType>(PsiPrimitiveType.getOptionallyUnboxedType(type)).orElse(type)
            }
        },

        Externalizable {
            override fun read(name: String, type: PsiType): String {
                return readNullable("$name = new ${type.canonicalText}();\n$name.readExternal(in);\n")
            }

            override fun write(name: String, type: PsiType): String {
                return writeNullable(name, "$name.writeExternal(out);\n")
            }
        },

        Literal {
            override fun read(name: String, type: PsiType): String {
                return readNullable("$name = in.readUTF();\n")
            }

            override fun write(name: String, type: PsiType): String {
                return writeNullable(name, "out.writeUTF($name);\n")
            }
        },

        Enum {
            override fun read(name: String, type: PsiType): String {
                return readNullable("$name = ${type.canonicalText}.values()[in.readShort()];\n")
            }

            override fun write(name: String, type: PsiType): String {
                return writeNullable(name, "out.writeShort((short) $name.ordinal());\n")
            }
        },

        OrdinaryObject {
            override fun read(name: String, type: PsiType): String {
                val typeText = type.canonicalText
                val castText = if (typeText == CommonClassNames.JAVA_LANG_OBJECT) "" else "($typeText) "
                return readNullable("$name = ${castText}in.readObject();\n")
            }

            override fun write(name: String, type: PsiType): String {
                return writeNullable(name, "out.writeObject($name);\n")
            }
        };

        protected abstract fun read(name: String, type: PsiType): String

        protected abstract fun write(name: String, type: PsiType): String

        companion object {

            fun write(generator: FieldGenerator): BiFunction<Optional<String>, PsiType, String> {
                return BiFunction { name, type -> name.map { generator.write(it, type) }.orElse("") }
            }

            fun read(generator: FieldGenerator): BiFunction<Optional<String>, PsiType, String> {
                return BiFunction { name, type -> name.map { generator.read(it, type) }.orElse("") }
            }

            fun of(type: PsiType): FieldGenerator {
                if (TypeConversionUtil.isPrimitiveAndNotNull(type)) {
                    return Primitive
                }
                if (PsiPrimitiveType.getUnboxedType(type) != null) {
                    return Boxed
                }
                if (type.canonicalText == CommonClassNames.JAVA_LANG_STRING) {
                    return Literal
                }
                if (TypeConversionUtil.isEnumType(type)) {
                    return Enum
                }
                if (InheritanceUtil.isInheritor(type, CommonClassNames.JAVA_IO_EXTERNALIZABLE)) {
                    return Externalizable
                }
                return OrdinaryObject
            }

            private fun readNullable(readText: String): String {
                return "if (in.readBoolean()) {\n$readText}\n"
            }

            private fun writeNullable(name: String, writeText: String): String {
                return "if ($name == null) {\n" +
                        "out.writeBoolean(false);\n" +
                        "} else {\n" +
                        "out.writeBoolean(true);\n" +
                        "$writeText" +
                        "}\n"
            }
        }
    }
}
