package plugin.treasuremining;

import java.io.InputStream;
import java.util.List;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import plugin.treasuremining.mapper.PlayerScoreMapper;
import plugin.treasuremining.mapper.data.PlayerScore;

/**
 * DB接続やそれに付随する登録や更新処理を行うクラス。
 *
 */
public class PlayerScoreDataAccess {


  private SqlSessionFactory sqlSessionFactory;


  public PlayerScoreDataAccess() {
    try {
      InputStream inputStream = Resources.getResourceAsStream("mybatis-config.xml");
      this.sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }


  /**
   * player_scoreテーブルから一覧でスコア情報を取得。
   *
   * @return　スコア情報の一覧
   */
  public List<PlayerScore> selectList() {
    try (SqlSession session = sqlSessionFactory.openSession()) {
      PlayerScoreMapper mapper = session.getMapper(PlayerScoreMapper.class);
      return mapper.selectList();
    }
  }


  /**
   * player_scoreテーブルにスコア情報を登録。
   *
   * @param playerScore　プレイヤースコア
   */
  public void insert(PlayerScore playerScore) {
    try (SqlSession session = sqlSessionFactory.openSession(true)){
      PlayerScoreMapper mapper = session.getMapper(PlayerScoreMapper.class);
      mapper.insert(playerScore);
    }
  }
}
