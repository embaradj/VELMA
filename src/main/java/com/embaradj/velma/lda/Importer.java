package com.embaradj.velma.lda;

import cc.mallet.pipe.*;
import cc.mallet.pipe.iterator.FileIterator;
import cc.mallet.types.InstanceList;

import java.io.File;
import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 * Responsible for importing text and process it for use with {@link Modeller}.
 */
public class Importer {
    Pipe pipe;

    public Importer() {
        pipe = buildPipe();
    }

    public Pipe buildPipe() {
        ArrayList pipeList = new ArrayList();

        // Reads the data from File
        pipeList.add(new Input2CharSequence("UTF-8"));

        // Specifies the tokens with regex, includes Unicode letters for non-English text
        Pattern tokenPattern = Pattern.compile("[\\p{L}\\p{M}]+");

        // Tokenize the raw strings
        pipeList.add(new CharSequence2TokenSequence(tokenPattern));

        // Set all tokens to lowercase
        pipeList.add(new TokenSequenceLowercase());

        // Remove stop words
        pipeList.add( new TokenSequenceRemoveStopwords(new File("conf/stopwords-sv.txt"), "UTF-8", false, false, false) );

        // Store the tokens as integers instead of String
        pipeList.add(new TokenSequence2FeatureSequence());

        // Store the label as Label object (integer index of alphabet)
        pipeList.add(new Target2Label());

        // Convert sequence of features to vector
        pipeList.add(new FeatureSequence2FeatureVector());

        // Print out features and label
        pipeList.add(new PrintInput());

        return new SerialPipes(pipeList);
    }

    /**
     * Start the recursive reading of directories.
     * @param dir directory
     * @return recursive reading of directories
     */
    public InstanceList readDir(File dir) {
        return readDirs(new File[] {dir});
    }

    /**
     * Recursively look through subdirectories with a
     * FileIterator selecting files, applying a filter and creating a label.
     * @param dirs directories
     * @return InstanceList (ArrayList) of files
     */
    public InstanceList readDirs(File[] dirs) {
        FileIterator iterator =
                new FileIterator(dirs, new TxtFilter(), FileIterator.LAST_DIRECTORY);

        InstanceList inst = new InstanceList(pipe);
        inst.addThruPipe(iterator);
        return inst;
    }
}
