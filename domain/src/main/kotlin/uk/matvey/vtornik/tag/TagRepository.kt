package uk.matvey.vtornik.tag

import com.github.jasync.sql.db.RowData
import com.github.jasync.sql.db.SuspendingConnection
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import uk.matvey.slon.sql.execute
import uk.matvey.slon.sql.getDateOrFail
import uk.matvey.slon.sql.getIntOrFail
import uk.matvey.slon.sql.getLongOrFail
import uk.matvey.slon.sql.getStringOrFail
import uk.matvey.vtornik.VtornikSql.TAGS
import java.time.ZoneOffset.UTC

class TagRepository(
    private val db: SuspendingConnection,
) {

    suspend fun set(userId: Int, movieId: Long, type: Tag.Type, payload: JsonElement) {
        db.execute(
            """
                |insert into $TAGS (user_id, movie_id, type, payload, created_at, updated_at)
                | values (?, ?, ?, ?, now(), now())
                | on conflict (user_id, movie_id, type) do update
                | set payload = excluded.payload, updated_at = now()
                |""".trimMargin(),
            listOf(userId, movieId, type, Json.encodeToString(payload))
        )
    }

    suspend fun findByUserIdMovieIdAndType(userId: Int, movieId: Long, type: Tag.Type): Tag? {
        return db.execute(
            "select * from $TAGS where user_id = ? and movie_id = ? and type = ?",
            listOf(userId, movieId, type)
        ).rows.singleOrNull()?.let { toTag(it) }
    }

    suspend fun findAllByUserIdAndMovieId(userId: Int, movieId: Long): List<Tag> {
        return db.execute(
            "select * from $TAGS where user_id = ? and movie_id = ?",
            listOf(userId, movieId)
        ).rows.map { toTag(it) }
    }

    suspend fun findAllByUserAndType(userId: Int, type: Tag.Type): List<Tag> {
        return db.execute(
            "select * from $TAGS where user_id = ? and type = ?",
            listOf(userId, type)
        )
            .rows.map { toTag(it) }
    }

    fun toTag(data: RowData): Tag {
        return Tag(
            userId = data.getIntOrFail("user_id"),
            movieId = data.getLongOrFail("movie_id"),
            type = Tag.Type.valueOf(data.getStringOrFail("type")),
            payload = Json.parseToJsonElement(data.getStringOrFail("payload")),
            createdAt = data.getDateOrFail("created_at").toInstant(UTC),
        )
    }
}
