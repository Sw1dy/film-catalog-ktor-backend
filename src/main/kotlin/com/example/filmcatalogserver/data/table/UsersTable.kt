package com.example.filmcatalogserver.data.table

import org.jetbrains.exposed.sql.Table

object UsersTable : Table("users") {
    val id = integer("id").autoIncrement()
    val firstName = varchar("first_name", 255)
    val lastName = varchar("last_name", 255)
    val email = varchar("email", 255).uniqueIndex()
    val passwordHash = varchar("password_hash", 255)
    val role = varchar("role", 50)

    override val primaryKey = PrimaryKey(id)
}
