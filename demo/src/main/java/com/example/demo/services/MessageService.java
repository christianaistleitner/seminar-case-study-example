package com.example.demo.services;

import com.example.demo.models.Ack;
import com.example.demo.models.Message;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.time.format.DateTimeFormatter;

import static java.time.Duration.ofSeconds;

@Service
public class MessageService {

    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");

    private final Sinks.Many<Message> messageSink = Sinks.many().multicast().directBestEffort();

    private final Sinks.Many<Ack> ackSink = Sinks.many().multicast().directBestEffort();

    public MessageService() {
    }

    public Flux<String> subscribe() {
        return messageSink.asFlux()
                .sample(ofSeconds(1))
                .map(it -> "[" + it.getSentTimestamp().format(dtf) + "] " + it.getText());
    }

    public Flux<String> send(String text, String username) {
        Message message = new Message(text, username);
        return Mono.just(message)
                .flatMapMany(it -> {
                    messageSink.tryEmitNext(it);
                    return ackSink.asFlux();
                })
                .filter(it -> it.getMessageId().equals(message.getId()))
                .timeout(ofSeconds(5))
                .retry(3)
                .onErrorComplete()
                .map(it -> it.getUsername());
    }

    public void ack(String id, String username) {
        Ack ack = new Ack(username, id);
        ackSink.tryEmitNext(ack);
    }
}
