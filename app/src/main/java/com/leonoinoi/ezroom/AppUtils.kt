package com.leonoinoi.ezroom

import android.content.Context
import android.graphics.Color
import android.support.v4.content.ContextCompat
import com.muddzdev.styleabletoastlibrary.StyleableToast

fun toast_message_loading(context: Context) {
    StyleableToast.Builder(context)
            .text(context.getString(R.string.message_loading))
            .textColor(Color.WHITE)
            .icon(R.drawable.ic_refresh).spinIcon()
            .backgroundColor(ContextCompat.getColor(context, R.color.grey700))
            .build().show()
}

fun toast_delete_cancel(context: Context) {
    StyleableToast.Builder(context)
            .text(context.getString(R.string.message_delete_cancel))
            .textColor(Color.WHITE)
            .icon(R.drawable.ic_cancel)
            .backgroundColor(ContextCompat.getColor(context, R.color.pink500))
            .build().show()
}