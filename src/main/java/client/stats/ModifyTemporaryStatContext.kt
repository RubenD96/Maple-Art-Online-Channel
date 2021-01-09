package client.stats

import client.Character
import java.util.*

class ModifyTemporaryStatContext(private val character: Character) {

    val resetOperations: MutableMap<TemporaryStatType, TemporaryStat> = EnumMap(TemporaryStatType::class.java)
    val setOperations: MutableMap<TemporaryStatType, TemporaryStat> = EnumMap(TemporaryStatType::class.java)

    fun set(type: TemporaryStatType, option: Int, templateId: Int, expire: Long = 0) {
        val stat = TemporaryStat(type, option, templateId, expire)

        reset(type)
        setOperations[type] = stat
        character.temporaryStats[type] = stat
    }

    fun reset(type: TemporaryStatType) {
        character.temporaryStats[type]?.let {
            resetOperations[type] = it
        }
        character.temporaryStats.remove(type)
    }

    /**
     * For skills
     */
    fun reset(templateId: Int) {
        character.temporaryStats.values.filter {
            it.templateId == templateId
        }.toList().forEach {
            reset(it.type)
        }
    }
}