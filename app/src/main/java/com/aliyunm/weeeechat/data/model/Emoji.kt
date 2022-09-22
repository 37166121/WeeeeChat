package com.aliyunm.weeeechat.data.model

object Emoji {
    /**
     * 笑脸
     */
    private val GRINNING = arrayListOf(
        "\uD83D\uDE00",
        "\uD83D\uDE03",
        "\uD83D\uDE04",
        "\uD83D\uDE01",
        "\uD83D\uDE06",
        "\uD83D\uDE05",
        "\uD83E\uDD23",
        "\uD83D\uDE02",
        "\uD83D\uDE42",
        "\uD83D\uDE43",
        "\uD83D\uDE09",
        "\uD83D\uDE0A",
        "\uD83D\uDE07"
    )

    /**
     * 爱慕
     */
    private val SMILING = arrayListOf(
        "\uD83E\uDD70",
        "\uD83D\uDE0D",
        "\uD83E\uDD29",
        "\uD83D\uDE18",
        "\uD83D\uDE17",
        "☺️",
        "\uD83D\uDE1A",
        "\uD83D\uDE19",
        "\uD83E\uDD72"
    )

    /**
     * 吐舌 手 脸
     */
    private val TONGUE = arrayListOf(
        "\uD83D\uDE0B",
        "\uD83D\uDE1B",
        "\uD83D\uDE1C",
        "\uD83E\uDD2A",
        "\uD83D\uDE1D",
        "\uD83E\uDD11",
        "\uD83E\uDD17",
        "\uD83E\uDD2D",
        "\uD83E\uDD2B",
        "\uD83E\uDD14"
    )

    val emoji = arrayListOf<String>()

    init {
        emoji.addAll(GRINNING)
        emoji.addAll(SMILING)
        emoji.addAll(TONGUE)
    }
}