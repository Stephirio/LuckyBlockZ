package stephirio.lb.events;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import stephirio.lb.Main;


/** When a player right clicks with an item in hand and sneaking.
 * */
public class ItemRightClick implements Listener {

    private static Main main;

    public ItemRightClick(Main main) { this.main = main; }

    @EventHandler
    public void onRightClick(PlayerInteractEvent event) {

        if ((event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK))
                && (event.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.DIAMOND_PICKAXE)
                || event.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.DIAMOND_SWORD)
                || event.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.DIAMOND_CHESTPLATE)
                || event.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.DIAMOND_HELMET)
                || event.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.DIAMOND_LEGGINGS)
                || event.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.DIAMOND_BOOTS))
                && event.getPlayer().isSneaking()) {
            Bukkit.dispatchCommand(event.getPlayer(), "gui open enchant");
        }

    }

}
