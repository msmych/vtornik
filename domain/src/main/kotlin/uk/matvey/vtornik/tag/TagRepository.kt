package uk.matvey.vtornik.tag

import com.github.jasync.sql.db.RowData
import com.github.jasync.sql.db.SuspendingConnection
import uk.matvey.slon.sql.execute
import uk.matvey.slon.sql.getDateOrFail
import uk.matvey.slon.sql.getIntOrFail
import uk.matvey.slon.sql.getLongOrFail
import uk.matvey.slon.sql.getStringOrFail
import java.time.ZoneOffset.UTC

class TagRepository(
    private val db: SuspendingConnection,
) {

    suspend fun add(userId: Int, movieId: Long, tag: String) {
        db.execute(
            """
                |insert into tags (user_id, movie_id, tag, created_at)
                | values (?, ?, ?, now())
                |""".trimMargin(),
            listOf(userId, movieId, tag)
        )
    }

    suspend fun delete(userId: Int, movieId: Long, tag: String) {
        db.execute(
            "delete from tags where user_id = ? and movie_id = ? and tag = ?",
            listOf(userId, movieId, tag)
        )
    }

    suspend fun findAllByUserIdAndMovieId(userId: Int, movieId: Long): List<Tag> {
        return db.execute(
            "select * from tags where user_id = ? and movie_id = ?",
            listOf(userId, movieId)
        ).rows.map { toTag(it) }
    }

    suspend fun findAllByUserAndTag(userId: Int, tag: String): List<Tag> {
        return db.execute(
            "select * from tags where user_id = ? and tag = ?",
            listOf(userId, tag)
        )
            .rows.map { toTag(it) }
    }

    fun toTag(data: RowData): Tag {
        return Tag(
            userId = data.getIntOrFail("user_id"),
            movieId = data.getLongOrFail("movie_id"),
            tag = data.getStringOrFail("tag"),
            createdAt = data.getDateOrFail("created_at").toInstant(UTC),
        )
    }
}