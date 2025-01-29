package com.example.finance.ui.common

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.finance.ui.theme.FinanceTheme

@Composable
fun SaveButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        shape = MaterialTheme.shapes.medium,
        modifier = modifier.height(50.dp)
    ) {
        Text(
            text = "Сохранить",
            style = MaterialTheme.typography.displayMedium
        )
    }
}

@Preview
@Composable
private fun SaveButtonPreview() {
    FinanceTheme {
        SaveButton(
            onClick = {},
            modifier = Modifier.fillMaxWidth()
        )
    }
}