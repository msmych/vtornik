package uk.matvey.vtornik.movie

import com.github.jasync.sql.db.RowData
import com.github.jasync.sql.db.SuspendingConnection
import kotlinx.serialization.json.Json
import uk.matvey.slon.sql.execute
import uk.matvey.slon.sql.getDateOrFail
import uk.matvey.slon.sql.getIntOrFail
import uk.matvey.slon.sql.getLongOrFail
import uk.matvey.slon.sql.getStringOrFail
import uk.matvey.vtornik.VtornikSql.MOVIES
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset.UTC

class MovieRepository(
    private val db: SuspendingConnection,
) {

    suspend fun getById(id: Long): Movie {
        return requireNotNull(findById(id)) {
            "Not found movie by id $id"
        }
    }

    suspend fun findById(id: Long): Movie? {
        return db.execute(
            "select * from $MOVIES where id = ?",
            listOf(id)
        ).rows.singleOrNull()?.let { toMovie(it) }
    }

    suspend fun findAllByIds(ids: List<Long>): List<Movie> {
        return db.execute("select * from $MOVIES where id = any(?)", listOf(ids))
            .rows.map { toMovie(it) }
    }

    suspend fun add(
        id: Long,
        title: String,
        runtime: Int,
        overview: String,
        originalTitle: String?,
        releaseDate: LocalDate?,
        tmdb: Movie.Tmdb?,
    ): Long {
        return db.execute(
            """
                |insert into $MOVIES (id, title, runtime, overview, original_title, release_date, tmdb, created_at, updated_at)
                | values (?, ?, ?, ?, ?, ?, ?, now(), now())
                | returning id
                |""".trimMargin(),
            listOf(
                id,
                title,
                runtime,
                overview,
                originalTitle,
                releaseDate,
                tmdb?.let { Json.encodeToString(it) },
            )
        ).rows.single().getLongOrFail("id")
    }

    suspend fun update(movie: Movie): Int {
        return db.execute(
            """
            |update $MOVIES
            | set mentions = ?
            | where id = ? and updated_at = ?
            |""".trimMargin(),
            listOf(
                movie.id,
                LocalDateTime.ofInstant(movie.updatedAt, UTC)
            )
        )
            .rows.size
    }

    private fun toMovie(data: RowData): Movie {
        return Movie(
            id = data.getLongOrFail("id"),
            title = data.getStringOrFail("title"),
            runtime = data.getIntOrFail("runtime"),
            overview = data.getStringOrFail("overview"),
            releaseDate = data.getAs<LocalDate>("release_date"),
            originalTitle = data.getString("original_title"),
            tmdb = data.getString("tmdb")?.let { Json.decodeFromString<Movie.Tmdb>(it) },
            createdAt = data.getDateOrFail("created_at").toInstant(UTC),
            updatedAt = data.getDateOrFail("updated_at").toInstant(UTC),
        )
    }
}
