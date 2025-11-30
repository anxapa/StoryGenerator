package controller;

public class NarrativeStrategy implements PromptStrategy{
    private int quality = 1;

    @Override
    public String prompt(String prompt) {
        switch (quality) {
            case 1:
                return String.format("Create a simple 300-500 word narrative story with basic, everyday vocabulary" +
                        " with the prompt: \"%s\".", prompt);
            case 2:
                return String.format("Create a complex 500-800 word narrative story with higher-frequency, academic" +
                        " vocabulary with the prompt: \"%s\".", prompt);
            case 3:
                return String.format("Create a complex 800-1000 word narrative story with some low-frequency, " +
                        "context-specific vocabulary with the prompt: \"%s\".", prompt);
            default:
                System.err.println("Quality invalid: " + quality);
        }

        return "";
    }

    @Override
    public String getStrategyName() {
        return "NARRATIVE";
    }

    @Override
    public void setQuality(int quality) {
        this.quality = quality;
    }

    @Override
    public int getQuality() {
        return quality;
    }
}
