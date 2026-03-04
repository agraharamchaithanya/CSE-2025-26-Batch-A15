package com.example.greenqrsmarttreeinformationsystem.utility

import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.Color
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import com.google.zxing.BarcodeFormat
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.content.Context
import com.example.greenqrsmarttreeinformationsystem.R
fun Fragment.showToast(message: String) {

    Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show()
}

fun generateQRCodeWithLogo(
    text: String,
    context: Context,
    size: Int = 800
): Bitmap {

    val hints = mapOf(
        com.google.zxing.EncodeHintType.MARGIN to 2, // better border
        com.google.zxing.EncodeHintType.ERROR_CORRECTION
                to com.google.zxing.qrcode.decoder.ErrorCorrectionLevel.H // important for logo
    )

    val bitMatrix: BitMatrix =
        MultiFormatWriter().encode(
            text,
            BarcodeFormat.QR_CODE,
            size,
            size,
            hints
        )

    val qrBitmap = Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565)

    for (x in 0 until size) {
        for (y in 0 until size) {
            qrBitmap.setPixel(
                x, y,
                if (bitMatrix[x, y]) Color.BLACK else Color.WHITE
            )
        }
    }

    // 🔥 Load Logo
    val logo = BitmapFactory.decodeResource(
        context.resources,
        R.drawable.srit_logo
    )

    val canvas = Canvas(qrBitmap)

    // 🔥 Smaller logo = faster scan (important)
    val logoSize = size / 6

    val scaledLogo = Bitmap.createScaledBitmap(
        logo,
        logoSize,
        logoSize,
        true
    )

    val left = (size - logoSize) / 2
    val top = (size - logoSize) / 2

    canvas.drawBitmap(scaledLogo, left.toFloat(), top.toFloat(), null)

    return qrBitmap
}

fun Fragment.savePref(): SharedPreferences {
    return requireActivity().getSharedPreferences("shareInfo", MODE_PRIVATE)
}

fun Fragment.backPush(nav: NavController) {
    requireActivity().onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            nav.navigateUp()
        }
    })
}