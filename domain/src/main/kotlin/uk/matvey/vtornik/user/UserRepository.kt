package uk.matvey.vtornik.user

import com.github.jasync.sql.db.RowData
import com.github.jasync.sql.db.SuspendingConnection
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonObject
import uk.matvey.slon.sql.execute
import uk.matvey.slon.sql.getDateOrFail
import uk.matvey.slon.sql.getIntOrFail
import uk.matvey.slon.sql.getStringOrFail
import java.time.ZoneOffset.UTC

class UserRepository(
    private val db: SuspendingConnection
) {

    suspend fun getById(id: Int): User {
        return db.execute(
            "select * from users where id = ?",
            listOf(id)
        ).rows.single().let { toUser(it) }
    }

    suspend fun getByGithubId(githubId: Long): User {
        return db.execute(
            "select * from users where details->'github'->>'id' = ?",
            listOf(githubId.toString())
        ).rows.single().let { toUser(it) }
    }

    suspend fun createUserIfNotExists(githubId: Long, githubLogin: String, githubName: String): Int {
        return db.execute(
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
        ).rows.singleOrNull()?.getInt("id") ?: getByGithubId(githubId).id
    }

    private fun toUser(data: RowData): User = User(
        id = data.getIntOrFail("id"),
        username = data.getStringOrFail("username"),
        details = Json.decodeFromString(data.getStringOrFail("details")),
        createdAt = data.getDateOrFail("created_at").toInstant(UTC),
        updatedAt = data.getDateOrFail("updated_at").toInstant(UTC),
    )
}