package com.hvuitsme.banzashoes.payment.zalopay.util

import java.text.SimpleDateFormat
import java.util.*

object Helpers {
    private var transIdCounter = 1

    fun getAppTransId(): String {
        if (transIdCounter >= 100000) transIdCounter = 1
        transIdCounter++
        val fmt = SimpleDateFormat("yyMMdd_HHmmss", Locale.getDefault())
        val timestamp = fmt.format(Date())
        return "%s%06d".format(timestamp, transIdCounter)
    }
}
