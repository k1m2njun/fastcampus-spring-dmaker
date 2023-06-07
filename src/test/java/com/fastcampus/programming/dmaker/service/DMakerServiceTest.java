package com.fastcampus.programming.dmaker.service;

import com.fastcampus.programming.dmaker.code.StatusCode;
import com.fastcampus.programming.dmaker.dto.CreateDeveloper;
import com.fastcampus.programming.dmaker.dto.DeveloperDetailDto;
import com.fastcampus.programming.dmaker.entity.Developer;
import com.fastcampus.programming.dmaker.exception.DMakerErrorCode;
import com.fastcampus.programming.dmaker.exception.DMakerException;
import com.fastcampus.programming.dmaker.repository.DeveloperRepository;
import com.fastcampus.programming.dmaker.repository.RetiredDeveloperRepository;
import com.fastcampus.programming.dmaker.type.DeveloperLevel;
import com.fastcampus.programming.dmaker.type.DeveloperSkillType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@SpringBootTest // 통합 테스트
class DMakerServiceTest {
    @Mock // 테스트하려는 클래스(여기서 DMakerService)의 디펜던시를 Mock으로 만든다.
    private DeveloperRepository developerRepository;
    @Mock // 그럼 이 클래스를 생성할 때 자동으로 이 Mock을 넣어준다.
    private RetiredDeveloperRepository retiredDeveloperRepository;

//    @Autowired
    @InjectMocks // 목업 데이터를 더해준다.
    private DMakerService dMakerService;

    private final Developer defaultDeveloper = Developer.builder()
            .developerLevel(DeveloperLevel.SENIOR)
            .developerSkillType(DeveloperSkillType.FRONT_END)
            .experienceYears(12)
            .statusCode(StatusCode.EMPLOYED)
            .name("name")
            .age(32)
            .build();

    private final CreateDeveloper.Request defaultCreateRequest =
            CreateDeveloper.Request.builder()
                    .developerLevel(DeveloperLevel.SENIOR)
                    .developerSkillType(DeveloperSkillType.FRONT_END)
                    .experienceYears(12)
                    .memberId("memberId")
                    .name("name")
                    .age(32)
                    .build();

    @Test
    public void testSomething() {
//        dMakerService.createDeveloper(CreateDeveloper.Request.builder()
//                        .developerLevel(DeveloperLevel.SENIOR)
//                        .developerSkillType(DeveloperSkillType.FRONT_END)
//                        .experienceYears(12)
//                        .memberId("memberId")
//                        .name("name")
//                        .age(32)
//                        .build());
//        List<DeveloperDto> allEmployedDevelopers = dMakerService.getAllEmployedDevelopers();
//        System.out.println("======================");
//        System.out.println(allEmployedDevelopers);
//        System.out.println("======================");

        // Mock 의 동작을 정의해야 한다.
        // anyString으로 memberId에 아무 문자열을 넣어주면 아래와 같은 데이터를 넣도록 정의한다.
        // given
        given(developerRepository.findByMemberId(anyString()))
                .willReturn(Optional.of(defaultDeveloper));

        // when
        DeveloperDetailDto developerDetail = dMakerService.getDeveloperDetail("memberId");

        // then
        assertEquals(DeveloperLevel.SENIOR, developerDetail.getDeveloperLevel());
        assertEquals(DeveloperSkillType.FRONT_END, developerDetail.getDeveloperSkillType());
        assertEquals(12, developerDetail.getExperienceYears());
        assertEquals(StatusCode.EMPLOYED, developerDetail.getStatusCode());
        assertEquals("name", developerDetail.getName());
        assertEquals(32, developerDetail.getAge());
    }

//    @Test
//    public void testSomething() {
//        String result = "hello" + " world!";
//
//        assertEquals("hello world!", result); // junit에서 제공되는 메서드. 예상되는 값과 테스트 소스를 넣는다.
//    }

    @Test
    void createDeveloperTest_success() {
        //given
//        CreateDeveloper.Request request = CreateDeveloper.Request.builder()
//                .developerLevel(DeveloperLevel.SENIOR)
//                .developerSkillType(DeveloperSkillType.FRONT_END)
//                .experienceYears(12)
//                .memberId("memberId")
//                .name("name")
//                .age(32)
//                .build();

        given(developerRepository.findByMemberId(anyString()))
                .willReturn(Optional.empty());

        ArgumentCaptor<Developer> captor =
                ArgumentCaptor.forClass(Developer.class); // DB에 호출되는 데이터가 뭔지 확인하고 싶을 때 등

        //when
        CreateDeveloper.Response developer = dMakerService.createDeveloper(defaultCreateRequest);

        //then
        verify(developerRepository, times(1))
                .save(captor.capture());

        Developer savedDeveloper = captor.getValue();
        assertEquals(DeveloperLevel.SENIOR, savedDeveloper.getDeveloperLevel());
        assertEquals(DeveloperSkillType.FRONT_END, savedDeveloper.getDeveloperSkillType());
        assertEquals(12, savedDeveloper.getExperienceYears());
    }

    @Test
    void createDeveloperTest_failed_with_duplicated() {
        //given
//        CreateDeveloper.Request request = CreateDeveloper.Request.builder()
//                .developerLevel(DeveloperLevel.SENIOR)
//                .developerSkillType(DeveloperSkillType.FRONT_END)
//                .experienceYears(12)
//                .memberId("memberId")
//                .name("name")
//                .age(32)
//                .build();

        given(developerRepository.findByMemberId(anyString()))
                .willReturn(Optional.of(defaultDeveloper));

//        ArgumentCaptor<Developer> captor =
//                ArgumentCaptor.forClass(Developer.class);

        //when
//        CreateDeveloper.Response developer = dMakerService.createDeveloper(defaultCreateRequest);

        //then
//        verify(developerRepository, times(1))
//                .save(captor.capture());
//
//        Developer savedDeveloper = captor.getValue();
//        assertEquals(DeveloperLevel.SENIOR, savedDeveloper.getDeveloperLevel());
//        assertEquals(DeveloperSkillType.FRONT_END, savedDeveloper.getDeveloperSkillType());
//        assertEquals(12, savedDeveloper.getExperienceYears());
        DMakerException dMakerException = assertThrows(DMakerException.class,
                () -> dMakerService.createDeveloper(defaultCreateRequest)
        );

        assertEquals(DMakerErrorCode.DUPLICATED_MEMBER_ID, dMakerException.getDMakerErrorCode());
    }
}