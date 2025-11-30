package controller;

public interface PromptStrategy {
    String prompt(String prompt);
    String getStrategyName();
    void setQuality(int quality);
    int getQuality();
}
