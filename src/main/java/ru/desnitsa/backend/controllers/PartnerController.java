package ru.desnitsa.backend.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.desnitsa.backend.dto.PartnerDto;
import ru.desnitsa.backend.entities.Partner;
import ru.desnitsa.backend.services.PartnerService;

@RestController
@RequestMapping("/partners")
public class PartnerController {
    private final PartnerService partnerService;

    public PartnerController(PartnerService partnerService) {
        this.partnerService = partnerService;
    }

    @GetMapping
    @Operation(summary = "Получить всех партнеров")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Получить всех партнеров"),
            @ApiResponse(
                    responseCode = "500",
                    description = "Внутренняя ошибка сервера",
                    content = {@Content(schema = @Schema(implementation = String.class))})
    })
    public Iterable<Partner> getAllPartners() {
        return partnerService.getAllPartners();
    }

    @GetMapping("{id}")
    @Operation(summary = "Получить партнера по id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Получить партнера по id"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Неправильный запрос",
                    content = {@Content(schema = @Schema(implementation = String.class))}),
            @ApiResponse(
                    responseCode = "404",
                    description = "Партнер с таким id не найден",
                    content = {@Content(schema = @Schema(implementation = String.class))}),
            @ApiResponse(
                    responseCode = "500",
                    description = "Внутренняя ошибка сервера",
                    content = {@Content(schema = @Schema(implementation = String.class))})
    })
    public Partner getPartner(@PathVariable Long id) {
        return partnerService.getPartner(id);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(code = HttpStatus.CREATED)
    @Operation(summary = "Добавить партнера")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Добавить партнера"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Неправильный запрос",
                    content = {@Content(schema = @Schema(implementation = String.class))}),
            @ApiResponse(
                    responseCode = "403",
                    description = "Запрещен доступ к контенту",
                    content = {@Content(schema = @Schema(implementation = String.class))}),
            @ApiResponse(
                    responseCode = "415",
                    description = "Тип загружаемого контента не соответствует изображению",
                    content = {@Content(schema = @Schema(implementation = String.class))}),
            @ApiResponse(
                    responseCode = "500",
                    description = "Внутренняя ошибка сервера",
                    content = {@Content(schema = @Schema(implementation = String.class))})
    })
    public Partner savePartner(@ModelAttribute PartnerDto partnerDto, BindingResult ignoredBindingResult) {
        return partnerService.savePartner(partnerDto);
    }

    @PutMapping(value = "{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Обновить партнера по id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Обновить партнера по id"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Неправильный запрос",
                    content = {@Content(schema = @Schema(implementation = String.class))}),
            @ApiResponse(
                    responseCode = "403",
                    description = "Запрещен доступ к контенту",
                    content = {@Content(schema = @Schema(implementation = String.class))}),
            @ApiResponse(
                    responseCode = "404",
                    description = "Партнер с таким id не найден",
                    content = {@Content(schema = @Schema(implementation = String.class))}),
            @ApiResponse(
                    responseCode = "415",
                    description = "Тип загружаемого контента не соответствует изображению",
                    content = {@Content(schema = @Schema(implementation = String.class))}),
            @ApiResponse(
                    responseCode = "500",
                    description = "Внутренняя ошибка сервера",
                    content = {@Content(schema = @Schema(implementation = String.class))})
    })
    public Partner updatePartner(
            @PathVariable Long id,
            @ModelAttribute PartnerDto partnerDto,
            BindingResult ignoredBindingResult
    ) {
        return partnerService.updatePartner(id, partnerDto);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Удалить партнера по id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Удален партнер по id"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Неправильный запрос",
                    content = {@Content(schema = @Schema(implementation = String.class))}),
            @ApiResponse(
                    responseCode = "403",
                    description = "Запрещен доступ к контенту",
                    content = {@Content(schema = @Schema(implementation = String.class))}),
            @ApiResponse(
                    responseCode = "500",
                    description = "Внутренняя ошибка сервера",
                    content = {@Content(schema = @Schema(implementation = String.class))})
    })
    public void deletePartner(@PathVariable Long id) {
        partnerService.deletePartner(id);
    }
}
