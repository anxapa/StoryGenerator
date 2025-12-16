package strategy;

public class ScreenplayStrategy implements PromptStrategy{
    private int complexity;
    private final int MIN_WORD_COUNT = 500;
    private final int MAX_WORD_COUNT = 3000;

    @Override
    public String prompt(String prompt, int wordCount) {
        switch (complexity) {
            case 1:
                return String.format("Create a simple screenplay story with basic, everyday vocabulary with around" +
                        " %d words with the prompt: \"%s\".", wordCount, prompt);
            case 2:
                return String.format("Create a complex screenplay story with higher-frequency, academic vocabulary" +
                        " with around %d words with the prompt: \"%s\".", wordCount, prompt);
            case 3:
                return String.format("Create a complex narrative story with some low-frequency, " +
                        "context-specific vocabulary of around %d words with the prompt: \"%s\".", wordCount, prompt);
            default:
                System.err.println("Quality invalid: " + complexity);
        }

        return "";
    }

    @Override
    public String getStrategyName() {
        return "SCREENPLAY";
    }

    @Override
    public void setComplexity(int complexity) {
        this.complexity = complexity;
    }

    @Override
    public int getComplexity() {
        return this.complexity;
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
