package com.sadaqahcode.muslimlauncher.data.remote.dto

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonObject

/**
 * Mirrors the wire payload:
 *
 *   {
 *     "salawats": [
 *       { "id": 1, "title_en": "Salawat Ibrahimiyyah", "title_id": "…",
 *         "arabic": "…", "text_en": "…", "text_id": "…",
 *         "latin": "…", "reference": "Muslim 406",
 *         "context_en": "…", "context_id": "…" }
 *     ]
 *   }
 *
 * Language columns are dynamic (`title_en`, `title_ar`, …). A fixed
 * `@Serializable` class cannot express "any key starting with title_",
 * so the companion serializer walks the object and flattens those keys
 * into a Map<String, String> per field — exactly like DhikrResponseDto.
 */
@Serializable(with = SalawatResponseDtoSerializer::class)
data class SalawatResponseDto(
    val salawats: List<SalawatDto> = emptyList(),
)

data class SalawatDto(
    val id: Int,
    val titles: Map<String, String>,
    val arabic: String,
    val texts: Map<String, String>,
    val latin: String?,
    val reference: String?,
    val contexts: Map<String, String>,
)

object SalawatResponseDtoSerializer : KSerializer<SalawatResponseDto> {

    override val descriptor: SerialDescriptor =
        buildClassSerialDescriptor("SalawatResponseDto")

    override fun deserialize(decoder: Decoder): SalawatResponseDto {
        require(decoder is JsonDecoder) { "SalawatResponseDto requires a JSON decoder" }
        val root = decoder.decodeJsonElement().jsonObject

        val salawats = (root["salawats"] as? JsonArray)
            .orEmpty()
            .mapNotNull { parseSalawat(it) }

        return SalawatResponseDto(salawats = salawats)
    }

    override fun serialize(encoder: Encoder, value: SalawatResponseDto) {
        error("SalawatResponseDto is read-only")
    }

    private fun parseSalawat(element: JsonElement): SalawatDto? {
        val obj = element as? JsonObject ?: return null
        val id = (obj["id"] as? JsonPrimitive)?.intOrNull ?: return null
        return SalawatDto(
            id = id,
            titles = obj.localized("title"),
            arabic = (obj["arabic"] as? JsonPrimitive)?.contentOrNull.orEmpty(),
            texts = obj.localized("text"),
            latin = (obj["latin"] as? JsonPrimitive)?.contentOrNull,
            reference = (obj["reference"] as? JsonPrimitive)?.contentOrNull,
            contexts = obj.localized("context"),
        )
    }

    private fun JsonObject.localized(prefix: String): Map<String, String> {
        val tag = "${prefix}_"
        return entries
            .mapNotNull { (key, value) ->
                if (!key.startsWith(tag)) return@mapNotNull null
                val text = (value as? JsonPrimitive)?.contentOrNull ?: return@mapNotNull null
                key.removePrefix(tag) to text
            }
            .toMap()
    }
}