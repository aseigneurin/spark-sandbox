package com.seigneurin.spark;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.serializer.KryoSerializer;
import org.apache.spark.streaming.Duration;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.twitter.TwitterUtils;
import org.elasticsearch.spark.rdd.api.java.JavaEsSpark;

import twitter4j.auth.Authorization;
import twitter4j.auth.AuthorizationFactory;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationContext;

import com.cybozu.labs.langdetect.DetectorFactory;
import com.cybozu.labs.langdetect.LangDetectException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.seigneurin.spark.pojo.Tweet;

public class AnalyseTweetsAndroid {

    public static void main(String[] args) throws Exception {

        // Twitter4J
        // IMPORTANT: ajuster vos clés d'API dans twitter4j.properties
        Configuration twitterConf = ConfigurationContext.getInstance();
        Authorization twitterAuth = AuthorizationFactory.getInstance(twitterConf);

        // Jackson
        ObjectMapper mapper = new ObjectMapper();

        // Language Detection
        // IMPORTANT:
        // - prendre les sources depuis https://code.google.com/p/language-detection/
        // - importer le projet dans Eclipse
        // - ajouter une dépendance de spark-sandbox vers language-detection
        // - décommenter ce code et le code dans detectLanguage()
        // - ajuster le chemin ci-dessus
        DetectorFactory.loadProfile("/Users/aseigneurin/dev/language-detection/profiles");

        // Spark
        SparkConf sparkConf = new SparkConf()
                .setAppName("Tweets Android")
                .setMaster("local[2]")
                .set("spark.serializer", KryoSerializer.class.getName())
                .set("es.nodes", "localhost:9200")
                .set("es.index.auto.create", "true")
                .set("es.mapping.timestamp", "createdAt");
        JavaStreamingContext sc = new JavaStreamingContext(sparkConf, new Duration(5000));

        String[] filters = { "#Android" };
        TwitterUtils.createStream(sc, twitterAuth, filters)
                .foreachRDD(rdd -> {
                    JavaRDD<String> tweets = rdd
                            .map(s -> new Tweet(s.getUser().getName(), s.getText(), s.getCreatedAt(),
                                    detectLanguage(s.getText())))
                            .map(t -> mapper.writeValueAsString(t));
                    tweets
                            .foreach(s -> System.out.println(s));
                    JavaEsSpark.saveJsonToEs(tweets, "spark/tweets");
                    return null;
                });

        sc.start();
        sc.awaitTermination();
    }

    private static String detectLanguage(String text) throws LangDetectException {
        /*
        Detector detector = DetectorFactory.create();
        detector.append(text);
        return detector.detect();
        */
        return "en";
    }
}
