package com.example.demo.services;

import com.example.demo.database.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import static java.time.Duration.ofSeconds;

@Service
public class MessageService {

    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");

    private final Scheduler radioScheduler = Schedulers.fromExecutor(Executors.newFixedThreadPool(1));

    private final MessageRepository messageRepository;

    private Consumer<String> messageSink;

    private final ConnectableFlux<String> messageRadio;

    public MessageService(@Autowired MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
        this.messageRadio = Flux.create(fluxSink -> messageSink = fluxSink::next)
                .sample(ofSeconds(1))
                .subscribeOn(radioScheduler)
                .map(it -> "[" + LocalTime.now().format(dtf) + "] " + it)
                .log()
                .publish();
    }

    public Flux<String> subscribe() {
        return messageRadio.autoConnect();
    }

    public Mono<Boolean> send(String text) {
        return Mono.create(monoSink -> {
            messageSink.accept(text);
            monoSink.success(true);
        });
    }
}
