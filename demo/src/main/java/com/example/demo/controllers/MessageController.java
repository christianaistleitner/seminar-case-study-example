package com.example.demo.controllers;

import com.example.demo.models.Ack;
import com.example.demo.models.Message;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.time.format.DateTimeFormatter;
import java.util.List;

import static java.time.Duration.ofSeconds;

@RestController()
@RequestMapping(value = {"messages"})
@CrossOrigin(origins = {"http://localhost:4200"})
public class MessageController {

    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");

    private final Sinks.Many<Message> messageSink = Sinks.many().multicast().directBestEffort();

    private final Sinks.Many<Ack> ackSink = Sinks.many().multicast().directBestEffort();

    public MessageController() {
    }

    @PostMapping
    public Mono<List<String>> send(@RequestBody String text, @RequestParam String username) {
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
                .map(it -> it.getUsername())
                .collectList();
    }

    @PostMapping(path = "ack")
    public void ack(@RequestBody String messageId, @RequestParam String username) {
        Ack ack = new Ack(username, messageId);
        ackSink.tryEmitNext(ack);
    }

    @GetMapping(produces = {MediaType.TEXT_EVENT_STREAM_VALUE})
    public Flux<String> subscribe() {
        return messageSink.asFlux()
                .sample(ofSeconds(1))
                .map(it -> "[" + it.getSentTimestamp().format(dtf) + "] " + it.getText());
    }
}
