package com.seigneurin.spark;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

public class TreesSample2 {

    public static void main(String[] args) {
        SparkConf conf = new SparkConf()
                .setAppName("arbres-alignement")
                .setMaster("local");
        JavaSparkContext sc = new JavaSparkContext(conf);

        JavaRDD<Float> rdd = sc.textFile("data/arbresalignementparis2010.csv")
                .filter(line -> !line.startsWith("geom"))
                .map(line -> line.split(";"))
                .map(fields -> Float.parseFloat(fields[3]))
                .filter(height -> height > 0)
                .cache();

        float totalHeight = rdd.reduce((x, y) -> x + y);

        long count = rdd.map(item -> 1)
                .reduce((x, y) -> x + y);

        System.out.println("Total height: " + totalHeight);
        System.out.println("Count: " + count);
        System.out.println("Average height " + totalHeight / count);
    }

}
