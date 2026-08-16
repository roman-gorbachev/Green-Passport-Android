package com.smartcity.greenpassport.feature.games.presentation.sorting

import com.smartcity.greenpassport.feature.games.R

enum class WasteCategory(val binLabelRes: Int) {
    GLASS(R.string.sorting_bin_glass),
    METAL(R.string.sorting_bin_metal),
    PAPER(R.string.sorting_bin_paper),
    PLASTIC(R.string.sorting_bin_plastic),
}

data class WasteItemDef(val nameRes: Int, val category: WasteCategory)

val wasteItemPool = listOf(
    WasteItemDef(R.string.sorting_item_glass_bottle, WasteCategory.GLASS),
    WasteItemDef(R.string.sorting_item_glass_jar, WasteCategory.GLASS),
    WasteItemDef(R.string.sorting_item_soda_can, WasteCategory.METAL),
    WasteItemDef(R.string.sorting_item_foil, WasteCategory.METAL),
    WasteItemDef(R.string.sorting_item_newspaper, WasteCategory.PAPER),
    WasteItemDef(R.string.sorting_item_cardboard_box, WasteCategory.PAPER),
    WasteItemDef(R.string.sorting_item_plastic_bag, WasteCategory.PLASTIC),
    WasteItemDef(R.string.sorting_item_plastic_bottle, WasteCategory.PLASTIC),
)
