# TreasureMining 
## プロジェクト概要 
TreasureMiningは、Javaによるオブジェクト指向設計、条件分岐、データベース連携等を学ぶために実装したミニゲームです。
このプロジェクト制作を通じて、バックエンドエンジニアとして必要な基礎スキルを習得することを目的としました。
また、ゲームロジックの設計や拡張性を意識したアプローチを取り入れることで、より実践的な成果物を目指しました。  
主な機能として、プレイヤーがランダムに生成される鉱石を採掘し合計スコアを競う仕組みを実装しました。
鉱石の種類ごとに設定されたスコア計算ロジックを条件分岐やループ処理を活用して構築し、リアルタイムでスコアを更新する仕組みを実現しています。
また、スコアやプレイヤーデータは MyBatisを利用してMySQLデータベースに保存し、データが永続的に保持されるようにしています。
 

## 主な実装ポイント
### 条件分岐とループ処理
**・ループ処理と条件分岐**：鉱石をランダムに出現させるループ処理や、鉱石の種類ごとに異なるポイント設定、連続採掘時のボーナスポイント加算を実現する条件分岐を活用。  
**・オブジェクト指向設計**：プレイヤー名やスコアを管理するクラスを設計し、可読性やメンテナンス性の高いコードを実現。  
**・データベース連携と管理**：MyBatisを使用してMySQLデータベースと連携し、ゲーム終了後のスコアデータの永続化を実現。  

# デモ動画
https://github.com/user-attachments/assets/65253c7c-d547-44dc-98e5-64f6a5620b7c

# ディレクトリ構成
* java/plugin/treasuremining  
  * Main.java：プラグインのエントリーポイント  
  * command/
    * BaseCommand.java：コマンドの基底クラス  
    * TreasureMining.java：ゲームのロジックやコマンド処理を実装したクラス
  * data/  
    * ExecutingPlayer.java：プレイヤーのデータオブジェクト
  * mapper/
    * PlayerScoreMapper.java：Mybatisのマッパーインターフェース
  * PlayerScore.java：データベース接続およびスコア管理
  * resources/
    * mybatis-config.xml：Mybatisの設定ファイル


## 機能概要
### ゲームコマンドの実行
* コマンド実行の基底クラス (BaseCommand) にコマンド実行者がプレイヤーかNPCかで処理を分ける抽象クラスを導入。  
&thinsp;これにより、コマンド処理の拡張性を高め、将来的な機能追加を容易にしています。 
* `/trasuremining`コマンドでゲームを開始し、鉱石採掘、スコア管理を行います。

## データベース接続とスコア管理
* MyBatisを用いたデータアクセス: PlayerScoreDataクラスを使用して、MySQLデータベースと接続し、プレイヤーのスコアデータを永続化しています。
* 本プロジェクトではプレイヤーのスコアを管理するために、`player_score`テーブルを使用。

### テーブル: player_score
| カラム名         | データ型         | 説明                   |
|------------------|--------------|------------------------|
| `id`            | int          | プレイヤーID           |
| `player_name`   | varchar(100) | プレイヤーの名前       |
| `score`         | int          | 現在のスコア           |
| `registered_at` | dateTime     | データの登録日時       |

### データ例
| id  | player_name | score | registered_at       |
|-----|-------------|-------|---------------------|
| 1   | playerA     | 120   | 2023-10-25 12:34:56|

### Mybatis設定ファイル
* MyBatisの設定ファイルは以下の通りです。
```java
<configuration>
  <settings>
    <setting name="mapUnderscoreToCamelCase" value="true"/>
  </settings>
  <environments default="development">
    <environment id="development">
      <transactionManager type="JDBC"/>
      <dataSource type="POOLED">
        <property name="driver" value="com.mysql.cj.jdbc.Driver"/>
        <property name="url" value="jdbc:mysql://localhost:3306/treasure_mining"/>
        <property name="username" value="root"/>
        <property name="password" value="mryomend1126t03"/>
      </dataSource>
    </environment>
  </environments>
  <mappers>
    <mapper class="plugin.treasuremining.mapper.PlayerScoreMapper"/>
  </mappers>
</configuration>
```
# コア機能と技術的工夫
## 鉱石のランダム抽出とスコアリング
* 機能概要：プレイヤーがブロック破壊時に、石炭、ラピスラズリ、ダイアモンド、エメラルドの中から1種をランダム抽出し、鉱石の種類によって異なるスコアリングを行います。
* コード例：
```java
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
```            
## 連続採掘によるボーナスポイント加算
* 機能概要：同じ鉱石を連続採掘するごとに、[鉱石ごとの獲得ポイント×連続採掘回数]点を加算します。
* コード例：
```java
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
```          
# まとめ
今回TreasureMiningの制作を通じて、ロジック設計の基礎を身に着けることができ、可読性や保守性の高いコードを書くことの大切さを実感しました。
また、データ永続化や拡張性のある設計を実現することで、バックエンドエンジニアとしての基本的なスキルを習得しました。

### 対応バージョン
・Minecraft：1.20.4  
・Spigot:1.20.4  
