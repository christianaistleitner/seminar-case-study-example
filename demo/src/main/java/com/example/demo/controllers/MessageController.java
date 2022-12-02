package com.example.demo.controllers;

import com.example.demo.models.AckDto;
import com.example.demo.models.MessageDto;
import com.example.demo.models.PacketDto;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import reactor.core.scheduler.Schedulers;

import static java.time.Duration.ofSeconds;

@RestController()
@RequestMapping(value = {"messages"})
@CrossOrigin(origins = {"http://localhost:4200"})
public class MessageController {

    private final Sinks.Many<PacketDto> packetSink = Sinks.many().multicast().directBestEffort();

    public MessageController() {
        // TODO: Using Schedulers.enableMetrics(); we could export Micrometer metrics
        // and take a look at thread pool utilization, etc.
    }

    @PostMapping(path = "send", produces = {MediaType.TEXT_PLAIN_VALUE})
    public String send(@RequestBody String text, @RequestParam String username) {
        MessageDto message = new MessageDto(text, username);
        packetSink.asFlux()
                .doOnSubscribe(ignore -> packetSink.tryEmitNext(message))
                .filter(it -> it.getType() == PacketDto.Type.ACK)
                .cast(AckDto.class)
                .any(it -> it.getMessageId().equals(message.getId()))
                .timeout(ofSeconds(5))
                .retry(3)
                .subscribeOn(Schedulers.parallel())
                .subscribe();
        return message.getId();
    }

    @PostMapping(path = "ack", produces = {MediaType.TEXT_PLAIN_VALUE})
    public void ack(@RequestBody String messageId, @RequestParam String username) {
        packetSink.tryEmitNext(new AckDto(username, messageId));
    }

    @GetMapping(path = "stream", produces = {MediaType.TEXT_EVENT_STREAM_VALUE})
    public Flux<PacketDto> subscribe(@RequestParam String username) {
        return packetSink.asFlux().filter(it -> it.getRecipient().equals(username));
    }
}
