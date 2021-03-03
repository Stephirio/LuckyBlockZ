package stephirio.lb.events;

import com.vk2gpz.tokenenchant.api.TokenEnchantAPI;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import stephirio.lb.Main;
import stephirio.lb.utils.ProbabilityCollection;
import java.util.ArrayList;
import java.util.Arrays;


/**This class manages any event that must happen when a block is broken.
 * @author Stephirio <stephirioyt@gmail.com>
 * @version 1.0*/
public class BlockBreak implements Listener {

    private Main main;

    public BlockBreak(Main main) {
        this.main = main;
    }


    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {

        Player player = event.getPlayer();
        ConfigurationSection config = main.getConfig().getConfigurationSection("lucky-setup");
        assert config != null;
        ProbabilityCollection<String> prize_to_give = new ProbabilityCollection<>();
        outerloop:

        for (String broken_block : config.getKeys(false)) {
            if (event.getBlock().getType().equals(Main.getMaterialFromString(broken_block))) {

                event.setDropItems(false);
                event.getBlock().setType(Material.AIR);
                ConfigurationSection broken_conf = config.getConfigurationSection(broken_block);
                ArrayList<String> prizes = new ArrayList<>();

                if (player.hasPermission("lp." + broken_block)) {
                    for (String prize : config.getConfigurationSection(broken_block).getKeys(false)) {
                        if (!prize.equals("permission")) {
                            Integer percentage = Integer.valueOf(broken_conf.getConfigurationSection(prize).getString("percentage").replace("%", ""));
                            prize_to_give.add(prize, percentage);
                        }
                    }

                    ConfigurationSection chosen_prize = broken_conf.getConfigurationSection(prize_to_give.get());
                    ArrayList<String> chosen_commands = new ArrayList<>();
                    String message, broadcast;

                    if (chosen_prize.getKeys(false).contains("items")) {
                        for (String drop_item : chosen_prize.getConfigurationSection("items").getKeys(false)) {
                            if (Main.getEmptySlots(player) != 0) {
                                ConfigurationSection drop_conf = chosen_prize.getConfigurationSection("items").getConfigurationSection(drop_item);
                                Integer amount = drop_conf.getInt("amount");
                                String name;
                                if (chosen_prize.getConfigurationSection("items").getConfigurationSection(drop_item).getKeys(false).contains("name"))
                                    name = drop_conf.getString("name");
                                else name = Main.getMaterialFromString(drop_item).name();
                                ArrayList<String> lore = new ArrayList<>();
                                if (drop_conf.getList("lore") != null)
                                    for (Object lore_line : drop_conf.getList("lore"))
                                        lore.add(Main.colors(lore_line.toString()));
                                String lore_complete = "";
                                for (String lore_string : lore) lore_complete += lore_string + "|";
                                Bukkit.dispatchCommand(Main.console, "give " + player.getName() + " " + Main.getMaterialFromString(drop_item) + " " + amount + " name:" + name.replace(" ", "_") +
                                        (lore != null ? " lore:" + lore_complete.replace(" ", "_") : ""));
                            } else {
                                event.setCancelled(true);
                                player.sendTitle(Main.customPlaceholders(Main.colors(main.getConfig().getString("full_inventory_title")), player), Main.customPlaceholders(Main.colors(main.getConfig().getString("full_inventory_subtitle")), player), 1, 15, 1);
                                break outerloop;
                            }
                        }
                    }

                    if (chosen_prize.getKeys(false).contains("commands"))
                        for (Object command : chosen_prize.getList("commands"))
                            Bukkit.dispatchCommand(Main.console, Main.customPlaceholders((String) command, player));

                    if (chosen_prize.getKeys(false).contains("message"))
                        player.sendMessage(Main.colors(Main.customPlaceholders(main.getConfig().getString("chat_prefix") + ChatColor.RESET + chosen_prize.getString("message"), player)));

                    if (chosen_prize.getKeys(false).contains("broadcast"))
                        Bukkit.getServer().broadcast(Main.colors(Main.customPlaceholders(main.getConfig().getString("chat_prefix") + ChatColor.RESET + chosen_prize.getString("broadcast"), player)), "lb.broadcast");

                    if (chosen_prize.getKeys(false).contains("sound"))
                        player.playSound(player.getLocation(), Sound.valueOf(chosen_prize.getString("sound")), 10, 0);

                    if (chosen_prize.getKeys(false).contains("effects")) {
                        if (chosen_prize.getConfigurationSection("effects").getKeys(false).contains("fireworks")) {
                            ConfigurationSection fireworks = chosen_prize.getConfigurationSection("effects").getConfigurationSection("fireworks");
                            ArrayList<Integer> colors = new ArrayList<>();
                            colors.add(fireworks.getConfigurationSection("color").getInt("R"));
                            colors.add(fireworks.getConfigurationSection("color").getInt("G"));
                            colors.add(fireworks.getConfigurationSection("color").getInt("B"));
                            Main.spawnFireworks(event.getBlock().getLocation(), fireworks.getInt("amount"), colors, fireworks.getInt("power"));
                        }
                        if (chosen_prize.getConfigurationSection("effects").getKeys(false).contains("explosion")) {
                            ConfigurationSection explosion = chosen_prize.getConfigurationSection("effects").getConfigurationSection("explosion");
                            Main.spawnExplosion(event.getBlock().getLocation(), explosion.getString("size"), explosion.getInt("amount"));
                        }
                        for (String custom : chosen_prize.getConfigurationSection("effects").getKeys(false)) {
                            String[] not_allowed = {"fireworks", "explosion"};
                            if (!Arrays.asList(not_allowed).contains(custom)) {
                                ConfigurationSection custom_effect = chosen_prize.getConfigurationSection("effects").getConfigurationSection(custom);
                                Main.customParticle(event.getBlock().getLocation(), custom_effect.getString("particle"), custom_effect.getInt("amount"), Float.parseFloat(custom_effect.getString("height")));
                            }
                        }
                    }

                    if (chosen_prize.getKeys(false).contains("money")) Main.econ.depositPlayer(player, chosen_prize.getDouble("money"));
                    TokenEnchantAPI teAPI = TokenEnchantAPI.getInstance();

                    if (chosen_prize.getKeys(false).contains("tokens")) teAPI.addTokens(player, chosen_prize.getInt("tokens"));
                } else {
                    event.getBlock().setType(Material.NOTE_BLOCK);
                    event.setCancelled(true);
                }
            }
        }
    }
}
