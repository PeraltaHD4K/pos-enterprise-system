package com.diegoperalta.pos.modules.ai.infrastructure.adapter.out.gemini;

import com.diegoperalta.pos.modules.ai.application.port.out.AiChatPort;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;

@Component
public class GeminiFreeTierAdapter implements AiChatPort {

    private final ChatClient chatClient;

    public GeminiFreeTierAdapter(ChatClient.Builder chatClientBuilder, com.diegoperalta.pos.modules.ai.application.tools.AiTools aiTools) {
        this.chatClient = chatClientBuilder
                .defaultSystem("Eres el asistente de administración de este Punto de Venta (POS). Tienes acceso a herramientas para consultar información en tiempo real de la base de datos. Responde siempre de manera concisa y clara utilizando la información de las herramientas si es requerida.")
                .defaultTools(aiTools)
                .build();
    }

    @Override
    public String sendMessage(String message) {
        return chatClient.prompt()
                .user(message)
                .call()
                .content();
    }
}
