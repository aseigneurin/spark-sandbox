package com.seigneurin.spark;

import java.io.Serializable;
import java.util.Comparator;
import java.util.function.Function;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaSparkContext;

import scala.Tuple2;

public class WikipediaMapReduceByKey {

    public static void main(String[] args) {
        SparkConf conf = new SparkConf()
                .setAppName("wikipedia-mapreduce-by-key")
                .setMaster("local[16]");
        JavaSparkContext sc = new JavaSparkContext(conf);

        JavaPairRDD<String, Long> rdd = sc.textFile("data/wikipedia-pagecounts/pagecounts-20141101-*")
                .map(line -> line.split(" "))
                .mapToPair(s -> new Tuple2<String, Long>(s[0], Long.parseLong(s[2])))
                .reduceByKey((x, y) -> x + y)
                .cache();

        // affichage simple
        rdd
                .foreach(t -> System.out.println(t._1 + " -> " + t._2));

        // tri
        rdd
                .sortByKey()
                .foreach(t -> System.out.println(t._1 + " -> " + t._2));

        // tri lower-case
        // génère une exception :
        //      Task not serializable: java.io.NotSerializableException
        //        rdd
        //                .sortByKey(Comparator.comparing(String::toLowerCase))
        //                .foreach(t -> System.out.println(t._1 + " -> " + t._2));

        // tri lower-case avec un cast (merci Fabien Arrault !)
        Function<String, String> f = (Function<String, String> & Serializable) String::toLowerCase;
        rdd
                .sortByKey(Comparator.comparing(f))
                .foreach(t -> System.out.println(t._1 + " -> " + t._2));

        // tri lower-case
        rdd
                .sortByKey(new LowerCaseStringComparator())
                .foreach(t -> System.out.println(t._1 + " -> " + t._2));

        // tri par valeurs
        rdd
                .mapToPair(t -> new Tuple2<Long, String>(t._2, t._1))
                .sortByKey(false)
                .take(10)
                .forEach(t -> System.out.println(t._2 + " -> " + t._1));
    }

    private static class LowerCaseStringComparator implements Comparator<String>, Serializable {
        @Override
        public int compare(String s1, String s2) {
            return s1.toLowerCase().compareTo(s2.toLowerCase());
        }
    }
}
