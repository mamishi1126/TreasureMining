package plugin.treasuremining.command;

import java.io.InputStream;
import java.time.LocalDateTime;
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
import plugin.treasuremining.mapper.PlayerScoreMapper;
import plugin.treasuremining.mapper.data.PlayerScore;


//implements=実装する
//CommandExecutor=コマンドを実行するもの
public class TreasureMiningCommand extends BaseCommand implements Listener {

  public static final int GAME_TIME = 60;
  public static final String PLAYER_LIST = "playerList";


  private Main main;
  private List<ExecutingPlayer> executingPlayerList = new ArrayList<>();

  //Sqlsessionfactory:sqlsession(コマンド実行、mapperの取得、トランザクション管理を行う)を作成するメソッド。
  private SqlSessionFactory sqlSessionFactory;


  //コマンドに対してプラグインの機能をもつことができる。
  public TreasureMiningCommand(Main main) {
    this.main = main;
    try {
      InputStream inputStream = Resources.getResourceAsStream("mybatis-config.xml");
      this.sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }


  @Override
  public boolean onExecutePlayerCommand(Player player, Command command, String label, String[] args) {
    if (args.length == 1 && PLAYER_LIST.equals(args[0])) {
      try (SqlSession session = sqlSessionFactory.openSession()){
        PlayerScoreMapper mapper = session.getMapper(PlayerScoreMapper.class);
        List<PlayerScore> playerScoreList = mapper.selectList();

        for (PlayerScore playerScore : playerScoreList) {
          if (playerScore.getRegistered_at() != null) {
            // 正常に日時が取得できた場合
            player.sendMessage(playerScore.getId() + " | "
                + playerScore.getPlayerName() + " | "
                + playerScore.getScore() + " | "
                + playerScore.getRegistered_at().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
          } else {
            // null だった場合のデバッグメッセージ
            player.sendMessage("Error: 登録日時が取得できませんでした。");
          }
        }
      }
      return false;
    }

      ExecutingPlayer nowExecutingPlayer = getPlayerScore(player);
      //nowExecutingPlayer.setGameTime(GAME_TIME);

      initPlayerStatus(player);

      player.sendTitle("Let's mine the treasure!", "制限時間1分", 10, 70, 20);

      gamePlay(player, nowExecutingPlayer);

      return true;
  }


  @Override
  public boolean onExecuteNPCPlayerCommand(CommandSender sender, Command command, String label, String[] args) {
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


  /**
   *　ゲームを実行します。
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

        //nowExecutingPlayer.setScore(0);

        executingPlayerList.clear();

        player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));

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

    //if(player == null ) {
      //return;
    //}

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
    ExecutingPlayer executingPlayer = executingPlayerList.stream()
        .filter(p -> p.getPlayerName().equals(player.getName()))
        .findFirst()
        .orElseGet(() -> addNewPlayer(player));

    executingPlayer.setGameTime(GAME_TIME);
    executingPlayer.setScore(0);
    return executingPlayer;
  }


  /**
   * 新規のプレイヤー情報を追加
   * @param player
   * @return 新規プレイヤー
   */
  private ExecutingPlayer addNewPlayer(Player player) {
    ExecutingPlayer newExecutingPlayer = new ExecutingPlayer(player.getName());
    executingPlayerList.add(newExecutingPlayer);
    return newExecutingPlayer;
  }

}
