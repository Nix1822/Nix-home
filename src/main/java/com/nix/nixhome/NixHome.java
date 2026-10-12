package com.nix.nixhome;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Set;

public final class NixHome extends JavaPlugin {

    private File file;
    private YamlConfiguration data;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        file = new File(getDataFolder(), "homes.yml");
        data = YamlConfiguration.loadConfiguration(file);

        for (String name : new String[]{"sethome", "home", "delhome", "homes"}) {
            getCommand(name).setExecutor(this);
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Players only.");
            return true;
        }

        String homeName = args.length > 0 ? args[0].toLowerCase() : "home";
        if (!homeName.matches("[a-z0-9_-]{1,16}")) {
            msg(player, "Home names: 1-16 letters, numbers, - or _.", NamedTextColor.RED);
            return true;
        }

        switch (command.getName().toLowerCase()) {
            case "sethome" -> setHome(player, homeName);
            case "home" -> goHome(player, homeName);
            case "delhome" -> delHome(player, homeName);
            case "homes" -> listHomes(player);
            default -> { return false; }
        }
        return true;
    }

    private String path(Player player, String name) {
        return player.getUniqueId() + "." + name;
    }

    private void setHome(Player player, String name) {
        String path = path(player, name);
        ConfigurationSection section = data.getConfigurationSection(player.getUniqueId().toString());
        int count = section == null ? 0 : section.getKeys(false).size();
        int max = getConfig().getInt("max-homes", 3);

        if (!data.contains(path) && count >= max) {
            msg(player, "You reached the limit of " + max + " homes.", NamedTextColor.RED);
            return;
        }

        data.set(path, player.getLocation());
        save();
        msg(player, "Home '" + name + "' saved.", NamedTextColor.GREEN);
    }

    private void goHome(Player player, String name) {
        Location location = data.getLocation(path(player, name));
        if (location == null) {
            msg(player, "Home '" + name + "' not found.", NamedTextColor.RED);
            return;
        }
        player.teleport(location);
        msg(player, "Teleported to '" + name + "'.", NamedTextColor.GREEN);
    }

    private void delHome(Player player, String name) {
        String path = path(player, name);
        if (!data.contains(path)) {
            msg(player, "Home '" + name + "' not found.", NamedTextColor.RED);
            return;
        }
        data.set(path, null);
        save();
        msg(player, "Home '" + name + "' deleted.", NamedTextColor.YELLOW);
    }

    private void listHomes(Player player) {
        ConfigurationSection section = data.getConfigurationSection(player.getUniqueId().toString());
        Set<String> names = section == null ? Set.of() : section.getKeys(false);
        if (names.isEmpty()) {
            msg(player, "You have no homes. Use /sethome.", NamedTextColor.YELLOW);
            return;
        }
        msg(player, "Your homes: " + String.join(", ", names), NamedTextColor.GREEN);
    }

    private void save() {
        try {
            data.save(file);
        } catch (IOException e) {
            getLogger().severe("Could not save homes.yml: " + e.getMessage());
        }
    }

    private void msg(Player player, String text, NamedTextColor color) {
        player.sendMessage(Component.text(text, color));
    }
}
