package uk.matvey.tmdb

class TmdbImages(
    private val config: TmdbConfiguration.Images,
) {

    fun posterUrl(path: String, size: String): String {
        require(size in config.posterSizes) {
            "Invalid poster size: $size. Available sizes: ${config.posterSizes.joinToString()}"
        }
        return "${config.secureBaseUrl}$size/$path"
    }
}