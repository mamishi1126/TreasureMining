package plugin.treasuremining.mapper.data;

import java.time.LocalDateTime;

/**
 * プレイヤーのスコア情報を扱うオブジェクト。
 * DB(Mybatis)と連動する。
 */


public class PlayerScore {

  private int id;
  private String playerName;
  private int score;
  private LocalDateTime registered_at;


  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
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

  public LocalDateTime getRegistered_at() {
    return registered_at;
  }

  public void setRegistered_at(LocalDateTime registered_at) {
    this.registered_at = registered_at;
  }
}
