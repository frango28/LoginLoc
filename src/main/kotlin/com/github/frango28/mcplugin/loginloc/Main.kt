package com.github.frango28.mcplugin.loginloc

import com.github.syari.spigot.api.command.command
import com.github.syari.spigot.api.command.tab.CommandTabArgument.Companion.argument
import com.github.syari.spigot.api.config.config
import com.github.syari.spigot.api.config.type.ConfigDataType
import com.github.syari.spigot.api.event.events
import com.github.syari.spigot.api.sound.playSound
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.spigotmc.event.player.PlayerSpawnLocationEvent

class Main : JavaPlugin() {
    companion object {
        internal lateinit var plugin: JavaPlugin
        var loginLocation: Location? = null
    }

    override fun onEnable() {
        plugin = this

        config(server.consoleSender, "config.yml") {
            loginLocation = get("LoginLocation", ConfigDataType.Location, true)
        }

        registerListeners()
    }

    override fun onDisable() {

    }

    fun registerListeners() {
        events {
            event<PlayerSpawnLocationEvent> { e ->
                val loc = loginLocation ?: return@event sendNoLocMessage(e.player)
                e.spawnLocation = loc
            }
        }

        command("login-loc") {
            tab {
                argument {
                    addAll("set", "get")
                }
            }
            execute {
                when (args.lowerOrNull(0)) {
                    "set" -> {
                        if (sender is Player) {
                            val loc = (sender as Player).location
                            loginLocation = loc
                            config(sender, "config.yml") {
                                set("LoginLocation", ConfigDataType.Location, loginLocation, true)
                            }
                            sender.sendMessage("${ChatColor.AQUA}座標(${loc.blockX},${loc.blockY},${loc.blockZ})をサーバー入室時のスポーン地点に設定しました")
                            (sender as Player).playSound(Sound.ENTITY_PLAYER_LEVELUP)
                        } else {
                            sender.sendMessage("${ChatColor.RED}このコマンドはプレイヤーのみ実行できます")
                        }
                    }
                    "get" -> {
                        val loc = loginLocation
                            ?: return@execute sender.sendMessage("${ChatColor.RED}サーバー入室時のスポーン地点は設定されていません")
                        sender.sendMessage("${ChatColor.AQUA}座標(${loc.blockX},${loc.blockY},${loc.blockZ})がサーバー入室時のスポーン地点として設定されています")
                    }
                    else -> {
                        sender.sendMessage("${ChatColor.RED} /login-loc set or /login-loc get")
                    }
                }
            }
        }
    }

    private fun sendNoLocMessage(p: Player) {
        if (p.isOp) p.sendMessage("${ChatColor.RED}[LoginLoc] LoginLocationが設定されていません")
    }
}
