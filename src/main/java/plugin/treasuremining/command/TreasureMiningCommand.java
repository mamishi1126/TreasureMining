package plugin.treasuremining.command;

import java.io.InputStream;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.SplittableRandom;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import plugin.treasuremining.Main;
import plugin.treasuremining.PlayerScoreDataAccess;
import plugin.treasuremining.data.ExecutingPlayer;
import plugin.treasuremining.mapper.PlayerScoreMapper;
import plugin.treasuremining.mapper.data.PlayerScore;


public class TreasureMiningCommand extends BaseCommand implements Listener {

  public static final int GAME_TIME = 60;
  public static final String PLAYER_LIST = "playerList";


  private Main main;
  private PlayerScoreDataAccess playerScoreDataAccess = new PlayerScoreDataAccess();

  private List<ExecutingPlayer> executingPlayerList = new ArrayList<>();


  public TreasureMiningCommand(Main main) {
    this.main = main;
  }


  @Override
  public boolean onExecutePlayerCommand(Player player, Command command, String label, String[] args) {
    //最初の引数が「playerList」の場合、スコアを一覧表示して処理を終了する。
    if (args.length == 1 && PLAYER_LIST.equals(args[0])) {
      sendPlayerScoreList(player);
      return false;
    }

      ExecutingPlayer nowExecutingPlayer = getPlayerScore(player);

      initPlayerStatus(player);

      player.sendTitle("Let's mine the treasure!", "制限時間1分", 10, 70, 20);

      gamePlay(player, nowExecutingPlayer);

      return true;
  }


  /**
   * 現在登録されているスコアの一覧をメッセージに送る。
   * @param player　プレイヤー
   */
  private void sendPlayerScoreList(Player player) {
    List<PlayerScore> playerScoreList = playerScoreDataAccess.selectList();
    for (PlayerScore playerScore : playerScoreList) {
      if (playerScore.getRegisteredAt() != null) {
        // 正常に日時が取得できた場合
        player.sendMessage(playerScore.getId() + " | "
            + playerScore.getPlayerName() + " | "
            + playerScore.getScore() + " | "
            + playerScore.getRegisteredAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
      } else {
        // null だった場合のデバッグメッセージ
        player.sendMessage("Error: 登録日時が取得できませんでした。");
      }
    }
  }


  @Override
  public boolean onExecuteNPCPlayerCommand(CommandSender sender, Command command, String label, String[] args) {
    return false;
  }


  /**
   * ゲーム開始前にプレイヤーの初期設定を行う。
   * @param player
   */
  private static void initPlayerStatus(Player player) {
    player.setFoodLevel(20);
    player.setHealth(20);

    PlayerInventory inventory = player.getInventory();
    inventory.clear();
    inventory.setItemInMainHand(new ItemStack(Material.DIAMOND_PICKAXE));
  }


  /**
   *　ゲームを実行。
   * @param player　コマンドを実行したプレイヤー
   * @param nowExecutingPlayer　プレイヤースコア情報
   */
  private void gamePlay(Player player, ExecutingPlayer nowExecutingPlayer) {
    Bukkit.getScheduler().runTaskTimer(main,Runnable -> {
      if (nowExecutingPlayer.getGameTime() <= 0) {
        Runnable.cancel();
        System.out.println("ゲーム終了");
        player.sendTitle("ゲームが終了しました","スコア：" + nowExecutingPlayer.getScore()+ "点",
            10, 70, 20);

        executingPlayerList.clear();

        player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));

        //スコア登録処理
        playerScoreDataAccess.insert(
            new PlayerScore(nowExecutingPlayer.getPlayerName()
                  , nowExecutingPlayer.getScore()));

        return;
      }

      //// 1秒ごとに残り時間を減少させる
      nowExecutingPlayer.setGameTime(nowExecutingPlayer.getGameTime() - 1);
    }, 0, 20);
  }


  @EventHandler
  public void onBreakBlock(BlockBreakEvent e) {
    Block block = e.getBlock();
    Player player = e.getPlayer();

    executingPlayerList.stream()
          .filter(p -> p.getPlayerName().equals(player.getName()))
          .findFirst()
          .ifPresent(p -> {

            e.setDropItems(false);

            // ランダムに選ばれた鉱石をドロップさせる
            Material randomOre = getRandomOre();
            block.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(randomOre, 1));


            int basePoint = switch (randomOre) {
              case LAPIS_LAZULI -> 20;
              case DIAMOND -> 30;
              case EMERALD -> 60;
              default -> 0;
            };

            int point = basePoint;

            //randomOreとlastDroppedOreが一致した場合、consecutiveDrops+1回となり、
            //(basePointが代入された)pointにbasePointが加算される。
            if (randomOre == p.getLastDroppedOre()) {
              p.setConsecutiveDrops(p.getConsecutiveDrops() + 1);
              point += basePoint;

              System.out.println("連続して" + p.getLastDroppedOre() + "を発掘！ボーナスポイント" + point + "点を獲得！");
              player.sendMessage("連続して" + p.getLastDroppedOre() + "を発掘！ボーナスポイント" + point + "点を獲得！");

            } else {
              p.setConsecutiveDrops(1);
            }

            p.setScore(p.getScore() + point);

            //lastDroppedOreを更新
            p.setLastDroppedOre(randomOre);

            // 新しいスコアを確認
            System.out.println(randomOre + "を発掘！新しいスコア（加算後）: " + p.getScore());

            player.sendMessage(randomOre + "を発掘！現在のポイントは" + p.getScore() + "点！");

          });
  }


  /**
   * ドロップする鉱石をランダムに抽出。
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
    ExecutingPlayer executingPlayer = executingPlayerList.stream()
        .filter(p -> p.getPlayerName().equals(player.getName()))
        .findFirst()
        .orElseGet(() -> addNewPlayer(player));

    executingPlayer.setGameTime(GAME_TIME);
    executingPlayer.setScore(0);
    return executingPlayer;
  }


  /**
   * 新規のプレイヤー情報を追加。
   * @param player
   * @return 新規プレイヤー
   */
  private ExecutingPlayer addNewPlayer(Player player) {
    ExecutingPlayer newExecutingPlayer = new ExecutingPlayer(player.getName());
    executingPlayerList.add(newExecutingPlayer);
    return newExecutingPlayer;
  }

}
