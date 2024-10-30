package plugin.treasuremining.data;

import org.bukkit.Material;
import org.bukkit.entity.Player;

/**
 * TreasureMiningGameのゲームを実行する際のプレイヤー情報を持つオブジェクト
 * ゲームをプレイした回数、合計点数を情報に持つ。
 */


public class ExecutingPlayer {

  private int gameTime;
  private String playerName;
  private int score;

  private Material lastDroppedOre; // 最後にドロップした鉱石
  private int consecutiveDrops;     // 連続ドロップ数


  public ExecutingPlayer(String playerName) {
    this.playerName = playerName;
  }


  public int getGameTime() {
    return gameTime;
  }

  public void setGameTime(int gameTime) {
    this.gameTime = gameTime;
  }

  public String getPlayerName() {
    return playerName;
  }

  public void setPlayerName(String playerName) {
    this.playerName = playerName;
  }

  public int getScore() {
    return score;
  }

  public void setScore(int score) {
    this.score = score;
  }


  public Material getLastDroppedOre() {
    return lastDroppedOre;
  }

  public void setLastDroppedOre(Material lastDroppedOre) {
    this.lastDroppedOre = lastDroppedOre;
  }

  public int getConsecutiveDrops() {
    return consecutiveDrops;
  }

  public void setConsecutiveDrops(int consecutiveDrops) {
    this.consecutiveDrops = consecutiveDrops;
  }
}