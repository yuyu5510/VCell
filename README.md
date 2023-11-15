# VCells

![VCells: Simple and Efficient Superpixels Using Edge-Weighted Centroidal Voronoi Tessellations](https://ieeexplore.ieee.org/abstract/document/6186738)の論文内容の実装です。

## 実行環境
実行環境は以下のとおりです。
javafx はこのディレクトリの一個上の階層におくか、Makefileで適切なパスを指定してください。

Java
```
openjdk version "18.0.2" 2022-07-19
OpenJDK Runtime Environment Corretto-18.0.2.9.1 (build 18.0.2+9-FR)
OpenJDK 64-Bit Server VM Corretto-18.0.2.9.1 (build 18.0.2+9-FR, mixed mode, sharing)
```

javafx
```
javafx-sdk-17.0.7
```


## 実行方法
make コマンドでウィンドウが起動します。

スライダーは6角形の1ペンの長さを5から50まで指定することができ、初期状態では10が指定されています。

step を 1回押すと、元画像が表示されます。\
step を もう1回押すと、画像を6角形での分割を行います。\
step を もう1回押すと、EWCVT_LNN アルゴリズムに基づいたセグメンテーションを行います。\
step を もう1回押すと、DSBアルゴリズムを行い壊れた分割を検出し、壊れている分割の番号を標準出力に表示し、新たな分割とします。

original を 押すと画像のセグメンテーションが初期化されます。\
初期状態の6角形を変えて実行したい場合はスライダーを動かした後にoriginal ボタンをおし、stepボタンを押す必要があります。\

finish ボタンを押すと、表示されている画像のその時点での分割を緑の線で表示した、output.bmpと、分割の番号で表示した output.txtを出力して終了します。

## 初期分割アルゴリズム
初期分割の6角形の分割は論文の手法ではないため記述しておきます。

6角形の辺の長さが入力として与えられます。\
6角形の中心が定まると辺の長さの情報を用いて中心から各頂点までのベクトルが求まり6つの三角形に対して、ベクトルの外積を用いて三角形の内部判定を行っています。\
それだと座標を int にキャストしている関係上いくつかのピクセルがどの６角形にも属しないので、そのような点は1番近いグループにくっつける処理を行っています。この操作を行なっても六角形のような見た目は崩れません。


