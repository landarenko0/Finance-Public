package com.example.finance.ui.screens.category

sealed interface CategoryEvent {

    data object RequestCategoryNameFocus : CategoryEvent

    data object CloseScreen : CategoryEvent

    data object NavigateToCategoryListScreen : CategoryEvent
}