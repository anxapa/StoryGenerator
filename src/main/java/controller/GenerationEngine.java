package controller;

/**
 * Main text generation engine that uses different strategies.
 * Demonstrates Composition and Strategy Pattern.
 */
public class GenerationEngine {
    private PromptStrategy strategy;

    public GenerationEngine(PromptStrategy strategy) {
        this.strategy = strategy;
    }

    public void setStrategy(PromptStrategy strategy) {
        this.strategy = strategy;
    }

    /**
     * Sets the quality of the generated material.
     * @param quality - from 1 - 3, with the higher being more complex
     */
    public void setQuality(int quality) {
        this.strategy.setQuality(quality);
    }

    public String getResponse(String prompt) {
        System.out.println("Using strategy: " + strategy.getStrategyName());
        return strategy.prompt(prompt);
    }

    public String construct(String prompt) {
        StringBuilder response = new StringBuilder();
        response.append(getResponse(prompt));
        return response.toString();
    }
}
