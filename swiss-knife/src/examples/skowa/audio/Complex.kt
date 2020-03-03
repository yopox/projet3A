package examples.skowa.audio

import kotlin.math.*

class Complex(val re: Double, val im: Double) {

    infix operator fun plus(x: Complex) = Complex(re + x.re, im + x.im)
    infix operator fun minus(x: Complex) = Complex(re - x.re, im - x.im)

    infix operator fun times(x: Double) = Complex(re * x, im * x)
    infix operator fun times(x: Complex) = Complex(re * x.re - im * x.im, re * x.im + im * x.re)
    infix operator fun div(x: Double) = Complex(re / x, im / x)

    val exp: Complex by lazy { Complex(cos(im), sin(im)) * (cosh(re) + sinh(re)) }

    private val a = "%1.3f".format(re)
    private val b = "%1.3f".format(abs(im))
    override fun toString() = when {
        b == "0.000" -> a
        a == "0.000" -> b + 'i'
        im > 0 -> a + " + " + b + 'i'
        else -> a + " - " + b + 'i'
    }

}