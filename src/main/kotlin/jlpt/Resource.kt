package jlpt

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import util.loadResource

sealed class Resource {

    abstract val url: String

    abstract fun load(): Document

    data class Web(override val url: String) : Resource() {

        override fun load(): Document = Jsoup.connect(url).get()

    }

    data class File(override val url: String) : Resource() {

        override fun load(): Document {
            val html = this::class.loadResource(url)
            return Jsoup.parse(html)
        }

    }

}