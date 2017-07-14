package su.nlq.idea.externalizable.generator;

import com.intellij.psi.CommonClassNames;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiPrimitiveType;
import com.intellij.psi.PsiType;
import com.intellij.psi.util.InheritanceUtil;
import com.intellij.psi.util.TypeConversionUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

import static org.apache.commons.lang.StringUtils.capitalize;

public enum MethodTextGenerator implements Function<Iterable<PsiField>, String> {

  Read {
    @NotNull
    @Override
    public String apply(@NotNull Iterable<PsiField> fields) {
      final StringBuilder builder = new StringBuilder()
          .append("@Override\n")
          .append("public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {\n");
      fields.forEach(field -> builder.append(generate(field, FieldGenerator::read)));
      builder.append('}');
      return builder.toString();
    }
  },

  Write {
    @NotNull
    @Override
    public String apply(@NotNull Iterable<PsiField> fields) {
      final StringBuilder builder = new StringBuilder()
          .append("@Override\n")
          .append("public void writeExternal(ObjectOutput out) throws IOException {\n");
      fields.forEach(field -> builder.append(generate(field, FieldGenerator::write)));
      builder.append('}');
      return builder.toString();
    }
  };

  @NotNull
  private static String generate(@NotNull PsiField field, @NotNull Function<FieldGenerator, BiFunction<String, PsiType, String>> operation) {
    final PsiType type = field.getType();
    return operation.apply(FieldGenerator.of(type)).apply(field.getName(), type);
  }

  private enum FieldGenerator {

    Primitive {
      @NotNull
      @Override
      protected String read(@NotNull String name, @NotNull PsiType type) {
        return name + " = in.read" + capitalize(type.getCanonicalText()) + "();\n";
      }

      @NotNull
      @Override
      protected String write(@NotNull String name, @NotNull PsiType type) {
        return "out.write" + capitalize(type.getCanonicalText()) + '(' + name + ");\n";
      }
    },

    Boxed {
      @NotNull
      @Override
      protected String read(@NotNull String name, @NotNull PsiType type) {
        return readNullable(Primitive.read(name, unbox(type)));
      }

      @NotNull
      @Override
      protected String write(@NotNull String name, @NotNull PsiType type) {
        return writeNullable(name, Primitive.write(name, unbox(type)));
      }

      @NotNull
      private PsiType unbox(@NotNull PsiType type) {
        return Optional.<PsiType>ofNullable(PsiPrimitiveType.getOptionallyUnboxedType(type)).orElse(type);
      }
    },

    Externalizable {
      @NotNull
      @Override
      protected String read(@NotNull String name, @NotNull PsiType type) {
        return readNullable(name + " = new " + type.getCanonicalText() + "();\n" + name + ".readExternal(in);\n");
      }

      @NotNull
      @Override
      protected String write(@NotNull String name, @NotNull PsiType type) {
        return writeNullable(name, name + ".writeExternal(out);\n");
      }
    },

    String {
      @NotNull
      @Override
      protected String read(@NotNull String name, @NotNull PsiType type) {
        return readNullable(name + " = in.readUTF();\n");
      }

      @NotNull
      @Override
      protected String write(@NotNull String name, @NotNull PsiType type) {
        return writeNullable(name, "out.writeUTF(" + name + ");\n");
      }
    },

    Object {
      @NotNull
      @Override
      protected String read(@NotNull String name, @NotNull PsiType type) {
        return readNullable(name + " = (" + type.getCanonicalText() + ") in.readObject();\n");
      }

      @NotNull
      @Override
      protected String write(@NotNull String name, @NotNull PsiType type) {
        return writeNullable(name, "out.writeObject(" + name + ");\n");
      }
    };

    @NotNull
    public static BiFunction<String, PsiType, String> write(@NotNull FieldGenerator generator) {
      return generator::write;
    }

    @NotNull
    public static BiFunction<String, PsiType, String> read(@NotNull FieldGenerator generator) {
      return generator::read;
    }

    @SuppressWarnings("StaticMethodNamingConvention")
    @NotNull
    public static FieldGenerator of(@NotNull PsiType type) {
      if (TypeConversionUtil.isPrimitiveAndNotNull(type)) {
        return Primitive;
      }
      if (PsiPrimitiveType.getUnboxedType(type) != null) {
        return Boxed;
      }
      if (type.getCanonicalText().equals(CommonClassNames.JAVA_LANG_STRING)) {
        return String;
      }
      if (InheritanceUtil.isInheritor(type, CommonClassNames.JAVA_IO_EXTERNALIZABLE)) {
        return Externalizable;
      }
      return Object;
    }

    @NotNull
    protected abstract String read(@NotNull String name, @NotNull PsiType type);

    @NotNull
    protected abstract String write(@NotNull String name, @NotNull PsiType type);

    @NotNull
    private static String readNullable(@NotNull String readText) {
      return "if (in.readBoolean()) {\n" + readText + "}\n";
    }

    @NotNull
    private static String writeNullable(@NotNull String name, @NotNull String writeText) {
      return "if (" + name + " == null) {\n"
          + "out.writeBoolean(false);\n"
          + "} else {\n"
          + "out.writeBoolean(true);\n"
          + writeText
          + "}\n";
    }
  }
}
