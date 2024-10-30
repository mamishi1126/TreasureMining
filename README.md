# TreasureMining 
### ゲーム概要 
制限時間内に採掘した鉱石の合計スコアを競うゲーム

●ゲーム開始： `/treasuremining` コマンドを入力してゲーム開始  
●制限時間：1分  
●鉱石ごとの獲得ポイント  
&emsp;・COAL（石炭）＋0点  
&emsp;・LAPISLAZULI（ラピスラズリ）＋20点  
&emsp;・DIAMOND（ダイアモンド）＋30点  
&emsp;・EMERALD（エメラルド）＋60点  
★連続して同じ鉱石を採掘するとボーナスポイントを加算  
例)エメラルドを連続2回発掘で+20点のボーナスポイント加算  
### ゲーム詳細  
1 `/treasuremining` コマンドを入力することで1分間のゲーム開始。  
2 ブロックを壊すことで鉱石を採掘。鉱石の種類によって異なるポイント、ボーナスポイントを獲得。  
3 ゲーム終了後画面に獲得した合計スコアが表示され、プレイヤー名、獲得スコア、日時がデータベースに保存される。  
[TreasureMining ゲーム動画 .mp4](..%2FTreasureMining%20%83%51%81%5B%83%80%93%AE%89%E6%20.mp4)
### スコア表示
保存されたデータは `/treasuremining playerlist` と入力することで確認可能。  
## データベース設計
本プロジェクトでは、プレイヤーのスコアを管理するために、`player_score`テーブルを使用しています。

### テーブル: player_score
| カラム名         | データ型         | 説明                   |
|------------------|--------------|------------------------|
| `id`            | int          | プレイヤーID           |
| `player_name`   | varchar(100) | プレイヤーの名前       |
| `score`         | int          | 現在のスコア           |
| `registered_at` | dateTime     | データの登録日時       |

#### データ例
| id  | player_name | score | registered_at       |
|-----|-------------|-------|---------------------|
| 1   | playerA     | 120   | 2023-10-25 12:34:56|

### 対応バージョン
・Minecraft：1.20.4  
・Spigot:1.20.4