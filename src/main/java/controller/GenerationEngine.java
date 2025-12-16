package controller;

import strategy.PromptStrategy;

/**
 * Main text generation engine that uses different strategies.
 * Demonstrates Composition and Strategy Pattern.
 */
public class GenerationEngine {
    private int complexity = 1;
    private PromptStrategy strategy;

    public GenerationEngine(PromptStrategy strategy) {
        setStrategy(strategy);
    }

    /**
     * Sets the strategy (story type) used for the generation.
     * @param strategy - story type
     */
    public void setStrategy(PromptStrategy strategy) {
        this.strategy = strategy;
        this.strategy.setComplexity(complexity);
    }

    /**
     * Sets the complexity of the generated material.
     * @param complexity - from 1 - 3, with the higher being more complex
     */
    public void setComplexity(int complexity) {
        this.complexity = complexity;
        this.strategy.setComplexity(complexity);
    }

    /**
     * Returns the minimum word count given the strategy.
     * @return minimum word count
     */
    public int getMinWordCount() {
        return strategy.getMinWordCount();
    }

    /**
     * Returns the maximum word count given the strategy.
     * @return maximum word count
     */
    public int getMaxWordCount() {
        return strategy.getMaxWordCount();
    }

    public String getResponse(String prompt, int wordCount) {
        System.out.println("Using strategy: " + strategy.getStrategyName());
        return strategy.prompt(prompt, wordCount);
    }

    public String construct(String prompt, int wordCount) {
        StringBuilder response = new StringBuilder();
        response.append(getResponse(prompt, wordCount));
        return response.toString();
    }
}
