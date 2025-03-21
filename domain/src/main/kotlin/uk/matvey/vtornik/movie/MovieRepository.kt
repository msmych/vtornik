package uk.matvey.vtornik.movie

import com.github.jasync.sql.db.RowData
import com.github.jasync.sql.db.pool.ConnectionPool
import com.github.jasync.sql.db.postgresql.PostgreSQLConnection
import kotlinx.coroutines.future.await
import kotlinx.serialization.json.Json
import java.time.ZoneOffset.UTC

class MovieRepository(
    private val db: ConnectionPool<PostgreSQLConnection>,
) {

    suspend fun findById(id: Long): Movie? {
        val result = db.sendPreparedStatement(
            "select * from movies where id = ?",
            listOf(id)
        ).await()
        return result.rows.singleOrNull()?.let { toMovie(it) }
    }

    suspend fun add(title: String, details: Movie.Details): Long {
        return db.sendPreparedStatement(
            """
                |insert into movies (id, title, year, details, created_at, updated_at)
                | values (?, ?, ?, ?, now(), now())
                | returning id
                |""".trimMargin(),
            listOf(details.tmdb!!.id, title, details.tmdb.releaseDate()?.year, Json.encodeToString(details))
        ).await().rows.single().getLong("id")!!
    }

    private fun toMovie(data: RowData): Movie {
        val details = Json.decodeFromString<Movie.Details>(data.getString("details")!!)
        return Movie(
            id = data.getLong("id")!!,
            title = data.getString("title")!!,
            year = details.tmdb?.releaseDate()?.year,
            details = details,
            createdAt = data.getDate("created_at")!!.toInstant(UTC),
            updatedAt = data.getDate("updated_at")!!.toInstant(UTC),
        )
    }
}