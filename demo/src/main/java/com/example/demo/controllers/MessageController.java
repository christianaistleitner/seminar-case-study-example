package com.example.demo.controllers;

import com.example.demo.database.MessageRepository;
import com.example.demo.database.models.Message;
import com.example.demo.services.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import javax.annotation.PreDestroy;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Executors;

import static java.time.Duration.ofSeconds;

@RestController()
@RequestMapping(value = {"messages"})
@CrossOrigin(origins = {"http://localhost:4200"})
public class MessageController {

    private final MessageService messageService;

    private final Scheduler jdbcScheduler = Schedulers.fromExecutor(Executors.newFixedThreadPool(10));

    /*
    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");

    ConnectableFlux<String> msgs = Flux.create(fluxSink -> {
                while(true) {
                    fluxSink.next(LocalTime.now().format(dtf));
                }
            })
            .sample(ofSeconds(2))
            .map(it -> "[" + it + "] " + "Hello World!")
            .log()
            .publish();


     */

    public MessageController(@Autowired MessageService messageService) {
        this.messageService = messageService;
    }

    @PreDestroy
    public void onDestroy() throws Exception {
        // msgs.connect().dispose();
    }

    @PostMapping
    public Mono<Boolean> send(@RequestBody String text, @RequestParam String username) {
        return messageService.send(text);
        /*
        var now = OffsetDateTime.now();
        return Mono.create(
                sink -> {
                    var message = new Message();
                    message.setText(text);
                    message.setUsername(username);
                    message.setDateSent(now);
                    messageRepository.save(message);
                    sink.success();
                }
        )
                .subscribeOn(jdbcScheduler)
                .publishOn(Schedulers.parallel()).log();
         */
    }

    @GetMapping(produces = {MediaType.TEXT_EVENT_STREAM_VALUE})
    public Flux<String> subscribe() {
        return messageService.subscribe();
    }
}
