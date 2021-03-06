package xyz.jpenilla.announcerplus.textanimation.animation

import org.bukkit.entity.Player
import xyz.jpenilla.announcerplus.AnnouncerPlus
import xyz.jpenilla.announcerplus.textanimation.TextAnimation

class ScrollingText(private val announcerPlus: AnnouncerPlus, private val player: Player?, text: String, private val windowSize: Int, private val ticks: Int) : TextAnimation {
    private val spaces = getSpaces(windowSize)
    private val text = "$spaces$text$spaces"
    private var index = 0
    private var ticksLived = 0

    private fun getSpaces(amount: Int): String {
        val sb = StringBuilder()
        for (i in 1..amount) {
            sb.append(" ")
        }
        return sb.toString()
    }

    override fun getValue(): String {
        return try {
            announcerPlus.configManager.parse(player, text).substring(index, index + windowSize)
        } catch (e: Exception) {
            //if the placeholders changed in a way that causes us to out of bounds
            index = 0
            announcerPlus.configManager.parse(player, text).substring(index, index + windowSize)
        }
    }

    override fun nextValue(): String {
        ticksLived++
        if (ticksLived % ticks == 0) {
            index++
            if (index > announcerPlus.configManager.parse(player, text).length - windowSize) {
                index = 0
            }
        }
        return getValue()
    }
}