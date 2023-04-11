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
    final int numTopics = 5;
    final int iterations = 2000;
    final int threads = 16;
    final Double[] alpha = {0.05, 0.1, 0.5, 1.0, 5.0, 10.0};
    final Double[] beta = {0.05, 0.1, 0.5, 1.0, 5.0, 10.0};

    /**
     * Receives the instance from {@link Modeller} which
     * Ensures that all test cases have gone through the same pipeline.
     * @param instances InstanceList
     */
    public Evaluator(InstanceList instances) {
        // Split dataset into 80% for training and 10% for testing:
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
        model = new ParallelTopicModel(numTopics, a, b);

        // Use the first 90% for training
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

        // Estimator
        MarginalProbEstimator estimator = model.getProbEstimator();
        double logLike = estimator.evaluateLeftToRight(
                trainingInstance[1], 10, false, null);

        // Build output string
        builder.append(logLike).append(" >> log likelihood with alpha: ").append(a).append(", beta: ").append(b).append(System.lineSeparator());
    }

    /**
     * Helper class for holding the alpha and beta values.
     * @param a alpha
     * @param b beta
     */
    private record Pair(Double a, Double b) { }
}
