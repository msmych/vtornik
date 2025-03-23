package uk.matvey.vtornik.tag

import com.github.jasync.sql.db.RowData
import com.github.jasync.sql.db.pool.ConnectionPool
import com.github.jasync.sql.db.postgresql.PostgreSQLConnection
import kotlinx.coroutines.future.await
import java.time.ZoneOffset.UTC

class TagRepository(
    private val db: ConnectionPool<PostgreSQLConnection>,
) {

    suspend fun add(userId: Int, movieId: Long, tag: String) {
        db.sendPreparedStatement(
            """
                |insert into tags (user_id, movie_id, tag, created_at)
                | values (?, ?, ?, now())
                |""".trimMargin(),
            listOf(userId, movieId, tag)
        ).await()
    }

    suspend fun delete(userId: Int, movieId: Long, tag: String) {
        db.sendPreparedStatement(
            "delete from tags where user_id = ? and movie_id = ? and tag = ?",
            listOf(userId, movieId, tag)
        ).await()
    }

    suspend fun findAllByUserIdAndMovieId(userId: Int, movieId: Long): List<Tag> {
        return db.sendPreparedStatement(
            "select * from tags where user_id = ? and movie_id = ?",
            listOf(userId, movieId)
        ).await().rows.map { toTag(it) }
    }

    suspend fun findAllByUserAndTag(userId: Int, tag: String): List<Tag> {
        return db.sendPreparedStatement(
            "select * from tags where user_id = ? and tag = ?",
            listOf(userId, tag)
        )
            .await().rows.map { toTag(it) }
    }

    fun toTag(data: RowData): Tag {
        return Tag(
            userId = data.getInt("user_id")!!,
            movieId = data.getLong("movie_id")!!,
            tag = data.getString("tag")!!,
            createdAt = data.getDate("created_at")!!.toInstant(UTC),
        )
    }
}