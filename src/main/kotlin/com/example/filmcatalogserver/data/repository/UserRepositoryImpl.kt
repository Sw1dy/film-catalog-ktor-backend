package com.example.filmcatalogserver.data.repository

import com.example.filmcatalogserver.data.dto.RegisterRequest
import com.example.filmcatalogserver.data.table.UsersTable
import com.example.filmcatalogserver.domain.model.AuthUser
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

class UserRepositoryImpl : UserRepository {
    override suspend fun findByEmail(email: String): UserCredentials? = dbQuery {
        UsersTable
            .selectAll()
            .where { UsersTable.email eq email.trim().lowercase() }
            .singleOrNull()
            ?.toUserCredentials()
    }

    override suspend fun create(
        request: RegisterRequest,
        passwordHash: String,
        role: String
    ): AuthUser? = dbQuery {
        val email = request.email.trim().lowercase()
        val userExists = UsersTable
            .selectAll()
            .where { UsersTable.email eq email }
            .any()

        if (userExists) {
            null
        } else {
            val id = UsersTable.insert {
                it[firstName] = request.firstName.trim()
                it[lastName] = request.lastName.trim()
                it[UsersTable.email] = email
                it[UsersTable.passwordHash] = passwordHash
                it[UsersTable.role] = role
            }[UsersTable.id]

            AuthUser(
                id = id,
                firstName = request.firstName.trim(),
                lastName = request.lastName.trim(),
                email = email,
                role = role
            )
        }
    }

    private fun ResultRow.toUserCredentials(): UserCredentials =
        UserCredentials(
            user = AuthUser(
                id = this[UsersTable.id],
                firstName = this[UsersTable.firstName],
                lastName = this[UsersTable.lastName],
                email = this[UsersTable.email],
                role = this[UsersTable.role]
            ),
            passwordHash = this[UsersTable.passwordHash]
        )

    private suspend fun <T> dbQuery(block: () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }
}
