package com.fastcampus.programming.dmaker.service;

import com.fastcampus.programming.dmaker.code.StatusCode;
import com.fastcampus.programming.dmaker.dto.CreateDeveloper;
import com.fastcampus.programming.dmaker.dto.DeveloperDetailDto;
import com.fastcampus.programming.dmaker.dto.DeveloperDto;
import com.fastcampus.programming.dmaker.dto.EditDeveloper;
import com.fastcampus.programming.dmaker.entity.Developer;
import com.fastcampus.programming.dmaker.entity.RetiredDeveloper;
import com.fastcampus.programming.dmaker.exception.DMakerErrorCode;
import com.fastcampus.programming.dmaker.exception.DMakerException;
import com.fastcampus.programming.dmaker.repository.DeveloperRepository;
import com.fastcampus.programming.dmaker.repository.RetiredDeveloperRepository;
import com.fastcampus.programming.dmaker.type.DeveloperLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DMakerService {
    private final DeveloperRepository developerRepository;
    // 기존에는 생성자에 해당 필드 타입의 데이터를 받아 주입했다면
    // RequiredArgsConstructor를 사용하면 자동으로 인젝션 해준다.
    // 이때, repository가 많을 때 필요한 것을 final로 선언하면 이것은 꼭 있어야되는 필드이므로
    // RequiredArgs 생성자가 자동으로 만들어진다.

    private final RetiredDeveloperRepository retiredDeveloperRepository;

    @Transactional
    public CreateDeveloper.Response createDeveloper(
            CreateDeveloper.Request request
    ) {
        validateCreateDeveloperRequest(request);

        // Entity를 Repository를 통해 DB에 영속화
        return CreateDeveloper.Response.fromEntity(
                developerRepository.save(
                        createDeveloperFromRequest(request)
                )
        );
    }

    private Developer createDeveloperFromRequest(CreateDeveloper.Request request) {
        return Developer.builder()
                .developerLevel(request.getDeveloperLevel())
                .developerSkillType(request.getDeveloperSkillType())
                .experienceYears(request.getExperienceYears())
                .memberId(request.getMemberId())
                .name(request.getName())
                .age(request.getAge())
                .statusCode(StatusCode.EMPLOYED)
                .build();
    }

    private void validateCreateDeveloperRequest(
            @NonNull CreateDeveloper.Request request
    ) {
        // business validation
        request.getDeveloperLevel().validateExperienceYears(
                request.getExperienceYears()
        );

//        Optional<Developer> developer = developerRepository.findByMemberId(request.getMemberId());
//        if(developer.isPresent())
//            throw new DMakerException(DMakerErrorCode.DUPLICATED_MEMBER_ID);
        developerRepository.findByMemberId(request.getMemberId())
                .ifPresent((developer -> {
                    throw new DMakerException(DMakerErrorCode.DUPLICATED_MEMBER_ID);
                }));
    }

    @Transactional(readOnly = true)
    public List<DeveloperDto> getAllEmployedDevelopers() {
        return developerRepository.findDevelopersByStatusCodeEquals(StatusCode.EMPLOYED)
                .stream().map(DeveloperDto::fromEntity)
                .collect(Collectors.toList());
    } // Dto 타입으로 변경

    @Transactional(readOnly = true)
    public DeveloperDetailDto getDeveloperDetail(String memberId) {
        return DeveloperDetailDto.fromEntity(getDeveloperByMemberId(memberId));
//                //findByMemberId 는 Optional인데, 이것은
//                // map 함수를 지원한다.
//                .map(DeveloperDetailDto::fromEntity) // DeveloperEntity -> DeveloperDetailEntity
////                .get()
//                .orElseThrow(() -> new DMakerException(DMakerErrorCode.NO_DEVELOPER));
//                // 값을 못가져오면 예외처리
    }

    private Developer getDeveloperByMemberId(String memberId) {
        return developerRepository.findByMemberId(memberId)
                .orElseThrow( () -> new DMakerException(DMakerErrorCode.NO_DEVELOPER));
    }

    @Transactional // 변경된 사항 적용 후 커밋되도록 함.
    public DeveloperDetailDto editDeveloper(
            String memberId, EditDeveloper.Request request
    ) {
        request.getDeveloperLevel().validateExperienceYears(request.getExperienceYears());

        return DeveloperDetailDto.fromEntity(
                getUpdatedDeveloperFromRequest(
                        request,
                        getDeveloperByMemberId(memberId)
                )
        );
    }

    private static Developer getUpdatedDeveloperFromRequest(
            EditDeveloper.Request request,
            Developer developer
    ) {
        developer.setDeveloperLevel(request.getDeveloperLevel());
        developer.setDeveloperSkillType(request.getDeveloperSkillType());
        developer.setExperienceYears(request.getExperienceYears());

        return developer;
    }

//    private void validateDeveloperLevel(
//            DeveloperLevel developerLevel,
//            Integer experienceYears
//    ) {
//        developerLevel.validateExperienceYears(experienceYears);
//        if (experienceYears < developerLevel.getMinExperienceYears() ||
//                experienceYears > developerLevel.getMaxExperienceYears()) {
//            throw new DMakerException(DMakerErrorCode.LEVEL_EXPERIENCE_YEARS_NOT_MATCHED);
//        }

//        if (developerLevel == DeveloperLevel.SENIOR
//                && experienceYears < MIN_SENIOR_EXPERIENCE_YEARS) {
////            throw new RuntimeException("SENIOR eed 10 years experience.");
//            throw new DMakerException(DMakerErrorCode.LEVEL_EXPERIENCE_YEARS_NOT_MATCHED);
//        }
//        if (developerLevel == DeveloperLevel.JUNGNIOR
//                && (experienceYears < MAX_JUNIOR_EXPERIENCE_YEARS
//                || experienceYears > MIN_SENIOR_EXPERIENCE_YEARS)) {
//            throw new DMakerException(DMakerErrorCode.LEVEL_EXPERIENCE_YEARS_NOT_MATCHED);
//        }
//        if (developerLevel == DeveloperLevel.JUNIOR
//                && experienceYears > MAX_JUNIOR_EXPERIENCE_YEARS) {
//            throw new DMakerException(DMakerErrorCode.LEVEL_EXPERIENCE_YEARS_NOT_MATCHED);
//        }
//    }

    @Transactional
    public DeveloperDetailDto deleteDeveloper(
            String memberId
    ) {
        // 1. EMPLOYED -> RETIRED
        Developer developer = developerRepository.findByMemberId(memberId)
                .orElseThrow(() -> new DMakerException(DMakerErrorCode.NO_DEVELOPER));
        developer.setStatusCode(StatusCode.RETIRED);
        // 2. save into RetiredDeveloper
        RetiredDeveloper retiredDeveloper = RetiredDeveloper.builder()
                .memberId(memberId)
                .name(developer.getName())
                .build();
        retiredDeveloperRepository.save(retiredDeveloper);
        return DeveloperDetailDto.fromEntity(developer);
    }
}
