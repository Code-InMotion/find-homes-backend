package code_immotion.server

import org.apache.poi.util.IOUtils
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching
import org.springframework.scheduling.annotation.EnableAsync

@SpringBootApplication
@EnableAsync
@EnableCaching
class ServerApplication

fun main(args: Array<String>) {
    IOUtils.setByteArrayMaxOverride(200_000_000)
    runApplication<ServerApplication>(*args)
}
