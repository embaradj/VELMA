package com.embaradj.velma.lda;

import cc.mallet.pipe.*;
import cc.mallet.pipe.iterator.FileIterator;
import cc.mallet.topics.ParallelTopicModel;
import cc.mallet.types.*;
import com.embaradj.velma.Settings;
import com.embaradj.velma.models.DataModel;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Responsible for topic modelling using {@link cc.mallet}.
 * Will import '.txt' files, process and train the model.
 */
public class Modeller {
    DataModel dataModel;
    ArrayList<Pipe> pipeList = new ArrayList<>();
    ParallelTopicModel model;
    // High alpha = Each document will contain a mixture of most topics
    // And not one single topic.
    // Low alpha = Each document might contain only a few or just one topic.
    double alpha = Settings.getAlpha(); // Set the alpha value
    // High beta = Each topic is likely to contain a mixture of words
    // Low beta = Each topic may contain a mixture of only a few words.
    double beta = Settings.getBeta(); // Set the beta value
    int numTopics = Settings.getNumTopics(); // Number of topics to search for
    int threads = Settings.getThreads(); // Number of threads to do work on
    int iterations = Settings.getIterations(); // Number of iterations for the modelling

    public Modeller(DataModel dataModel) {
        this.dataModel = dataModel;
    }

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
        model = new ParallelTopicModel(numTopics, alpha, beta);

        // Add the instances to the model
        model.addInstances(instances);
        // Set number of thread to do work on
        model.setNumThreads(threads);
        // Set the number of iteration to train the model on
        // 50 for testing, 1000-2000 for production mode
        model.setNumIterations(iterations);

        // Hyperparameter optimization
        // Will change alpha and beta to allow for some topics to be more pronounced
        // Needs further testing before use
//        model.setOptimizeInterval(10);

        model.setRandomSeed(42);


        // Build the LDA model
        try {
            model.estimate();
            saveResults();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Find topics and top words
        List<Object[]> topicWords = Arrays.stream(model.getTopWords(Settings.getWords())).toList();

        // Add topics and related words to datamodel
        for (int i = 0; i < topicWords.size(); i++) {
            dataModel.addLDATopics(String.valueOf(i), Arrays.toString(topicWords.get(i))
                    .replaceAll("[\\[\\]]+", ""));
        }

        // Used for creating the noise identification files
//        new NoiseIdentify(instances);

        // Used for evaluating the modeller
//        new Evaluator(instances);
    }

    /**
     * Save the results of the LDA to file.
     * The file will be named according to what datasets are used in the LDA.
     */
    private void saveResults() {
        StringBuilder preFix = new StringBuilder();
        for (String selCorp : Settings.getCorporapreFixes()) {
            preFix.append(selCorp).append("_");
        }
        Path path = Path.of("resources/" + preFix + "topics.txt");

        try {
            model.printTopWords(path.toFile(), Settings.getWords(), true);
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
        Pattern tokenPattern = Pattern.compile("[\\p{L}\\p{M}#+]+");

        // Tokenize the raw strings
        pipeList.add(new CharSequence2TokenSequence(tokenPattern));

        // Load stopword files from jar
        InputStream stopWordsEnInput = getClass().getResourceAsStream("/stopwords-en.txt");
        InputStream stopWordsSvInput = getClass().getResourceAsStream("/stopwords-sv.txt");
        InputStream stopWordsNoiseInput = getClass().getResourceAsStream("/stopwords-noise.txt");

        try { // Create readable files to add to pipe list
            File stopWordsEn = File.createTempFile(String.valueOf(stopWordsEnInput.hashCode()), ".tmp");
            File stopWordsSv = File.createTempFile(String.valueOf(stopWordsSvInput.hashCode()), ".tmp");
            File stopWordsNoise = File.createTempFile(String.valueOf(stopWordsNoiseInput.hashCode()), ".tmp");
            FileUtils.copyInputStreamToFile(stopWordsEnInput, stopWordsEn);
            FileUtils.copyInputStreamToFile(stopWordsSvInput, stopWordsSv);
            FileUtils.copyInputStreamToFile(stopWordsNoiseInput, stopWordsNoise);

            // Remove stop words
            pipeList.add( new TokenSequenceRemoveStopwords(stopWordsEn, "UTF-8", false, false, false) );
            pipeList.add( new TokenSequenceRemoveStopwords(stopWordsSv, "UTF-8", false, false, false) );
            // Custom stopwords filter for identified noise
            pipeList.add( new TokenSequenceRemoveStopwords(stopWordsNoise, "UTF-8", false, false, false) );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Store the tokens as integers instead of String
        pipeList.add(new TokenSequence2FeatureSequence());

        // Store the label as Label object (integer index of alphabet)
        pipeList.add(new Target2Label());
        return new SerialPipes(pipeList);
    }

    /**
     * Responsible for saving the trained model to file.
     */
    public void saveModel() {
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(
                    new FileOutputStream(new File("resources/processeddata/data"+".model")));
            oos.writeObject(model);
            oos.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Responsible for loading a trained model.
     */
    public void loadModel() {
        try {
            ObjectInputStream ois = new ObjectInputStream(
                    new FileInputStream (new File("resources/processeddata/data"+".model")));
            ParallelTopicModel model = (ParallelTopicModel) ois.readObject();
            ois.close();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
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
