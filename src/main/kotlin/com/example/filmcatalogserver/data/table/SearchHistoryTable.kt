package com.example.filmcatalogserver.data.table

import org.jetbrains.exposed.sql.Table

object SearchHistoryTable : Table("search_history") {
    val historyId = integer("history_id").autoIncrement()
    val userId = varchar("user_id", 255).references(UsersTable.userId)
    val query = varchar("query", 255)
    val createdAt = long("created_at")

    override val primaryKey = PrimaryKey(historyId)
}
