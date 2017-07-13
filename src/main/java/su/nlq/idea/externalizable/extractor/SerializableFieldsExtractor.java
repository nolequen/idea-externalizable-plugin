package su.nlq.idea.externalizable.extractor;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiModifier;
import com.intellij.psi.PsiModifierList;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public final class SerializableFieldsExtractor implements Supplier<Collection<PsiField>> {
  @NotNull
  private final PsiClass psiClass;

  public SerializableFieldsExtractor(@NotNull PsiClass psiClass) {
    this.psiClass = psiClass;
  }

  @NotNull
  @Override
  public Collection<PsiField> get() {
    return Arrays.stream(psiClass.getFields())
        .filter(ModifiersFilter.filter)
        .collect(Collectors.toList());
  }

  private static final class ModifiersFilter implements Predicate<PsiField> {
    @NotNull
    public static final ModifiersFilter filter = new ModifiersFilter();

    @NotNull
    private final Collection<String> forbidden = Arrays.asList(PsiModifier.TRANSIENT, PsiModifier.STATIC);

    @Override
    public boolean test(@NotNull PsiField psiField) {
      final PsiModifierList modifiers = psiField.getModifierList();
      return modifiers != null && forbidden.stream().noneMatch(modifiers::hasModifierProperty);
    }
  }
}
