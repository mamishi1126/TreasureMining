package plugin.treasuremining.command;

import java.util.ArrayList;
import java.util.List;
import java.util.SplittableRandom;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import plugin.treasuremining.Main;
import plugin.treasuremining.data.ExecutingPlayer;


//implements=実装する
//CommandExecutor=コマンドを実行するもの
public class TreasureMiningCommand implements CommandExecutor, Listener {


  private Main main;
  private List<ExecutingPlayer> executingPlayerList = new ArrayList<>();
  private int gameTime = 60;

  //コマンドに対してプラグインの機能をもつことができる。
  public TreasureMiningCommand(Main main) {
    this.main = main;

  }


  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (sender instanceof Player player) {
      ExecutingPlayer executingPlayer = new ExecutingPlayer();
      executingPlayer.setPlayerName(player.getName());
      executingPlayerList.add(executingPlayer);

      gameTime = 60;
      //初期設定
      initPlayerStatus(player);

      player.sendTitle("Let's mine the treasure!", "制限時間3分", 10, 70, 20);

      Bukkit.getScheduler().runTaskTimer(main,Runnable -> {
        if (gameTime <= 0) {
          Runnable.cancel();
          System.out.println("ゲーム終了");
          player.sendTitle("ゲームが終了しました。", null,10, 70, 20);
          return;
        }
        gameTime -= 1;
      },0,20);

      getRandomOre();

    }
    //上記のonCommandメソッドが実行されなかった場合にfalseとみなす。
    return false;
  }



  /**
   * ゲーム開始前にプレイヤーの初期設定を行う
   * @param player
   */
  private static void initPlayerStatus(Player player) {
    player.setFoodLevel(20);
    player.setHealth(20);

    PlayerInventory inventory = player.getInventory();
    inventory.clear();
    inventory.setItemInMainHand(new ItemStack(Material.DIAMOND_PICKAXE));
  }


  @EventHandler
  public void onBreakBlock(BlockBreakEvent e) {
    Block block = e.getBlock();
    Material material = block.getType();
    Player player = e.getPlayer();

    //コマンド実行前にイベント発生した場合のNullチェック
    //if(Object.isNull(player)) {
    //return;
    //}

    //コマンド実行度のNullチェック
    //if (executingPlayerList.isEmpty()) {
    //  return;
    //}

      executingPlayerList.stream()
          .filter(p -> p.getPlayerName().equals(player.getName()))
          .findFirst()
          .ifPresent(p -> {

            // 発掘した鉱石の種類に応じてポイントを加算
            int point = switch (material) {
              case LAPIS_LAZULI -> 20;
              case DIAMOND -> 30;
              case EMERALD -> 60;
              default -> 0;
            };
            p.setScore(p.getScore() + point);

            // ブロックのドロップをキャンセル
            e.setDropItems(false);

            // ランダムに選ばれた鉱石をドロップさせる
            Material randomOre = getRandomOre();
            block.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(randomOre, 1));
          });

  }

  /**
   * ドロップする鉱石をランダムに抽出
   *
   * @return
   */
  private static Material getRandomOre() {
    List<Material> ores = List.of(Material.COAL,Material.LAPIS_LAZULI, Material.DIAMOND,Material.EMERALD);
    int random = new SplittableRandom().nextInt(4);
    return ores.get(random);
  }






}
