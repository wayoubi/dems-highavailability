package ca.concordia.ginacody.comp6231.demsha.client.shell;

import org.jline.terminal.Terminal;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;
import org.springframework.beans.factory.annotation.Value;

/**
 *
 */
public class ShellHelper {

    /**
     *
     */
    @Value("${shell.out.info}")
    public String infoColor;

    /**
     *
     */
    @Value("${shell.out.success}")
    public String successColor;

    /**
     *
     */
    @Value("${shell.out.warning}")
    public String warningColor;

    /**
     *
     */
    @Value("${shell.out.error}")

    /**
     *
     */
    public String errorColor;

    /**
     *
     */
    public Terminal terminal;

    /**
     *
     * @param terminal
     */
    public ShellHelper(Terminal terminal) {
        this.terminal = terminal;
    }

    /**
     *
     * @param message
     * @param color
     * @return
     */
    public String getColored(String message, PromptColor color) {
        return (new AttributedStringBuilder())
                .append(message, AttributedStyle.DEFAULT.foreground(color.toJlineAttributedStyle())).toAnsi();
    }

    /**
     *
     * @param message
     * @return
     */
    public String getInfoMessage(String message) {
        return getColored(message, PromptColor.valueOf(infoColor));
    }

    /**
     *
     * @param message
     * @return
     */
    public String getSuccessMessage(String message) {
        return getColored(message, PromptColor.valueOf(successColor));
    }

    /**
     *
     * @param message
     * @return
     */
    public String getWarningMessage(String message) {
        return getColored(message, PromptColor.valueOf(warningColor));
    }

    /**
     *
     * @param message
     * @return
     */
    public String getErrorMessage(String message) {
        return getColored(message, PromptColor.valueOf(errorColor));
    }

    /**
     * Print message to the console in the default color.
     *
     * @param message message to print
     */
    public void print(String message) {
        print(message, null);
    }

    /**
     * Print message to the console in the success color.
     *
     * @param message message to print
     */
    public void printSuccess(String message) {
        print(message, PromptColor.valueOf(successColor));
    }

    /**
     * Print message to the console in the info color.
     *
     * @param message message to print
     */
    public void printInfo(String message) {
        print(message, PromptColor.valueOf(infoColor));
    }

    /**
     * Print message to the console in the warning color.
     *
     * @param message message to print
     */
    public void printWarning(String message) {
        print(message, PromptColor.valueOf(warningColor));
    }

    /**
     * Print message to the console in the error color.
     *
     * @param message message to print
     */
    public void printError(String message) {
        print(message, PromptColor.valueOf(errorColor));
    }

    /**
     * Generic Print to the console method.
     *
     * @param message message to print
     * @param color   (optional) prompt color
     */
    public void print(String message, PromptColor color) {
        String toPrint = message;
        if (color != null) {
            toPrint = getColored(message, color);
        }
        terminal.writer().println(toPrint);
        terminal.flush();
    }
}