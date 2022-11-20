package com.example.demo.services;

import com.example.demo.models.Ack;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import static java.time.Duration.ofSeconds;

@Service
public class MessageService {

    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");

    private final Sinks.Many<String> messageSink = Sinks.many().multicast().directBestEffort();

    private final Sinks.Many<Ack> ackSink = Sinks.many().multicast().directBestEffort();

    public MessageService() {
    }

    public Flux<String> subscribe() {
        return messageSink.asFlux()
                .sample(ofSeconds(1))
                .map(it -> "[" + LocalTime.now().format(dtf) + "] " + it);
    }

    public Flux<String> send(String text, String username) {
        messageSink.tryEmitNext(text);
        return ackSink.asFlux()
                //.filter(it -> it.messageId.equals(""))
                .map(it -> it.username)
                .timeout(ofSeconds(10), Mono.empty());
    }

    public void ack(String id, String username) {
        Ack ack = new Ack();
        ack.username = username;
        ack.messageId = id;
        ackSink.tryEmitNext(ack);
    }
}
