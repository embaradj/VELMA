package com.embaradj.velma.lda;

import cc.mallet.pipe.*;
import cc.mallet.pipe.iterator.CsvIterator;
import cc.mallet.topics.ParallelTopicModel;
import cc.mallet.topics.TopicInferencer;
import cc.mallet.types.*;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Responsible for topic modelling using {@link cc.mallet}.
 * Will import data from the '.mallet' file, train the model
 * And analyse the topic assignments.
 */
public class Modeller {
    ArrayList<Pipe> pipeList = new ArrayList<>();
    int numTopics = 10; // Number of topics to search for
    int threads = 2; // Number of threads to do work on
    int iterations = 2000; // Number of iterations for the modelling

    public Modeller() { }

    /**
     * Receives a prepared '.mallet' file to import data from
     * And train the model and analyze the topic assignments.
     * Finishes by inferring a topic distribution
     * @param file '.mallet'
     */
    public void worker(String file) {
        // Apply lowercase, tokenize, remove stop words and map features
        pipeList.add( new CharSequenceLowercase() );
        pipeList.add( new CharSequence2TokenSequence(Pattern.compile("\\p{L}[\\p{L}\\p{P}]+\\p{L}")) );
        pipeList.add( new TokenSequenceRemoveStopwords(new File("conf/stopwords-sv.txt"), "UTF-8", false, false, false) );
        pipeList.add( new TokenSequence2FeatureSequence() );

        InstanceList instances = new InstanceList (new SerialPipes(pipeList));

        // Read the passed file
        Reader reader = null;
        try {
            reader = new InputStreamReader(new FileInputStream(file), "UTF-8");
        } catch (UnsupportedEncodingException | FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        // Creates the layout of the model i.e., data, label, name
        instances.addThruPipe(new CsvIterator(reader, Pattern.compile("^(\\S*)[\\s,]*(\\S*)[\\s,]*(.*)$"),
                3, 2, 1));


        // Creates a model with set amount of topics
        ParallelTopicModel model = new ParallelTopicModel(numTopics, 1.0, 0.01);
        model.addInstances(instances);
        model.setNumThreads(threads); // Set working threads
        model.setNumIterations(iterations); // Set iterations
        try {
            model.estimate();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Map and show the word of the first instance run
        Alphabet dataAlphabet = instances.getDataAlphabet();
        FeatureSequence tokens = (FeatureSequence) model.getData().get(0).instance.getData();
        LabelSequence topics = model.getData().get(0).topicSequence;
        Formatter out = new Formatter(new StringBuilder(), Locale.US);

        for (int i = 0; i < tokens.getLength(); i++) {
            out.format("%s-%d\n", dataAlphabet.lookupObject(tokens.getIndexAtPosition(i)), topics.getIndexAtPosition(i));
        }
        System.out.println(out);

        // Estimate the topic distribution during first instance run
        double[] topicDistr = model.getTopicProbabilities(0);

        // Array of sorted sets of word ID and count
        ArrayList<TreeSet< IDSorter>> topicSortedWords = model.getSortedWords();

        // Top 5 words in the topics with proportions for first document
        for (int topic = 0; topic < numTopics; topic++) {
            Iterator<IDSorter> iterator = topicSortedWords.get(topic).iterator();

            out = new Formatter(new StringBuilder(), Locale.US);
            out.format("%d\t%.3f\t", topic, topicDistr[topic]);
            int rank = 0;

            while (iterator.hasNext() && rank < 5) {
                IDSorter idCountPair = iterator.next();
                out.format("%s (%.0f)", dataAlphabet.lookupObject(idCountPair.getID()), idCountPair.getWeight());
                rank++;
            }
            System.out.println(out);
        }

        // Create a new instance with high probability of first topic
        StringBuilder builder = new StringBuilder();
        Iterator<IDSorter> iterator = topicSortedWords.get(0).iterator();

        int rank = 0;
        while (iterator.hasNext() && rank < 5) {
            IDSorter idCountPair = iterator.next();
            builder.append(dataAlphabet.lookupObject(idCountPair.getID()) + " ");
            rank++;
        }

        // New test instance with empty target and source fields
        InstanceList testing = new InstanceList(instances.getPipe());
        testing.addThruPipe(new Instance(builder.toString(), null, "test instance", null));

        TopicInferencer inferencer = model.getInferencer();
        double[] testProb = inferencer.getSampledDistribution(testing.get(0), 10, 1, 5);
        System.out.println("0\t" + testProb[0]);
    }
}
