package plugin.treasuremining.mapper.data;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.Material;

/**
 * プレイヤーのスコア情報を扱うオブジェクト。
 * DB(Mybatis)と連動する。
 */



@NoArgsConstructor
public class PlayerScore {

  private int id;
  private String playerName;
  private int score;
  private LocalDateTime registeredAt;



  public PlayerScore(String playerName, int score){
    this.playerName = playerName;
    this.score = score;
  }


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

  public LocalDateTime getRegisteredAt() {
    return registeredAt;
  }

  public void setRegisteredAt(LocalDateTime registeredAt) {
    this.registeredAt = registeredAt;
  }

}
