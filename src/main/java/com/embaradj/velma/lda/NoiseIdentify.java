package com.embaradj.velma.lda;

import cc.mallet.types.Alphabet;
import cc.mallet.types.InstanceList;
import cc.mallet.util.FeatureCountTool;
import com.embaradj.velma.Settings;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.NumberFormat;
import java.util.Formatter;
import java.util.Locale;

/**
 * Generate file for noise identification.
 * The file consists of the number of hits, in how many documents
 * The word was found, including the word.
 */
public class NoiseIdentify {
    public NoiseIdentify(InstanceList instances) {
        StringBuilder preFix = new StringBuilder();
        for (String selCorp : Settings.getCorporapreFixes()) {
            preFix.append(selCorp).append("_");
        }
        Path path = Path.of("resources/" + preFix + "corporaWords.txt");

        FeatureCountTool countTool = new FeatureCountTool(instances);
        countTool.count();
        double[] featureCounts = countTool.getFeatureCounts();
        int[] documentFrequencies = countTool.getDocumentFrequencies();
        Alphabet alphabet = instances.getDataAlphabet();
        NumberFormat nf = NumberFormat.getInstance();
        nf.setMinimumFractionDigits(0);
        nf.setMaximumFractionDigits(6);
        nf.setGroupingUsed(false);

        Formatter hitsDocsText = new Formatter(new StringBuilder(), Locale.US);
        for(int feature = 0; feature < instances.getDataAlphabet().size(); ++feature) {
            hitsDocsText.format("%s\t%d\t%s\n", nf.format(featureCounts[feature]), documentFrequencies[feature], alphabet.lookupObject(feature).toString());

        }
        try {
            Files.writeString(path, hitsDocsText.toString(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
