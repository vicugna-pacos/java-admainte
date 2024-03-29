# 概要
入力ファイルをもとに、Active Directoryを更新することを目的としたモジュール。

# 前提条件
- Java 8
- Gradle

# パラメータ

| キー| 名前 | 説明 |
|---|---|---|
| -input | 入力ファイルパス | 更新条件、値の一覧が書かれたファイルのパス。必須。 |
| -error | エラーファイルパス | 更新に失敗したデータを出力するファイルのパス。省略可能。 |

# 入力ファイル

|文字コード|区切り文字|ヘッダー行|
|---|---|---|
|UTF-8|タブ|あり|

|No|列名|説明|
|---|---|---|
|1|userId|必須指定。ログオンユーザーID。これをuserPrincipalNameの前方一致で検索し、取得できたDNを更新条件とする。|
|2|name||
|3|displayName|名前(姓名)|
|4|givenName|ファーストネーム|
|5|sn|ラストネーム|
|6|mail|メールアドレス|
|7|pager|ポケットベル|
|8|otherPager|ポケットベル(その他)|

## 不要な項目がある場合
更新対象ではない項目がある場合、その列は入力ファイルから外しても問題ない。ヘッダー行のラベルで、列の紐づけを行っているためである。

## 複数の値をもつ項目について
`otherPager`などは1ユーザーに対して複数の値を設定できる。
入力ファイルでは、同じユーザーIDのデータを複数行続けることで、`otherPager`などに複数値を指定できる。
このときの単一の値の項目は、同じユーザーIDでの先頭行が採用される。しかし、単一項目と複数項目を一つのファイルに記述すると混乱を招くため、例えば`pager`と`otherPager`を更新するためのファイルは分けたほうが良い。

↓`otherPager`を更新するときのファイルのサンプル (taro01は複数のポケベルを持っている)

|userId|otherPager|
|---|---|
|taro01|aaaaaaaa|
|taro01|bbbbbbbb|
|taro01|cccccccc|


# プロパティファイル

モジュールの動作にはプロパティファイルが必要。下記キーを持ったプロパティファイルをクラスパスに含めること。

|ファイル名|
|---|
|config.properties|

|キー|説明|サンプル|
|---|---|---|
|ldap.url | ADサーバーのURL|`ldap://xxx`|
|ldap.principal|ADサーバーに接続するためのユーザーID。|`admin@domain`|
|ldap.password|上記ユーザーIDのパスワード||
|ldap.get_user.name|更新対象ユーザーを取得する際の検索範囲。|`OU=xxx, DC=yyy, DC=zzz`|
|ldap.get_user.filter|上記検索範囲に指定するフィルタ。「[userId]」と書いた場所が入力ファイルのuserIdに置き換わる。|`(&(objectCategory=user)(userPrincipalName=[userId]*))`|

# ログ
logbackを使用している。`logback.xml`などの設定ファイルをクラスパスに含めれば、出力するログレベルなどを操作できる。

# 仕様概要

1. パラメータで指定された入力ファイルを読み取る。
1. 入力ファイルのデータを元にADを更新する。
	- ADに存在しない等の理由で更新できない場合、その行のユーザーのみ更新をスキップする。
1. 入力ファイルの項目が空の場合は、更新対象に含めない(つまり、空白にする更新ができない)。

# 導入方法

1. Gradleのタスク`installDist`を実行する。
1. `プロジェクトフォルダ/build/install`に実行モジュールが作成されるので、コピーして所定の場所に移動する。
1. `bin`フォルダにあるbatファイルを実行する。
