package code_immotion.server.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SwaggerConfig {
    @Bean
    fun openApi(): OpenAPI {
        return OpenAPI()
            .info(Info()
                .version("v0.1")
                .title("구해줘! 홈즈 API 명세서")
                .description("API DOC")
            )
    }
}