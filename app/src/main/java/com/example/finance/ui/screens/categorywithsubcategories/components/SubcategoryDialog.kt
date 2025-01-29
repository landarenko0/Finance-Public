package com.example.finance.ui.screens.categorywithsubcategories.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.finance.ui.common.SaveButton
import com.example.finance.ui.theme.FinanceTheme

@Composable
fun SubcategoryDialog(
    initialText: String?,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var subcategoryNameFieldValue by remember {
        mutableStateOf(
            TextFieldValue(
                text = initialText ?: "",
                selection = TextRange(initialText?.length ?: 0)
            )
        )
    }

    var subcategoryNameError by remember { mutableStateOf(false) }

    val subcategoryNameFocusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) { subcategoryNameFocusRequester.requestFocus() }

    Dialog(onDismissRequest = onDismiss) {
        Card(shape = MaterialTheme.shapes.medium) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(16.dp)
            ) {
                Text(text = if (initialText == null) "Создание подкатегории" else "Редактирование подкатегории")

                OutlinedTextField(
                    value = subcategoryNameFieldValue,
                    onValueChange = {
                        subcategoryNameError = false
                        if (it.text.length <= 50) subcategoryNameFieldValue = it
                    },
                    label = {
                        Text(
                            text = "Название",
                            style = MaterialTheme.typography.displaySmall
                        )
                    },
                    isError = subcategoryNameError,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    maxLines = 3,
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(subcategoryNameFocusRequester)
                )

                SaveButton(
                    onClick = {
                        if (subcategoryNameFieldValue.text.isEmpty() || subcategoryNameFieldValue.text.isBlank()) {
                            subcategoryNameError = true
                            subcategoryNameFocusRequester.requestFocus()
                        } else {
                            onConfirm(subcategoryNameFieldValue.text.trim())
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Preview
@Composable
private fun SubcategoryDialogPreview() {
    FinanceTheme {
        SubcategoryDialog(
            initialText = null,
            onConfirm = {},
            onDismiss = {}
        )
    }
}