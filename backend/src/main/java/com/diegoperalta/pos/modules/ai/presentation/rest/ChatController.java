package com.diegoperalta.pos.modules.ai.presentation.rest;

import com.diegoperalta.pos.modules.ai.application.port.in.ChatUseCase;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
public class ChatController {

    private final ChatUseCase chatUseCase;

    @PostMapping("/chat")
    public ChatResponse chat(@RequestBody ChatRequest request) {
        String response = chatUseCase.chat(request.getMessage());
        return new ChatResponse(response);
    }
}

@Data
class ChatRequest {
    private String message;
}

@Data
class ChatResponse {
    private String response;
    
    public ChatResponse(String response) {
        this.response = response;
    }
}
