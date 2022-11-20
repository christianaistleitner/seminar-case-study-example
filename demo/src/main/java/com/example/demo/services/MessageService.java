package com.example.demo.services;

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

    private Sinks.Many<String> messageSink = Sinks.many().multicast().directBestEffort();

    public MessageService() {
    }

    public Flux<String> subscribe() {
        return messageSink.asFlux()
                .sample(ofSeconds(1))
                .map(it -> "[" + LocalTime.now().format(dtf) + "] " + it);
    }

    public Mono<Boolean> send(String text) {
        return Mono.create(monoSink -> {
            messageSink.tryEmitNext(text);
            monoSink.success(true);
        });
    }
}
