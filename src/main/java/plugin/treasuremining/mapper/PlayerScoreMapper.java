package plugin.treasuremining.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import plugin.treasuremining.mapper.data.PlayerScore;

public interface PlayerScoreMapper {

  //SQLのselectでプレイヤースコア情報を抽出してくる。
  @Select("select * from player_score")
  //抽出したプレイヤースコア情報をマッピングしてセレクトリストにして返す。
  List<PlayerScore> selectList();

  @Insert("insert player_score(player_name, score, registered_at) values (#{playerName}, #{score}, now())")
  int insert(PlayerScore playerScore);

}
