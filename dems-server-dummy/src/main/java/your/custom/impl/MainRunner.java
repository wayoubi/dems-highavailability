package your.custom.impl;

public class MainRunner {

    public static void main(String args[]) {
        Thread thread = new Thread(new RMIInitializer());
        thread.start();
    }
}
