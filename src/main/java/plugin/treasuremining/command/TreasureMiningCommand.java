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
  private ExecutingPlayer nowExecutingPlayer;

  //コマンドに対してプラグインの機能をもつことができる。
  public TreasureMiningCommand(Main main) {
    this.main = main;

  }


  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (sender instanceof Player player) {
      ExecutingPlayer nowExecutingPlayer = getPlayerScore(player);
      nowExecutingPlayer.setGameTime(60);

      //初期設定
      initPlayerStatus(player);

      player.sendTitle("Let's mine the treasure!", "制限時間1分", 10, 70, 20);

      Bukkit.getScheduler().runTaskTimer(main,Runnable -> {
        if (nowExecutingPlayer.getGameTime() <= 0) {
          Runnable.cancel();
          System.out.println("ゲーム終了");
          player.sendTitle("ゲームが終了しました","スコア：" + nowExecutingPlayer.getScore()+ "点",
              10, 70, 20);
          nowExecutingPlayer.setScore(0);
          return;
        }
        nowExecutingPlayer.setGameTime(nowExecutingPlayer.getGameTime() - 1);
      }, 0, 20);
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
    Player player = e.getPlayer();

    //コマンド実行前にイベント発生した場合のNullチェック
    if(player == null) {
      return;
    }

    //コマンド実行度のNullチェック
    if (executingPlayerList.isEmpty()) {
      return;
    }


      executingPlayerList.stream()
          .filter(p -> p.getPlayerName().equals(player.getName()))
          .findFirst()
          .ifPresent(p -> {


            e.setDropItems(false);

            // ランダムに選ばれた鉱石をドロップさせる
            Material randomOre = getRandomOre();
            block.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(randomOre, 1));


            // 発掘した鉱石の種類に応じてポイントを加算
            //★ここのdefaultしか機能していないかも？
            //getRandamOre()は機能していてMaterial名は出るのかも
            //リファクタリング前から作成してみる
            int point = switch (randomOre) {
              case LAPIS_LAZULI -> 20;
              case DIAMOND -> 30;
              case EMERALD -> 60;
              default -> 0;
            };

            p.setScore(p.getScore() + point);

            // 新しいスコアを確認
            System.out.println(randomOre + "を発掘！新しいスコア（加算後）: " + p.getScore());

            player.sendMessage(randomOre + "を発掘！現在のポイントは" + p.getScore() + "点！");
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


  /**
   * 現在実行しているプレイヤーのスコア情報を取得。
   * @param player コマンドを実行したプレイヤー
   * @return 現在実行しているプレイヤーのスコア情報
   */
  private ExecutingPlayer getPlayerScore(Player player) {
    for (ExecutingPlayer executingPlayer : executingPlayerList) {
      if (executingPlayer.getPlayerName().equals(player.getName())) {
        // プレイヤーがリストに存在する場合、そのプレイヤーを返す
        return executingPlayer;
      }
    }
    return addNewPlayer(player);
  }

    //if (executingPlayerList.isEmpty()) {
      //executingPlayerListが空であれば新規プレイヤーを返す(存在しているがリストにない場合)。
      //return addNewPlayer(player);
    //} else {
      //for (ExecutingPlayer executingPlayer : executingPlayerList) {
       // if (!executingPlayer.getPlayerName().equals(player.getName())) {
          //プレイヤー情報が損際していない場合も新規プレイヤーを返す。
         // return addNewPlayer(player);
       // } else {
          //ifに引っかからない場合(=プレイヤー情報がリストに入っていて存在している場合)executingPlayerを返す。
        //  return executingPlayer;
   //     }
   //   }
   // }
   // return null;



  /**
   * 新規のプレイヤー情報を追加
   * @param player
   * @return 新規プレイヤー
   */
  private ExecutingPlayer addNewPlayer(Player player) {
    ExecutingPlayer newExecutingPlayer = new ExecutingPlayer();
    newExecutingPlayer.setPlayerName(player.getName());
    executingPlayerList.add(newExecutingPlayer);
    return newExecutingPlayer;
  }

}
