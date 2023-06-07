package com.fastcampus.programming.dmaker.controller;

import com.fastcampus.programming.dmaker.dto.DeveloperDto;
import com.fastcampus.programming.dmaker.service.DMakerService;
import com.fastcampus.programming.dmaker.type.DeveloperLevel;
import com.fastcampus.programming.dmaker.type.DeveloperSkillType;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DMakerController.class) // 해당 컨트롤러만 빈에 올려준다.
class DMakerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DMakerService dMakerService; // DMakerController는 DMakerService를 의존하고 있다.

    protected MediaType contentType =
            new MediaType(MediaType.APPLICATION_JSON.getType(),
                    MediaType.APPLICATION_JSON.getSubtype(),
                    StandardCharsets.UTF_8);

    @Test
    void getAllDevelopers() throws Exception {
        DeveloperDto juniorDeveloperDto = DeveloperDto.builder()
                .developerSkillType(DeveloperSkillType.BACK_END)
                .developerLevel(DeveloperLevel.JUNIOR)
                .memberId("memberId1").build();

        DeveloperDto seniorDeveloperDto = DeveloperDto.builder()
                .developerSkillType(DeveloperSkillType.FRONT_END)
                .developerLevel(DeveloperLevel.SENIOR)
                .memberId("memberId2").build();

        given(dMakerService.getAllEmployedDevelopers())
                .willReturn(Arrays.asList(juniorDeveloperDto, seniorDeveloperDto));

        mockMvc.perform(get("/developers").contentType(contentType))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(
                        jsonPath("$.[0].developerSkillType",
                                CoreMatchers.is(DeveloperSkillType.BACK_END.name()))
                ).andExpect(
                        jsonPath("$.[0].developerLevel",
                                CoreMatchers.is(DeveloperLevel.JUNIOR.name()))
                ).andExpect(
                        jsonPath("$.[1].developerSkillType",
                                CoreMatchers.is(DeveloperSkillType.FRONT_END.name()))
                ).andExpect(
                        jsonPath("$.[1].developerLevel",
                                CoreMatchers.is(DeveloperLevel.SENIOR.name()))
                );
    }
}