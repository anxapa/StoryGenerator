package strategy;

public class NarrativeStrategy implements PromptStrategy{
    private int complexity = 1;
    private final int MIN_WORD_COUNT = 500;
    private final int MAX_WORD_COUNT = 3000;

    @Override
    public String prompt(String prompt, int wordCount) {
        switch (complexity) {
            case 1:
                return String.format("Create a simple narrative story with basic, everyday vocabulary " +
                        "with around %d words with the prompt: \"%s\".", wordCount, prompt);
            case 2:
                return String.format("Create a narrative story with academic vocabulary" +
                        " with around %d words with the prompt: \"%s\".", wordCount, prompt);
            case 3:
                return String.format("Create a complex narrative story with many academic, " +
                        "context-specific vocabulary and many different story elements of " +
                        "around %d words with the prompt: \"%s\".", wordCount, prompt);
            default:
                System.err.println("Quality invalid: " + complexity);
        }

        return "";
    }

    @Override
    public String getStrategyName() {
        return "NARRATIVE";
    }

    @Override
    public void setComplexity(int complexity) {
        this.complexity = complexity;
    }

    @Override
    public int getComplexity() {
        return complexity;
    }

    @Override
    public int getMinWordCount() {
        return MIN_WORD_COUNT;
    }

    @Override
    public int getMaxWordCount() {
        return MAX_WORD_COUNT;
    }
}
