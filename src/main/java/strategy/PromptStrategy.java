package strategy;

public interface PromptStrategy {
    String prompt(String prompt, int wordCount);
    String getStrategyName();
    void setComplexity(int complexity);
    int getComplexity();
    int getMinWordCount();
    int getMaxWordCount();
}
