package com.embaradj.velma.lda;

import cc.mallet.topics.MarginalProbEstimator;
import cc.mallet.topics.ParallelTopicModel;
import cc.mallet.types.InstanceList;
import cc.mallet.util.Randoms;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

/**
 * Responsible for evaluating the hyperparameters.
 */
public class Evaluator {
    StringBuilder builder = new StringBuilder();
    Path path = Path.of("resources/" + "eval.txt");
    InstanceList[] trainingInstance;
    ParallelTopicModel model;
    final int iterations = 100;
    final int threads = 16;

    final Integer[] topics = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
    final Double[] alpha = {0.01, 0.05, 0.1, 0.25, 0.5, 0.75, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0};
    final Double[] beta = {0.01, 0.05, 0.1, 0.25, 0.5, 0.75, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0};

    /**
     * Receives the instance from {@link Modeller} which
     * Ensures that all test cases have gone through the same pipeline.
     * @param instances InstanceList
     */
    public Evaluator(InstanceList instances) {
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

        for (Integer topic : topics) {

            model = new ParallelTopicModel(topic, a, b);

            // Use the first 80% for training
            model.addInstances(trainingInstance[0]);
            model.setNumThreads(threads);
            model.setNumIterations(iterations);

            // Set random seed to reduce randomness
            model.setRandomSeed(42);

            // Build the LDA model
            try {
                model.estimate();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            // Estimate the models perplexity
            MarginalProbEstimator estimator = model.getProbEstimator();
            double logLike = estimator.evaluateLeftToRight(
                    trainingInstance[1], 10, false, null);

            // Build output string
            builder.append(logLike).append(" >> log likelihood with alpha: ").append(a).append(", beta: ").append(b).append(", topics: ").append(topic).append(System.lineSeparator());
        }
    }

    /**
     * Helper class for holding the alpha and beta values.
     * @param a alpha
     * @param b beta
     */
    private record Pair(Double a, Double b) { }
}
