package com.example.demo.controllers;

import com.example.demo.services.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController()
@RequestMapping(value = {"messages"})
@CrossOrigin(origins = {"http://localhost:4200"})
public class MessageController {

    private final MessageService messageService;

    public MessageController(@Autowired MessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping
    public Mono<List<String>> send(@RequestBody String text, @RequestParam String username) {
        return messageService.send(text, username).collectList();
    }

    @PostMapping(path = "ack")
    public void ack(@RequestBody String messageId, @RequestParam String username) {
        messageService.ack(messageId, username);
    }

    @GetMapping(produces = {MediaType.TEXT_EVENT_STREAM_VALUE})
    public Flux<String> subscribe() {
        return messageService.subscribe();
    }
}
