package com.CreateAPI.WebApplication.Configure;

import com.google.cloud.pubsub.v1.Publisher;
import com.google.pubsub.v1.TopicName;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class PubSubConfig {
    @Bean
    public Publisher pubsubPublisher(
            @Value("${projectId}") String projectId,
            @Value("${topicId}") String topicId)
            throws IOException {
        TopicName topicName = TopicName.of(projectId, topicId);
        return Publisher.newBuilder(topicName).build();
    }
}
