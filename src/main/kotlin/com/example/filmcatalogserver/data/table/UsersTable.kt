package com.example.filmcatalogserver.data.table

import org.jetbrains.exposed.sql.Table

object UsersTable : Table("users") {
    val userId = varchar("user_id", 255)
    val email = varchar("email", 255).uniqueIndex()
    val firstName = varchar("first_name", 255)
    val lastName = varchar("last_name", 255)
    val role = varchar("role", 50)

    override val primaryKey = PrimaryKey(userId)
}
