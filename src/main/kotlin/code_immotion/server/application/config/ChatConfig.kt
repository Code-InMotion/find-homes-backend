package code_immotion.server.application.config

import org.springframework.ai.ollama.OllamaChatModel
import org.springframework.ai.ollama.api.OllamaApi
import org.springframework.ai.ollama.api.OllamaModel
import org.springframework.ai.ollama.api.OllamaOptions
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class ChatConfig {
    @Bean
    fun chatClient(): OllamaChatModel {
        val ollamaApi = OllamaApi()
        val ollamaOptions = OllamaOptions.builder()
            .model(OllamaModel.LLAMA3)
            .temperature(0.7)
            .build()

        return OllamaChatModel.builder()
            .ollamaApi(ollamaApi)
            .defaultOptions(ollamaOptions)
            .build()
    }
}