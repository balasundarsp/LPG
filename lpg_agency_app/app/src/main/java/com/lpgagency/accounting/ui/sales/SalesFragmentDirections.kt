package com.lpgagency.accounting.ui.sales

import android.os.Bundle
import androidx.navigation.NavDirections

// Safe args direction for navigating from Sales to AddSale with optional saleId
class SalesFragmentDirections {
    companion object {
        fun actionSalesToAddSale(saleId: Long = -1L): NavDirections {
            return object : NavDirections {
                override val actionId = com.lpgagency.accounting.R.id.action_sales_to_addSale
                override val arguments = Bundle().apply {
                    putLong("saleId", saleId)
                }
            }
        }
    }
}
