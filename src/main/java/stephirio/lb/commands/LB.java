package stephirio.lb.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import stephirio.lb.Main;

public class LB implements CommandExecutor {

    private static Main main;

    public LB(Main main) { this.main = main; }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender instanceof Player) {

            Player player = (Player) sender;

            if (args.length > 0) {

                if (args[0].equalsIgnoreCase("reload")) {

                    main.reloadConfig();
                    player.sendMessage( Main.customPlaceholders(Main.colors(main.getConfig().getString("chat_prefix") + ChatColor.RESET + "&2The configuration has been reloaded successfully."), player));

                } else if (args[0].equalsIgnoreCase("help")) Bukkit.dispatchCommand(player, "help LuckyBlockZ");

            } else Bukkit.dispatchCommand(player, "help LuckyBlockZ");

        }

        return false;
    }
}
