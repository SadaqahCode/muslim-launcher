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
import kotlinx.serialization.json.longOrNull

/**
 * Mirrors the wire payload:
 *
 *   {
 *     "dhikrs": [
 *       { "id": 1, "title_en": "Istighfar", "title_id": "Istighfar",
 *         "arabic": "…", "text_en": "…", "text_id": "…",
 *         "latin": "…", "reference": "…" }
 *     ],
 *     "dhikr_items": [
 *       { "id": 1, "name_en": "Daily Istighfar 100x", "name_id": "…",
 *         "dhikr_id": "1", "repetitions": "100",
 *         "text_en": "…", "text_id": "…", "position": 0 }
 *     ]
 *   }
 *
 * Language columns are dynamic (`title_en`, `title_ar`, …). A fixed
 * `@Serializable` class cannot express "any key starting with title_",
 * so the companion serializer walks the object and flattens those keys
 * into a Map<String, String> per field.
 */
@Serializable(with = DhikrResponseDtoSerializer::class)
data class DhikrResponseDto(
    val dhikrs: List<DhikrDto> = emptyList(),
    val dhikrItems: List<DhikrItemDto> = emptyList(),
)

data class DhikrDto(
    val id: Int,
    val titles: Map<String, String>,
    val arabic: String,
    val texts: Map<String, String>,
    val latin: String?,
    val reference: String?,
)

data class DhikrItemDto(
    val id: Long,
    val names: Map<String, String>,
    val texts: Map<String, String>,
    val dhikrIds: List<Int>,
    val repetitions: List<Int>,
    val position: Int,
)

object DhikrResponseDtoSerializer : KSerializer<DhikrResponseDto> {

    override val descriptor: SerialDescriptor =
        buildClassSerialDescriptor("DhikrResponseDto")

    override fun deserialize(decoder: Decoder): DhikrResponseDto {
        require(decoder is JsonDecoder) { "DhikrResponseDto requires a JSON decoder" }
        val root = decoder.decodeJsonElement().jsonObject

        val dhikrs = (root["dhikrs"] as? JsonArray)
            .orEmpty()
            .mapNotNull { parseDhikr(it) }

        val items = (root["dhikr_items"] as? JsonArray)
            .orEmpty()
            .mapNotNull { parseItem(it) }

        return DhikrResponseDto(dhikrs = dhikrs, dhikrItems = items)
    }

    override fun serialize(encoder: Encoder, value: DhikrResponseDto) {
        error("DhikrResponseDto is read-only")
    }

    private fun parseDhikr(element: JsonElement): DhikrDto? {
        val obj = element as? JsonObject ?: return null
        val id = (obj["id"] as? JsonPrimitive)?.intOrNull ?: return null
        return DhikrDto(
            id = id,
            titles = obj.localized("title"),
            arabic = (obj["arabic"] as? JsonPrimitive)?.contentOrNull.orEmpty(),
            texts = obj.localized("text"),
            latin = (obj["latin"] as? JsonPrimitive)?.contentOrNull,
            reference = (obj["reference"] as? JsonPrimitive)?.contentOrNull,
        )
    }

    private fun parseItem(element: JsonElement): DhikrItemDto? {
        val obj = element as? JsonObject ?: return null
        val id = (obj["id"] as? JsonPrimitive)?.longOrNull ?: return null

        // Server sends "1,6" / "3,1" as CSV strings in the current schema,
        // but may send single ints or arrays. Accept all three.
        val dhikrIds = (obj["dhikr_id"] ?: obj["dhikr_ids"]).asIntList()
        val repetitions = obj["repetitions"].asIntList()

        return DhikrItemDto(
            id = id,
            names = obj.localized("name"),
            texts = obj.localized("text"),
            dhikrIds = dhikrIds,
            repetitions = repetitions,
            position = (obj["position"] as? JsonPrimitive)?.intOrNull ?: 0,
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

    private fun JsonElement?.asIntList(): List<Int> {
        if (this == null) return emptyList()
        return when (this) {
            is JsonArray -> mapNotNull { (it as? JsonPrimitive)?.intOrNull }
            is JsonPrimitive -> {
                val raw = contentOrNull.orEmpty()
                if (raw.isBlank()) emptyList()
                else raw.split(',').mapNotNull { it.trim().toIntOrNull() }
            }
            else -> emptyList()
        }
    }
}