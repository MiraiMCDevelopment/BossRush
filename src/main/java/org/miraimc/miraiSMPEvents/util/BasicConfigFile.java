package org.miraimc.miraiSMPEvents.util;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
@Setter
public class BasicConfigFile {

    private JavaPlugin plugin;
    private String fileName;
    private YamlConfiguration configuration;

    private File file;

    public BasicConfigFile(JavaPlugin plugin, String fileName) {
        this.plugin = plugin;
        this.fileName = fileName;
        this.file = new File(plugin.getDataFolder(), fileName);
        if (!this.file.exists()) {
            plugin.saveResource(fileName, false);
        }
        this.load();
    }

    public boolean getBoolean(String path) {
        return (this.configuration.contains(path)) && (this.configuration.getBoolean(path));
    }

    public double getDouble(String path) {
        if (this.configuration.contains(path)) {
            return this.configuration.getDouble(path);
        }
        return 0.0D;
    }

    public File getFile() {
        return this.file;
    }

    public int getInt(String path) {
        if (this.configuration.contains(path)) {
            return this.configuration.getInt(path);
        }
        return 0;
    }

    public String getString(String path) {
        if (this.configuration.contains(path)) {
            return ChatColor.translateAlternateColorCodes('&', this.configuration.getString(path));
        }
        return path;
    }

    public List<String> getStringList(String path) {
        if (this.configuration.contains(path)) {
            ArrayList<String> strings = new ArrayList<String>();
            for (String string : this.configuration.getStringList(path)) {
                strings.add(ChatColor.translateAlternateColorCodes('&', string));
            }
            return strings;
        }
        return Collections.singletonList(path);
    }

    public List<?> getList(String path) {
        if (this.configuration.contains(path)) {
            return this.configuration.getList(path);
        }
        return Collections.emptyList();
    }

    public void setBoolean(String path, boolean value) {
        this.configuration.set(path, value);
    }

    public void setDouble(String path, double value) {
        this.configuration.set(path, value);
    }

    public void setInt(String path, int value) {
        this.configuration.set(path, value);
    }

    public void setString(String path, String value) {
        this.configuration.set(path, value);
    }

    public void setStringList(String path, List<String> value) {
        this.configuration.set(path, value);
    }

    public void set(String path, Object value) {
        this.configuration.set(path, value);
    }

    public void remove(String path) {
        this.configuration.set(path, null);
    }

    public void load() {
        this.file = new File(plugin.getDataFolder(), fileName);
        if (!this.file.exists()) {
            plugin.saveResource(fileName, false);
        }
        this.configuration = YamlConfiguration.loadConfiguration(this.file);

        InputStream defConfigStream = plugin.getResource(fileName);
        if (defConfigStream != null) {
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defConfigStream));
            this.configuration.setDefaults(defConfig);
            this.configuration.options().copyDefaults(true);
            save();
        }
    }

    public void save() {
        try {
            this.configuration.save(this.file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void reload() {
        load();
        save();
    }

    public ConfigurationSection getConfigurationSection(final String path) {
        if (this.configuration.contains(path)) {
            return this.configuration.getConfigurationSection(path);
        }
        return null;
    }

}
