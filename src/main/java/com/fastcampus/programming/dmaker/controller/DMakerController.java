package com.fastcampus.programming.dmaker.controller;

import com.fastcampus.programming.dmaker.dto.CreateDeveloper;
import com.fastcampus.programming.dmaker.dto.DeveloperDetailDto;
import com.fastcampus.programming.dmaker.dto.DeveloperDto;
import com.fastcampus.programming.dmaker.dto.EditDeveloper;
import com.fastcampus.programming.dmaker.service.DMakerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController // 스프링 프레임워크의 어노테이션, 해당 클래스를 RestController 타입의 Bean으로 등록. Controller에 ResponseBody를 더해준다.
@RequiredArgsConstructor
public class DMakerController {

    private final DMakerService dMakerService;

    // Controller : 사용자의 입력을 최초로 받아들이는 위치.
    @GetMapping("/developers") // /developers 로 요청이 오는 경우
    public List<DeveloperDto> getAllDevelopers() {
        // 직접 Entity를 그대로 쓰지 않는 이유는
        // 불필요한 정보가 나갈 수도 있고,
        // 정보에 접근할 때 정보가 충분하지 않은 경우 오류가 발생할 수 있기 때문.
        // 그래서 Dto를 통해서 응답을 내려주는 데이터와 Entity를 분리해주는 게 좋다
        log.info("GET /developers HTTP/1.1");

//        return Arrays.asList("snow", "Elsa", "Olaf");
        return dMakerService.getAllDevelopers();
    }

    @GetMapping("/developer/{memberId}")
    public DeveloperDetailDto getAllDeveloperDetail(
            @PathVariable String memberId // 경로의 {}안에 표시할 변수값
    ) {
        log.info("GET /developers HTTP/1.1");

        return dMakerService.getDeveloperDetail(memberId);
    }

    @PostMapping("/create-developer")
    public CreateDeveloper.Response createDevelopers(
            // 앞의 자바 빈 발리데이션이 동작하려면 Valid가 있어야 한다.
            @Valid @RequestBody CreateDeveloper.Request request
            ) {
        log.info("Request : {}", request);

        return dMakerService.createDeveloper(request);
    }

    @PutMapping("/developer/{memberId}")
    public DeveloperDetailDto editDeveloper(
            @PathVariable String memberId,
            @Valid @RequestBody EditDeveloper.Request request
    ) {
        log.info("GET /developers HTTP/1.1");

        return dMakerService.editDeveloper(memberId, request);
    }
}
