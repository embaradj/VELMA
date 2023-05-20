package com.embaradj.velma.lda;

import cc.mallet.topics.MarginalProbEstimator;
import cc.mallet.topics.ParallelTopicModel;
import cc.mallet.types.InstanceList;
import cc.mallet.util.Randoms;
import com.embaradj.velma.Settings;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

/**
 * Responsible for evaluating the hyperparameters and number of topics.
 */
public class Evaluator {
    StringBuilder builder = new StringBuilder();
    InstanceList[] trainingInstance;
    ParallelTopicModel model;
    final Integer[] topics = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
    final Double[] alpha = {0.01, 0.05, 0.1, 0.25, 0.5, 0.75, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0};
    final Double[] beta = {0.01, 0.05, 0.1, 0.25, 0.5, 0.75, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0};

    /**
     * Receives the instance from {@link Modeller} which
     * Ensures that all test cases have gone through the same pipeline.
     * @param instances InstanceList
     */
    public Evaluator(InstanceList instances) {
        StringBuilder preFix = new StringBuilder();
        for (String selCorp : Settings.getCorporapreFixes()) {
            preFix.append(selCorp).append("_");
        }
        Path path = Path.of("resources/" + preFix + "eval.txt");

        // Split dataset into 80% for training and 20% for testing:
        trainingInstance = instances.split(new Randoms(), new
                double[] {0.8, 0.2, 0.0});

        // Create all combinations of alpha, beta arrays and run evaluate
        Arrays.stream(alpha)
                .flatMap(a -> Arrays.stream(beta).map(b -> new Pair(a, b)))
                .forEach(this::evaluate);

        try { // Write results to file
            Files.writeString(path, builder.toString(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Creates a model based on the incoming {@link Pair}.
     * Estimates the model and append to the output string.
     * @param pair Pair
     */
    private void evaluate(Pair pair) {
        double a = pair.a;
        double b= pair.b;

        for (int i = 0; i < topics.length; i++) {

            // Create the model with n topics and the alpha, beta value
            model = new ParallelTopicModel(i+1, a, b);

            // Use the first 80% for training
            model.addInstances(trainingInstance[0]);

            // Set random seed to reduce randomness
            model.setRandomSeed(42);

            // Estimate the models total log likelihood
            MarginalProbEstimator estimator = model.getProbEstimator();
            double logLike = estimator.evaluateLeftToRight(
                    trainingInstance[1], 10, false, null);

            // Build output string
            builder.append(logLike)
                    .append(" >> log likelihood with alpha: ")
                    .append(a)
                    .append(", beta: ")
                    .append(b)
                    .append(", topics: ")
                    .append(i+1)
                    .append(System.lineSeparator());
        }
    }

    /**
     * Helper class for holding the alpha and beta values.
     * @param a alpha
     * @param b beta
     */
    private record Pair(Double a, Double b) { }
}
