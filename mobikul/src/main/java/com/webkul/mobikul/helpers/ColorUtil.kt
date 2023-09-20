package com.webkul.mobikul.helpers

import android.graphics.Color



fun lighten(color: Int, fraction: Double): Int {
    var red: Int = Color.red(color)
    var green: Int = Color.green(color)
    var blue: Int = Color.blue(color)
    red = lightenColor(red, fraction)
    green = lightenColor(green, fraction)
    blue = lightenColor(blue, fraction)
    val alpha: Int = Color.alpha(color)
    return Color.argb(alpha, red, green, blue)
}

fun darken(myPassedColor: String, fraction: Double): Int {
 val color= Color.parseColor(myPassedColor)
    var red: Int = Color.red(color)
    var green: Int = Color.green(color)
    var blue: Int = Color.blue(color)
    red = darkenColor(red, fraction)
    green = darkenColor(green, fraction)
    blue = darkenColor(blue, fraction)
    val alpha: Int = Color.alpha(color)
    return Color.argb(alpha, red, green, blue)
}

private fun darkenColor(color: Int, fraction: Double): Int {
    return Math.max(color - color * fraction, 0.0).toInt()
}

private fun lightenColor(color: Int, fraction: Double): Int {
    return Math.min(color + color * fraction, 255.0).toInt()
}