package plugin.treasuremining.data;

/**
 * MiningGameのゲームを実行する際のプレイヤー情報を持つオブジェクト
 * ゲームをプレイした回数、合計点数、日時を情報に持つ。
 */


public class ExecutingPlayer {

  private String playerName;
  private int score;

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