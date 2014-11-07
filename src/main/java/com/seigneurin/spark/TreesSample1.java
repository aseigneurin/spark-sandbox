package com.seigneurin.spark;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;

public class TreesSample1 {

    public static void main(String[] args) {
        SparkConf conf = new SparkConf()
                .setAppName("arbres-alignement")
                .setMaster("local");
        JavaSparkContext sc = new JavaSparkContext(conf);

        long count = sc.textFile("data/arbresalignementparis2010.csv")
                .filter(line -> !line.startsWith("geom"))
                .map(line -> line.split(";"))
                .map(fields -> Float.parseFloat(fields[3]))
                .filter(height -> height > 0)
                .count();
        System.out.println(count);
    }

}
