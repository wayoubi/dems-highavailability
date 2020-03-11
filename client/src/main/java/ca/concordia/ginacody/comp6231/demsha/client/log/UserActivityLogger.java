package ca.concordia.ginacody.comp6231.demsha.client.log;


import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class UserActivityLogger {

    private static Logger LOGGER = LoggerFactory.getLogger(UserActivityLogger.class);

    /**
     *
     */
    private String username;

    /**
     *
     */
    private PrintWriter printWriter;

    /**
     *
     */
    public UserActivityLogger(String username) {
        this.setUsername(username);
        try{
            FileWriter fileWriter = new FileWriter(String.format("log/%s.log", username), true);
            //FileWriter fileWriter = new FileWriter(String.format("%s.log", username), true);
             printWriter = new PrintWriter(new BufferedWriter(fileWriter),true);
        } catch (IOException ex) {
            LOGGER.error(ex.getMessage());
        }
    }

    /**
     *
     * @param message
     */
    public void log(String message) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        this.printWriter.printf(String.format("%s %s %s%s", dateFormat.format(new Date()), Thread.currentThread().getName(), message, System.lineSeparator()));
    }

    /**
     *
     */
    public void release() {
        if(this.printWriter != null) {
            this.printWriter.close();
        }
    }

    /**
     *
     * @return
     */
    public String getUsername() {
        return username;
    }

    /**
     * s
     * @param username
     */
    public void setUsername(String username) {
        this.username = username;
    }
}
