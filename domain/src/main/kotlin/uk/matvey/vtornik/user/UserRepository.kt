package uk.matvey.vtornik.user

import com.github.jasync.sql.db.RowData
import com.github.jasync.sql.db.pool.ConnectionPool
import com.github.jasync.sql.db.postgresql.PostgreSQLConnection
import kotlinx.coroutines.future.await
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonObject
import java.time.ZoneOffset.UTC

class UserRepository(
    private val db: ConnectionPool<PostgreSQLConnection>,
) {

    suspend fun getById(id: Int): User {
        val result = db.sendPreparedStatement(
            "select * from users where id = ?",
            listOf(id)
        ).await()
        return result.rows.single().let { toUser(it) }
    }

    suspend fun getByGithubId(githubId: Long): User {
        return db.sendPreparedStatement(
            "select * from users where details->'github'->>'id' = ?",
            listOf(githubId.toString())
        ).await().rows.single().let { toUser(it) }
    }

    suspend fun createUserIfNotExists(githubId: Long, githubLogin: String, githubName: String): Int {
        return db.sendPreparedStatement(
            """
                |insert into users (username, details, created_at, updated_at)
                | values (?, ?, now(), now())
                | on conflict do nothing
                | returning id
                |""".trimMargin(),
            listOf(githubLogin, buildJsonObject {
                putJsonObject("github") {
                    put("id", githubId)
                    put("login", githubLogin)
                    put("name", githubName)
                }
            })
        ).await().rows.singleOrNull()?.getInt("id") ?: getByGithubId(githubId).id
    }

    private fun toUser(data: RowData): User = User(
        id = data.getInt("id")!!,
        username = data.getString("username")!!,
        details = Json.decodeFromString(data.getString("details")!!),
        createdAt = data.getDate("created_at")!!.toInstant(UTC),
        updatedAt = data.getDate("updated_at")!!.toInstant(UTC),
    )
}