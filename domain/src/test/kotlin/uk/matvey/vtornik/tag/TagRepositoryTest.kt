package uk.matvey.vtornik.tag

import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import uk.matvey.vtornik.DomainTestSetup

class TagRepositoryTest : DomainTestSetup() {

    @Test
    fun `should add and delete tags`() = runTest {
        // given
        val tagRepository = TagRepository(db)

        // when
        tagRepository.add(1234, 9876,"tag1")
        tagRepository.add(1234, 9876,"tag2")

        // then
        val tags = tagRepository.findAllByUserIdAndMovieId(1234, 9876)
        assertThat(tags.map { it.tag }).contains("tag1", "tag2")

        // when
        tagRepository.delete(1234, 9876, "tag1")

        // then
        val tagsAfterDelete = tagRepository.findAllByUserIdAndMovieId(1234, 9876)
        assertThat(tagsAfterDelete.map { it.tag }).doesNotContain("tag1")
    }
}