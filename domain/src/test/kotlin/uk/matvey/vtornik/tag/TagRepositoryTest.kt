package uk.matvey.vtornik.tag

import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.JsonPrimitive
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import uk.matvey.vtornik.DomainTestSetup

class TagRepositoryTest : DomainTestSetup() {

    @Test
    fun `should set tags`() = runTest {
        // given
        val tagRepository = TagRepository(db)

        // when
        tagRepository.set(1234, 9876, Tag.Type.WATCHED, JsonPrimitive(true))
        tagRepository.set(1234, 9876, Tag.Type.WATCHLIST, JsonPrimitive(true))

        // then
        val tags = tagRepository.findAllByUserIdAndMovieId(1234, 9876)
        assertThat(tags.map { it.type }).contains(Tag.Type.WATCHED, Tag.Type.WATCHLIST)
    }
}