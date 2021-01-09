package client.stats

data class TemporaryStat(var type: TemporaryStatType, var option: Int, var templateId: Int, var expire: Long)