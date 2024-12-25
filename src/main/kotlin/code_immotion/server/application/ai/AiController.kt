package code_immotion.server.application.ai

import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.ai.chat.messages.UserMessage
import org.springframework.ai.chat.model.ChatResponse
import org.springframework.ai.chat.prompt.Prompt
import org.springframework.ai.ollama.OllamaChatModel
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux


@Tag(name = "LLAMA AI API")
@RestController
@RequestMapping("ai")
class AiController(private val ollamaChatModel: OllamaChatModel) {
    @GetMapping("generate")
    fun generate(@RequestParam(value = "message", defaultValue = "Tell me a joke") message: String?): Map<String, String> {
        return java.util.Map.of("generation", ollamaChatModel.call(message))
    }

    @GetMapping
    fun generateStream(@RequestParam(value = "message", defaultValue = "Tell me a joke") message: String?): Flux<ChatResponse>? {
        val prompt = Prompt(UserMessage(message))
        return this.ollamaChatModel.stream(prompt)
    }
}