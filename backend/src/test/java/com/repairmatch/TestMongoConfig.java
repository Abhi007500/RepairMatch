package com.repairmatch;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import de.bwaldvogel.mongo.MongoServer;
import de.bwaldvogel.mongo.backend.memory.MemoryBackend;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class TestMongoConfig {

    private static final MongoServer server;
    private static final String connectionString;

    static {
        server = new MongoServer(new MemoryBackend());
        connectionString = server.bindAndGetConnectionString();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                server.shutdown();
            } catch (Exception ignored) {
            }
        }));
    }

    @Bean
    @Primary
    public MongoClient mongoClient() {
        return MongoClients.create(connectionString);
    }
}
