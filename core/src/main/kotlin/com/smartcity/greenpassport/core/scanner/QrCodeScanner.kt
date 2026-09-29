package com.smartcity.greenpassport.core.scanner

import android.content.Context
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class QrCodeScanner @Inject constructor() {

    suspend fun scan(activityContext: Context): String? {
        val options = GmsBarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
            .build()
        return GmsBarcodeScanning.getClient(activityContext, options).startScan().await().rawValue
    }
}
