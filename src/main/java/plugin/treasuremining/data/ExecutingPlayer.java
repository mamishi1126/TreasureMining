package plugin.treasuremining.data;

import org.bukkit.entity.Player;

/**
 * TreasureMiningGameのゲームを実行する際のプレイヤー情報を持つオブジェクト
 * ゲームをプレイした回数、合計点数を情報に持つ。
 */


public class ExecutingPlayer {

  private int gameTime;
  private String playerName;
  private int score;

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

 

}