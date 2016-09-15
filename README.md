# gather_tweet
Gatherting tweet by Spark straming 

■事前導入ソフトウェア
<li>jdk 1.6
<li>spark 1.6.2
<li>elasticsearch
<li>kuromoji
<li>kibana
<li>nginx
<li>firewalld

■Spark Streamingによるつぶやき収集方法
<li>例:"iPhone7"のつぶやきを収集する
<li>spark-submit --class "Startup" Twitter-assembly-1.0.jar "iPhone7"
<li>例:"iPhone7"と"iOS10"のつぶやきを収集する
<li>spark-submit --class "Startup" Twitter-assembly-1.0.jar "iPhone7" "iOS10"
