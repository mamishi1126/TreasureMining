package plugin.treasuremining;

import java.util.HashSet;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import plugin.treasuremining.command.TreasureMiningCommand;

public final class Main extends JavaPlugin  {

  @Override
  public void onEnable() {

    // TreasureMiningCommand のインスタンスを作成
    TreasureMiningCommand treasureMiningCommand = new TreasureMiningCommand();

    // TreasureMiningCommand をリスナーとして登録
    Bukkit.getPluginManager().registerEvents(treasureMiningCommand, this);

    // "treasureMining" コマンドが実行されたときに TreasureMiningCommand を呼び出す
    getCommand("treasureMining").setExecutor(treasureMiningCommand);
  }

}
