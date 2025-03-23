package uk.matvey.vtornik.movie

import com.github.jasync.sql.db.RowData
import com.github.jasync.sql.db.pool.ConnectionPool
import com.github.jasync.sql.db.postgresql.PostgreSQLConnection
import kotlinx.coroutines.future.await
import kotlinx.serialization.json.Json
import uk.matvey.slon.sql.getDateOrFail
import uk.matvey.slon.sql.getLongOrFail
import uk.matvey.slon.sql.getStringOrFail
import java.time.ZoneOffset.UTC

class MovieRepository(
    private val db: ConnectionPool<PostgreSQLConnection>,
) {

    suspend fun getById(id: Long): Movie {
        return requireNotNull(findById(id)) {
            "Not found movie by id $id"
        }
    }

    suspend fun findById(id: Long): Movie? {
        return db.sendPreparedStatement(
            "select * from movies where id = ?",
            listOf(id)
        ).await().rows.singleOrNull()?.let { toMovie(it) }
    }

    suspend fun findAllByIds(ids: List<Long>): List<Movie> {
        return db.sendPreparedStatement("select * from movies where id = any(?)", listOf(ids))
            .await().rows.map { toMovie(it) }
    }

    suspend fun add(details: Movie.Details.Tmdb): Long {
        return db.sendPreparedStatement(
            """
                |insert into movies (id, title, year, details, created_at, updated_at)
                | values (?, ?, ?, ?, now(), now())
                | returning id
                |""".trimMargin(),
            listOf(
                details.id,
                details.title,
                details.releaseDateOrNull()?.year,
                Json.encodeToString(Movie.Details(tmdb = details)),
            )
        ).await().rows.single().getLongOrFail("id")
    }

    private fun toMovie(data: RowData): Movie {
        val details = Json.decodeFromString<Movie.Details>(data.getStringOrFail("details"))
        return Movie(
            id = data.getLongOrFail("id"),
            title = data.getStringOrFail("title"),
            year = details.tmdb?.releaseDateOrNull()?.year,
            details = details,
            createdAt = data.getDateOrFail("created_at").toInstant(UTC),
            updatedAt = data.getDateOrFail("updated_at").toInstant(UTC),
        )
    }
}