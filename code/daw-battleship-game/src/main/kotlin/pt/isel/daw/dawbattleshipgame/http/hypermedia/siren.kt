package pt.isel.daw.dawbattleshipgame.http.hypermedia

import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import pt.isel.daw.dawbattleshipgame.http.LinkRelation
import java.net.URI

/**
 * Class whose instances represent actions that are included in a siren entity.
 */
data class SirenAction(
    val name: String,
    val href: URI,
    val title: String? = null,
    val clazz: List<String>? = null,
    val method: HttpMethod? = null,
    val type: String? = null,
    val fields: List<Field>? = null
) {
    /**
     * Represents action's fields
     */
    data class Field(
        val name: String,
        val fields: List<Field>? = null,
        val type: String? = null,
        val value: String? = null,
        val title: String? = null
    )
}

data class LinkOutputModel(
    private val targetUri: URI,
    private val relation: LinkRelation
) {
    val href = targetUri.toASCIIString()
    val rel = relation.value
}