package ca.concordia.ginacody.comp6231.demsha.client.shell;

/**
 *
 */
public enum PromptColor {
    Black(0), Red(1), Green(2), Yellow(3), Blue(4), Magenta(5), Cyan(6), White(7);

    /**
     *
     */
    private final int value;

    /**
     * This method sets the color for prompt
     */
    PromptColor(int value) {
        this.value = value;
    }

    /**
     * @return Jline style
     */
    public int toJlineAttributedStyle() {
        return this.value;
    }
}