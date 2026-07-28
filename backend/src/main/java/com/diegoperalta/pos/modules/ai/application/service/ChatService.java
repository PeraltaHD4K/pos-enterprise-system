package com.diegoperalta.pos.modules.ai.application.service;

import com.diegoperalta.pos.modules.ai.application.port.in.ChatUseCase;
import com.diegoperalta.pos.modules.ai.application.port.out.AiChatPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatService implements ChatUseCase {

    private final AiChatPort aiChatPort;

    @Override
    public String chat(String message) {
        // Here we could add logic to save conversations to the database in the future
        return aiChatPort.sendMessage(message);
    }
}
