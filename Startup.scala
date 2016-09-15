import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf
import org.apache.spark.streaming.twitter._
import org.apache.spark.streaming.StreamingContext._
import org.elasticsearch.spark._
import org.elasticsearch.spark.rdd.EsSpark

object Startup{
  def main(args: Array[String]) :Unit = {

    // Set Twitter Access Keys
    System.setProperty("twitter4j.oauth.consumerKey", "your key")
    System.setProperty("twitter4j.oauth.consumerSecret", "your secret key")
    System.setProperty("twitter4j.oauth.accessToken", "your token")
    System.setProperty("twitter4j.oauth.accessTokenSecret", "your token secret")

    // Create Stream
    val index_name = "twitter"
    val sparkConf = new SparkConf().setAppName("TwitterSearch").setMaster("local[*]")
    val ssc = new StreamingContext(sparkConf, Seconds(2))
    val stream = TwitterUtils.createStream(ssc, None, args)

    val tweet = stream.map ( status => {
      var lonlat : Array[Double] = Array()
      val hashmap = new java.util.HashMap[String, Object]()
      hashmap.put("user_name", status.getUser().getName())
      hashmap.put("user_lang", status.getUser().getLang())
      hashmap.put("text", status.getText())
      hashmap.put("@create_at", new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX").format(status.getCreatedAt()))
      if(status.getGeoLocation() != null) {
          lonlat = Array(status.getGeoLocation().getLongitude(), status.getGeoLocation().getLatitude())
      }
      hashmap.put("location", lonlat)
      (new org.json.JSONObject(hashmap).toString())
    })

    // Data send to elasticsearch
    tweet.foreachRDD(jsonRDD => {
       EsSpark.saveJsonToEs(jsonRDD, index_name+"/"+args.mkString(";"))
    })

    ssc.start()
    ssc.awaitTermination()
  }
}
