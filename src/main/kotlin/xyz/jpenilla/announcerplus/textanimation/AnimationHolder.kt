package xyz.jpenilla.announcerplus.textanimation

import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import org.bukkit.entity.Player
import xyz.jpenilla.announcerplus.AnnouncerPlus
import xyz.jpenilla.announcerplus.textanimation.animation.*
import java.util.regex.Pattern

class AnimationHolder(private val announcerPlus: AnnouncerPlus, private val player: Player?, private val message: String) {
    companion object {
        private val pattern = Pattern.compile("\\{animate:/?([a-z][^}]*)/?}?")
    }

    private val matcher = pattern.matcher(message)
    private val animations = HashMap<String, TextAnimation>()

    init {
        while (matcher.find()) {
            val tokens = matcher.group(1).split(":")

            when (tokens[0]) {
                "scroll" -> {
                    val speed = try {
                        tokens[1].toFloat()
                    } catch (e: Exception) {
                        0.1f
                    }
                    animations[matcher.group()] = ScrollingGradient(speed)
                }

                "flash" -> {
                    val colors = ArrayList(tokens.subList(1, tokens.size))
                    var ticks: Int
                    try {
                        ticks = colors.last().toInt()
                        colors.removeAt(colors.lastIndex)
                    } catch (e: Exception) {
                        ticks = 10
                    }
                    animations[matcher.group()] = FlashingText(colors, ticks)
                }

                "pulse" -> {
                    val colors = ArrayList(tokens.subList(1, tokens.size))
                    var ticks: Int
                    try {
                        ticks = colors.last().toInt()
                        colors.removeAt(colors.lastIndex)
                    } catch (e: Exception) {
                        ticks = 10
                    }
                    val textColors = colors.map { color ->
                        NamedTextColor.NAMES.value(color) ?: (TextColor.fromHexString(color) ?: NamedTextColor.WHITE)
                    }
                    animations[matcher.group()] = PulsingColor(textColors, ticks)
                }

                "type" -> {
                    val text = tokens[1]
                    val ticks = try {
                        tokens[2].toInt()
                    } catch (e: Exception) {
                        6
                    }
                    animations[matcher.group()] = Typewriter(announcerPlus, player, text, ticks)
                }

                "randomcolor" -> {
                    val type = try {
                        RandomColor.Type.of(tokens[1])
                    } catch (e: Exception) {
                        RandomColor.Type.PULSE
                    }
                    val ticks = try {
                        tokens[2].toInt()
                    } catch (e: Exception) {
                        10
                    }
                    animations[matcher.group()] = RandomColor(type, ticks)
                }

                "scrolltext" -> {
                    val text = tokens[1]
                    val window = try {
                        tokens[2].toInt()
                    } catch (e: Exception) {
                        10
                    }
                    val ticks = try {
                        tokens[3].toInt()
                    } catch (e: Exception) {
                        4
                    }
                    animations[matcher.group()] = ScrollingText(announcerPlus, player, text, window, ticks)
                }
            }
        }
    }

    fun parseNext(text: String?): String {
        var msg = text ?: message
        for (animation in animations) {
            msg = msg.replace(animation.key, animation.value.nextValue())
        }
        return announcerPlus.configManager.parse(player, msg)
    }

    fun parseCurrent(text: String?): String {
        var msg = text ?: message
        for (animation in animations) {
            msg = msg.replace(animation.key, animation.value.getValue())
        }
        return announcerPlus.configManager.parse(player, msg)
    }
}