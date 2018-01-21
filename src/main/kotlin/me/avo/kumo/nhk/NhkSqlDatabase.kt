package me.avo.kumo.nhk

import me.avo.kumo.nhk.persistence.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.*
import org.joda.time.*
import java.sql.*

object Articles : Table("articles") {
    val id = varchar("id", 20).primaryKey()
    val url = varchar("url", 254)
    val title = varchar("title", 254)
    val date = datetime("date")
    val content = text("content")
    val audioUrl = varchar("audio_url", 254)
    val imported = bool("imported")
}

class NhkSqlDatabase(url: String, driver: String) : NhkDatabase {

    init {
        Database.connect(url, driver)
        TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE
        transaction {
            SchemaUtils.createMissingTablesAndColumns(Articles)
        }
    }

    override fun saveArticles(articles: List<Article>) = transaction {
        articles.forEach { art ->
            Articles.insert {
                it[id] = art.id
                it[url] = art.url
                it[title] = art.title
                it[date] = DateTime.parse(art.date)
                it[content] = art.content
                it[audioUrl] = art.audioUrl
                it[imported] = art.imported
            }
        }
    }

    override fun updateArticle(article: Article): Unit = transaction {
        Articles.update({ Articles.id eq article.id }) {
            it[imported] = true
        }
    }

    override fun filterImported(articles: List<Article>): List<Article> {
        val ids = articles.map(Article::id)
        val alreadyImported = transaction {
            Articles.slice(Articles.id)
                    .select { Articles.id inList ids and (Articles.imported eq true) }
                    .map { it[Articles.id] }
        }
        return articles.filter { it.id !in alreadyImported }
    }

}