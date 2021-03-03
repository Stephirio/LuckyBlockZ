package stephirio.lb;


import net.milkbowl.vault.economy.Economy;
import org.bukkit.*;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import stephirio.lb.commands.LB;
import stephirio.lb.events.BlockBreak;
import stephirio.lb.events.ItemRightClick;
import stephirio.lb.utils.UpdateChecker;

import java.util.ArrayList;


/**
 * The main class of the plugin. This is the first class that gets called by the server.
 *
 * @version 1.0
 * @author Stephirio <stephirioyt@gmail.com>
 */
public final class Main extends JavaPlugin {


    public static ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
    public static Economy econ = null;
    private Integer pluginID = 89700;


    @Override
    public void onEnable() {
        getConfig().options().copyDefaults(true);
        saveDefaultConfig();
        reloadConfig();
        getServer().getPluginManager().registerEvents(new BlockBreak(this), this);
        getCommand("lb").setExecutor(new LB(this));
        getServer().getPluginManager().registerEvents(new ItemRightClick(this), this);    // Not working yet. Coming soon...
        if (!setupEconomy() ) getServer().getPluginManager().disablePlugin(this);
        new UpdateChecker(this, pluginID).getLatestVersion(version -> {
            if(this.getDescription().getVersion().equalsIgnoreCase(version))
                System.out.println(getConfig().getString("chat_prefix") + "The plugin is up to date.");
            else
                System.out.println(getConfig().getString("chat_prefix") + "Please update the plugin. Keeping it outdated could cause errors and malfunction.");
                getServer().broadcast(colors(getConfig().getString("chat_prefix") + ChatColor.RESET + "The plugin is outdated. Please update it."), "lp.outdated_warning");
        });
    }


    /** From MilkBowl's API documentation for Vault
     * @return bool
     */
    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }


    /** Converts the type of a value from String to Material.
     * @param string The string to be converted into material
     * @return String */
    public static Material getMaterialFromString(String string) {
        return Material.valueOf(string.toUpperCase());
    }

    /** A custom method to easily replace color codes.
     * @param string The string to be parsed.
     * @return String */
    public static String colors(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }


    /** Emulates placeholders.
     * @param string The string to be parsed
     * @param player The player to get info from
     * @return String
     */
    public static String customPlaceholders(String string, Player player) { return string.replace("%player%", player.getName()); }


    /** Gets all empty slots from player's inventory.
     * @param player The player to get inventory from
     * @return Integer
     */
    public static Integer getEmptySlots(Player player) {
        PlayerInventory inventory = player.getInventory();
        ItemStack[] cont = inventory.getContents();
        int i = 0;
        for (ItemStack item : cont)
            if (item != null && item.getType() != Material.AIR) {
                i++;
            }
        return 36 - i;
    }


    /** Spawns a firework.
     * @param location The location for the firework to spawn
     * @param amount The amount of firewoks
     * @param colors A custom RGB color of the fireworks
     * @param power The height at which fireworks should rise (1->3)
     */
    public static void spawnFireworks(Location location, Integer amount, ArrayList<Integer> colors, Integer power) {
        Location loc = location.add(0.5, 0.5, 0.5);
        Firework fw = (Firework) loc.getWorld().spawnEntity(loc, EntityType.FIREWORK);
        FireworkMeta fwm = fw.getFireworkMeta();

        fwm.setPower(power);
        fwm.addEffect(FireworkEffect.builder().withColor(Color.fromRGB(colors.get(0), colors.get(1), colors.get(2))).flicker(true).build());
        fw.setFireworkMeta(fwm);
        fw.detonate();

        for(int i = 0;i<amount; i++){
            Firework fw2 = (Firework) loc.getWorld().spawnEntity(loc, EntityType.FIREWORK);
            fw2.setFireworkMeta(fwm);
        }
    }

    /** Spawns an explosion
     * @param location The place where the explosion should spawn
     * @param size The size of the explosion
     * @param amount The amount of explosions
     */
    public static void spawnExplosion(Location location, String size, Integer amount) {
        location.getWorld().createExplosion(location.add(0.5, 0.5, 0.5), 0);
        location.getWorld().spawnParticle(Particle.valueOf("EXPLOSION_" + size.toUpperCase()), location.add(0.5, 0.5, 0.5), amount);
    }

    /** Allows players to create their own particle effects.
     * @param location The location for the effect to happen
     * @param particle The particle that should be shown (A list of the particles <a href="https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Particle.html">here</a>)
     * @param amount The amount of particles
     * @param height The height (0.0->1.0)
     */
    public static void customParticle(Location location, String particle, Integer amount, Float height) {
        location.getWorld().spawnParticle(Particle.valueOf(particle), location.add(0.5, height, 0.5), amount);
    }


}
