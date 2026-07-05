package br.edu.utfpr.financeflow.utils

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import java.text.NumberFormat
import java.util.Locale

class CurrencyVisualTransformation(
    private val locale: Locale = Locale.getDefault()
) : VisualTransformation {

    private val currencyFormatter = NumberFormat.getCurrencyInstance(locale)

    override fun filter(text: AnnotatedString): TransformedText {
        val originalText = text.text

        // Se estiver vazio, formata como zero
        val cents = originalText.toLongOrNull() ?: 0L
        val formattedText = currencyFormatter.format(cents / 100.0)

        // Mapeamento de cursor simples: mantém o cursor sempre no final do texto
        // para evitar comportamentos estranhos ao tentar editar o meio da moeda
        val currencyOffsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int = formattedText.length
            override fun transformedToOriginal(offset: Int): Int = originalText.length
        }

        return TransformedText(AnnotatedString(formattedText), currencyOffsetMapping)
    }
}