package ca.concordia.ginacody.comp6231.demsha.client;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class ClientRunner {

    public static void main(String[] args) {
        SpringApplicationBuilder builder = new SpringApplicationBuilder(ClientRunner.class);
        builder.headless(false);
        builder.run();
    }
}
