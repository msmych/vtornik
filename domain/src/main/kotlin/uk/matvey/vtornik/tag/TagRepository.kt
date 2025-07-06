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
import uk.matvey.vtornik.VtornikSql.CREATED_AT
import uk.matvey.vtornik.VtornikSql.TAGS
import uk.matvey.vtornik.VtornikSql.UPDATED_AT
import java.time.ZoneOffset.UTC

class TagRepository(
    private val db: SuspendingConnection,
) {

    suspend fun set(userId: Int, movieId: Long, type: Tag.Type, value: JsonElement) {
        db.execute(
            """
                |insert into $TAGS ($USER_ID, $MOVIE_ID, $TYPE, ${VALUE}, $CREATED_AT, $UPDATED_AT)
                | values (?, ?, ?, ?, now(), now())
                | on conflict ($USER_ID, $MOVIE_ID, $TYPE) do update
                | set $VALUE = excluded.${VALUE}, $UPDATED_AT = now()
                |""".trimMargin(),
            listOf(userId, movieId, type, Json.encodeToString(value))
        )
    }

    suspend fun findByUserIdMovieIdAndType(userId: Int, movieId: Long, type: Tag.Type): Tag? {
        return db.execute(
            "select * from $TAGS where $USER_ID = ? and $MOVIE_ID = ? and $TYPE = ?",
            listOf(userId, movieId, type)
        ).rows.singleOrNull()?.let { toTag(it) }
    }

    suspend fun findAllByUserIdAndMovieId(userId: Int, movieId: Long): List<Tag> {
        return db.execute(
            "select * from $TAGS where $USER_ID = ? and $MOVIE_ID = ?",
            listOf(userId, movieId)
        ).rows.map { toTag(it) }
    }

    suspend fun findAllByUserIdTypeAndValue(userId: Int, type: Tag.Type, value: JsonElement): List<Tag> {
        return db.execute(
            "select * from $TAGS where $USER_ID = ? and $TYPE = ? and $VALUE = ?",
            listOf(userId, type, Json.encodeToString(value))
        )
            .rows.map { toTag(it) }
    }

    suspend fun findAllNotesByUserId(userId: Int): List<Tag> {
        return db.execute(
            "select * from $TAGS where $USER_ID = ? and $TYPE = 'NOTE' and $VALUE <> '\"\"'",
            listOf(userId)
        )
            .rows.map { toTag(it) }
    }

    fun toTag(data: RowData): Tag {
        return Tag(
            userId = data.getIntOrFail(USER_ID),
            movieId = data.getLongOrFail(MOVIE_ID),
            type = Tag.Type.valueOf(data.getStringOrFail(TYPE)),
            value = Json.parseToJsonElement(data.getStringOrFail(VALUE)),
            createdAt = data.getDateOrFail(CREATED_AT).toInstant(UTC),
            updatedAt = data.getDateOrFail(UPDATED_AT).toInstant(UTC),
        )
    }

    companion object {
        const val USER_ID = "user_id"
        const val MOVIE_ID = "movie_id"
        const val TYPE = "type"
        const val VALUE = "value"
    }
}
