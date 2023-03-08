package com.embaradj.velma.lda;

import cc.mallet.pipe.*;
import cc.mallet.pipe.iterator.FileIterator;
import cc.mallet.topics.ParallelTopicModel;
import cc.mallet.types.*;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Responsible for topic modelling using {@link cc.mallet}.
 * Will import '.txt' files, process and train the model.
 */
public class Modeller {
    ArrayList<Pipe> pipeList = new ArrayList<>();
    // High alpha = Each document will contain a mixture of most topics
    // And not one single topic.
    // Low alpha = Each document might contain only a few or just one topic.
    double alpha = 0.01; // Set the alpha value
    // High beta = Each topic is likely to contain a mixture of words
    // Low beta = Each topic may contain a mixture of only a few words.
    double beta = 0.01; // Set the beta value
    int numTopics = 10; // Number of topics to search for
    int threads = 8; // Number of threads to do work on
    int iterations = 2000; // Number of iterations for the modelling

    public Modeller() { }

    /**
     * Receives a prepared '.mallet' file to import data from
     * And train the model and analyze the topic assignments.
     * Finishes by inferring a topic distribution
     * @param file '.mallet'
     */
    public void worker(File file) {
        // Create instance list with the pipeline
        InstanceList instances = new InstanceList (buildPipe());
        // Process each instance provided by the iterator
        instances.addThruPipe(readDir(file));

        // Create the model and set number of topics and alpha, beta value
        // High Alpha = mixture of topics in each file
        // High Beta = mixture of words in each topic
        ParallelTopicModel model =
                new ParallelTopicModel(numTopics, alpha, beta);

        // Add the instances to the model
        model.addInstances(instances);
        // Set number of thread to do work on
        model.setNumThreads(threads);
        // Set the number of iteration to train the model on
        // 50 for testing, 1000-2000 for production mode
        model.setNumIterations(iterations);
        // Build the LDA model
        try {
            model.estimate();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Build the default pipeline which will process the raw data files.
     * @return Pipe
     */
    private Pipe buildPipe() {
        // Reads the data from File and convert to lower case
        pipeList.add(new Input2CharSequence("UTF-8"));
        pipeList.add(new CharSequenceLowercase());

        // Specifies the tokens with regex, includes Unicode letters for non-English text
        Pattern tokenPattern = Pattern.compile("[\\p{L}\\p{M}]+");

        // Tokenize the raw strings
        pipeList.add(new CharSequence2TokenSequence(tokenPattern));

        // Remove stop words
        pipeList.add( new TokenSequenceRemoveStopwords(new File("conf/stopwords-sv.txt"), "UTF-8", false, false, false) );
        pipeList.add( new TokenSequenceRemoveStopwords(new File("conf/stopwords-en.txt"), "UTF-8", false, false, false) );

        // Store the tokens as integers instead of String
        pipeList.add(new TokenSequence2FeatureSequence());

        // Store the label as Label object (integer index of alphabet)
        pipeList.add(new Target2Label());
        return new SerialPipes(pipeList);
    }

    /**
     * Start the recursive reading of directories
     * @param dir to read
     * @return FileIterator
     */
    private FileIterator readDir(File dir) {
        return readDirs(new File[] {dir});
    }

    /**
     * Recursively look through subdirectories
     * And only look for '.txt' files.
     * @param dirs directories
     * @return FileIterator
     */
    private FileIterator readDirs(File[] dirs) {
        return new FileIterator(dirs,
                new TxtFilter(),
                FileIterator.LAST_DIRECTORY);
    }
}
