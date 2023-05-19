package com.fastcampus.programming.dmaker.controller;

import com.fastcampus.programming.dmaker.dto.CreateDeveloper;
import com.fastcampus.programming.dmaker.service.DMakerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@Slf4j
@RestController // 스프링 프레임워크의 어노테이션, 해당 클래스를 RestController 타입의 Bean으로 등록. Controller에 ResponseBody를 더해준다.
@RequiredArgsConstructor
public class DMakerController {

    private final DMakerService dMakerService;

    // Controller : 사용자의 입력을 최초로 받아들이는 위치.
    @GetMapping("/developers") // /developers 로 요청이 오는 경우
    public List<String> getAllDevelopers() {
        log.info("GET /developers HTTP/1.1");

        return Arrays.asList("snow", "Elsa", "Olaf");
    }

    @PostMapping("/create-developer")
    public CreateDeveloper.Response createDevelopers(
            // 앞의 자바 빈 발리데이션이 동작하려면 Valid가 있어야 한다.
            @Valid @RequestBody CreateDeveloper.Request request
            ) {
        log.info("Request : {}", request);

        return dMakerService.createDeveloper(request);
    }
}
