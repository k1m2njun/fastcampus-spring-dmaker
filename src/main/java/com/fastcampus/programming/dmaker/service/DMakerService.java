package com.fastcampus.programming.dmaker.service;

import com.fastcampus.programming.dmaker.entity.Developer;
import com.fastcampus.programming.dmaker.repository.DeveloperRepository;
import com.fastcampus.programming.dmaker.type.DeveloperLevel;
import com.fastcampus.programming.dmaker.type.DeveloperSkillType;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DMakerService {
    private final DeveloperRepository developerRepository;
    // 기존에는 생성자에 해당 필드 타입의 데이터를 받아 주입했다면
    // RequiredArgsConstructor를 사용하면 자동으로 인젝션 해준다.
    // 이때, repository가 많을 때 필요한 것을 final로 선언하면 이것은 꼭 있어야되는 필드이므로
    // RequiredArgs 생성자가 자동으로 만들어진다.

    @Transactional
    public void createDeveloper() {

        Developer developer = Developer.builder()
                .developerLevel(DeveloperLevel.JUNIOR)
                .developerSkillType(DeveloperSkillType.FRONT_END)
                .experienceYears(2)
                .name("Olaf")
                .age(5)
                .build();

        developerRepository.save(developer); // Entity를 Repository를 통해 DB에 영속화

    }
}
