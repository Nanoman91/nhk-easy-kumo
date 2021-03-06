package me.avo.kumo.util

import javazoom.jl.decoder.*
import org.jsoup.nodes.*
import java.io.*
import java.net.*
import java.text.*
import java.util.*

val sdf = SimpleDateFormat("yyyy-MM-dd")

val currentDate: String get() = sdf.format(Date())

fun Element.getContent() = getElementsByTag("p").html().getText()

fun String.getText() = this
    .replace(Regex("(?s)(<rt>.*?)(?:(?:\r*\n){2}|</rt>)"), "")
    .replace(Regex("(?s)(<.*?)(?:(?:\r*\n){2}|>)"), "")
    .replace(Regex("( )+"), "")
    .trimEnd('\n')

fun Element.getTitle() = html().getText()

const val gatsu = "月"
const val nichi = "日"

fun Element.getDate() = makeDate(this.getElementsByClass("newsDate").first().text())

fun makeDate(text: String): String {
    val stripped = text.removeSurrounding("[", "]").substringBefore(nichi)

    val year = currentDate.substring(0, 4)
    val month = stripped.substringBefore(gatsu).addZero()
    val day = stripped.substringAfter(gatsu).addZero()

    return "$year-$month-$day"
}

fun String.addZero() = if (length == 1) "0$this" else this

fun getIdFromUrl(url: String) = url.substringAfterLast("/").substringBefore(".html")

fun File.notExists() = !this.exists()

fun File.writeIfNotExists(bytes: ByteArray) {
    if (this.notExists() && bytes.isNotEmpty()) this.writeBytes(bytes)
}

fun File.writeIfNotExists(text: String) = writeIfNotExists(text.toByteArray())

fun URL.read() = try {
    BufferedInputStream(this.openStream()).use { it.readBytes() }
} catch (ex: Exception) {
    ByteArray(0)
}

fun File.getDuration(): Long = inputStream().use {
    val stream = Bitstream(it)
    val h = stream.readFrame()
    val tn = it.channel.size()
    val ms = h.ms_per_frame()
    val bitrate = h.bitrate()
    val frame = h.calculate_framesize()
    //println("Frame: $frame, ms: $ms, bitrate: $bitrate, channel: $tn, ${tn / 10000}" )
    tn / 10000
}