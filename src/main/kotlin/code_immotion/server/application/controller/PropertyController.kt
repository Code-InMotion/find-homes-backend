package code_immotion.server.application.controller

import io.github.oshai.kotlinlogging.KotlinLogging
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.springdoc.core.annotations.ParameterObject
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

private val logger = KotlinLogging.logger { }

//@Tag(name = "PROPERTY API")
//@RestController
//@RequestMapping("property")
//class PropertyController(private val propertyFacade: PropertyFacade) {
//    @GetMapping
//    @Operation(summary = "사용자 조건에 동내 추천")
//    fun findRegionWithCondition(@ParameterObject condition: PropertyCondition.Recommend) =
//        propertyFacade.findRegionWithCondition(condition)
//
//    @GetMapping("list")
//    @Operation(summary = "추천 지역 내 매물 목록 조회")
//    fun findRegionProperties(@ParameterObject condition: PropertyCondition.Address) =
//        propertyFacade.findRegionProperties(condition)
//
//    @GetMapping("detail")
//    @Operation(summary = "매물 상세 조회")
//    fun findProperty(
//        @Parameter propertyId: String,
//        @Parameter(description = "목적지", example = "성복동 819") destination: String
//    ) = propertyFacade.findProperty(propertyId, destination)
//
//    @GetMapping("size")
//    @Operation(summary = "전체 매물 수 확인")
//    fun findTotalSize() = propertyFacade.findTotalSize()
//
//    @DeleteMapping
//    @Operation(summary = "전체 매물 삭제")
//    fun deleteAll() = propertyFacade.deleteAll()
//}