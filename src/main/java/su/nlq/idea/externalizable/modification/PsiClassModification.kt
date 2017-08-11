package su.nlq.idea.externalizable.modification

import com.intellij.psi.PsiClass

import java.util.function.Consumer

interface PsiClassModification : Consumer<PsiClass>
