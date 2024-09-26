package plugin.treasuremining.command;

import java.util.HashSet;
import java.util.Set;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

//implements=実装する
//CommandExecutor=コマンドを実行するもの
public class TreasureMiningCommand implements CommandExecutor, Listener {


  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (sender instanceof Player player) {

      //初期設定
      player.setFoodLevel(20);
      player.setHealth(20);

      PlayerInventory inventory = player.getInventory();
      inventory.setItemInMainHand(new ItemStack(Material.DIAMOND_PICKAXE));

      player.sendTitle("Let's mine the treasure!", null, 10, 70, 20);

    }
    //上記のonCommandメソッドが実行されなかった場合にfalseとみなす。
    return false;
  }

  @EventHandler
  public void onBreakBlock(BlockBreakEvent e) {
    Block block = e.getBlock();
    Material material = block.getType();
    Player player = e.getPlayer();

    executingPlayerList.stream()
        .filter(p -> p.getPlayer().equals(player))
        .findFirst()
        .ifPresent(p -> {
          int point = switch (material) {
            case DEEPSLATE_IRON_ORE -> 10;
            case DEEPSLATE_LAPIS_ORE -> 20;
            case DEEPSLATE_REDSTONE_ORE -> 30;
            case DEEPSLATE_DIAMOND_ORE -> 60;
            default -> 0;
          };
          p.setScore(p.getScore() + point);
        });
  }



}
