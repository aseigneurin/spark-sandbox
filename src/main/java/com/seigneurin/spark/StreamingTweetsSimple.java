package com.seigneurin.spark;

import java.util.Arrays;

import org.apache.spark.SparkConf;
import org.apache.spark.streaming.Duration;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.twitter.TwitterUtils;

import twitter4j.auth.Authorization;
import twitter4j.auth.AuthorizationFactory;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationContext;

public class StreamingTweetsSimple {

    public static void main(String[] args) {

        // Twitter4J
        // IMPORTANT: adjust your API keys in the twitter4J.properties file
        Configuration twitterConf = ConfigurationContext.getInstance();
        Authorization twitterAuth = AuthorizationFactory.getInstance(twitterConf);

        // Spark
        SparkConf sparkConf = new SparkConf()
                .setAppName("Tweets Android")
                .setMaster("local[2]");
        JavaStreamingContext sc = new JavaStreamingContext(sparkConf, new Duration(5000));

        // basic stats on tweets
        String[] filters = { "#Android" };
        TwitterUtils.createStream(sc, twitterAuth, filters)
                .flatMap(s -> Arrays.asList(s.getHashtagEntities()))
                .map(h -> h.getText().toLowerCase())
                .filter(h -> !h.equals("android"))
                .countByValue()
                .print();

        sc.start();
        sc.awaitTermination();
    }
}
