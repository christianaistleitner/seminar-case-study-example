package com.example.demo.controllers;

import com.example.demo.services.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController()
@RequestMapping(value = {"messages"})
@CrossOrigin(origins = {"http://localhost:4200"})
public class MessageController {

    private final MessageService messageService;

    public MessageController(@Autowired MessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping
    public Mono<Boolean> send(@RequestBody String text, @RequestParam String username) {
        return messageService.send(text);
    }

    @GetMapping(produces = {MediaType.TEXT_EVENT_STREAM_VALUE})
    public Flux<String> subscribe() {
        return messageService.subscribe();
    }
}
