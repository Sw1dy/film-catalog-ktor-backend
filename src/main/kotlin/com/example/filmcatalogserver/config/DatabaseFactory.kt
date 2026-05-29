package com.example.filmcatalogserver.config

import com.example.filmcatalogserver.data.table.MoviesTable
import com.example.filmcatalogserver.data.table.SearchHistoryTable
import com.example.filmcatalogserver.data.table.UsersTable
import com.example.filmcatalogserver.data.table.WatchlistTable
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.config.ApplicationConfig
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {
    fun init(config: ApplicationConfig) {
        Database.connect(createDataSource(config))

        transaction {
            SchemaUtils.create(UsersTable, MoviesTable, SearchHistoryTable, WatchlistTable)
            seedMovies()
        }
    }

    private fun createDataSource(config: ApplicationConfig): HikariDataSource {
        val hikariConfig = HikariConfig().apply {
            jdbcUrl = config.property("database.jdbcUrl").getString()
            username = config.property("database.user").getString()
            password = config.property("database.password").getString()
            driverClassName = "org.postgresql.Driver"
            maximumPoolSize = 10
            isAutoCommit = false
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
            validate()
        }
        return HikariDataSource(hikariConfig)
    }

    private fun seedMovies() {
        if (MoviesTable.selectAll().empty()) {
            MoviesTable.insert {
                it[title] = "Грязные деньги"
                it[description] = "Криминальная драма о деньгах, риске и последствиях сложного выбора."
                it[genre] = "Драма"
                it[year] = 2018
                it[rating] = 7.4
                it[imageUrl] = "https://example.com/images/dirty-money.jpg"
            }
            MoviesTable.insert {
                it[title] = "Детство Шелдона"
                it[description] = "Комедийный сериал о юном гении Шелдоне Купере и его семье."
                it[genre] = "Комедия"
                it[year] = 2017
                it[rating] = 7.7
                it[imageUrl] = "https://example.com/images/young-sheldon.jpg"
            }
            MoviesTable.insert {
                it[title] = "Джентльмены"
                it[description] = "Стильная криминальная история о борьбе за прибыльный бизнес."
                it[genre] = "Криминал"
                it[year] = 2019
                it[rating] = 8.5
                it[imageUrl] = "https://example.com/images/the-gentlemen.jpg"
            }
            MoviesTable.insert {
                it[title] = "Хвост Феи"
                it[description] = "Аниме о гильдии волшебников, дружбе и приключениях."
                it[genre] = "Аниме"
                it[year] = 2009
                it[rating] = 7.9
                it[imageUrl] = "https://example.com/images/fairy-tail.jpg"
            }
        }
    }
}
