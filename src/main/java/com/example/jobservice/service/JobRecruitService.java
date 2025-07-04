package com.example.jobservice.service;

import com.example.jobservice.dto.company.response.CompanyNameLogoResponse;
import com.example.jobservice.dto.recruit.request.JobRecruitRequestDto;
import com.example.jobservice.dto.recruit.request.JobSearchCondition;
import com.example.jobservice.dto.recruit.response.JobRecruitListResponseDto;
import com.example.jobservice.dto.recruit.response.data.RecruitDetailDataDto;
import com.example.jobservice.http.CompanyServiceClient;
import com.example.jobservice.mapper.*;
import com.example.jobservice.vo.JobRecruit;
import com.example.jobservice.vo.jobrecruit.JobRecruitPaging;
import com.example.jobservice.kafka.KafkaProducerService;
import com.example.jobservice.kafka.dto.RecruitCountMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JobRecruitService {

    private final JobRecruitMapper jobRecruitMapper;

    private final TypeService typeService;
    private final CompanyService companyService;
    private final CategoryService categoryService;
    private final DepartmentService departmentService;
    private final JobRecruitDetailService jobRecruitDetailService;
    private final KafkaProducerService kafkaProducerService;
    private final FileUploadService fileUploadService;

    @Transactional
    public void save(JobRecruitRequestDto[] requests) {
        try{
            for(JobRecruitRequestDto request : requests) {
                Long departmentId = departmentService.getDepartmentId(request.getBasic().getDepartment());
                Long companyId = companyService.getCompanyId(request.getBasic().getCompany());
                Long typeId = typeService.getTypeId(request.getBasic().getType(), request.getBasic().getTitle());

                JobRecruit jobRecruit = new JobRecruit(request.getBasic().getTitle(), request.getBasic().getWorkExperience(), request.getBasic().getUrl(),
                        departmentId, companyId, typeId, request.getBasic().getDeadline(), request.getBasic().getPostingType());

                jobRecruitMapper.insert(jobRecruit);

                jobRecruitDetailService.save(request, jobRecruit.getId());
                categoryService.save(request.getBasic().getCategory(), request.getDetail(), jobRecruit.getId());

                RecruitCountMessage message = new RecruitCountMessage(request.getBasic().getCompany(), "count 증가");

                kafkaProducerService.sendRecruitCount(message);
            }
        }
        catch(Exception e) {
            e.printStackTrace();
            throw e;
        }

    }

    public void saveOne(JobRecruitRequestDto request, MultipartFile image) throws IOException {
        try {
            String imageUrl = fileUploadService.uploadImage(image);

            Long departmentId = departmentService.getDepartmentId(request.getBasic().getDepartment());
            Long companyId = companyService.getCompanyId(request.getBasic().getCompany());
            Long typeId = typeService.getTypeId(request.getBasic().getType(), request.getBasic().getTitle());

            JobRecruit jobRecruit = new JobRecruit(request.getBasic().getTitle(), request.getBasic().getWorkExperience(), request.getBasic().getUrl(),
                    departmentId, companyId, typeId, request.getBasic().getDeadline(), request.getBasic().getPostingType());

            jobRecruitMapper.insert(jobRecruit);

            jobRecruitDetailService.save(imageUrl, jobRecruit.getId());
            categoryService.save(request.getBasic().getCategory(), request.getDetail().getBody(), jobRecruit.getId());

            RecruitCountMessage message = new RecruitCountMessage(request.getBasic().getCompany(), "count 증가");

            kafkaProducerService.sendRecruitCount(message);

        }catch(Exception e) {
            e.printStackTrace();
            throw e;
        }

    }

    public JobRecruitListResponseDto search(JobSearchCondition condition, Pageable pageable, String sort) {
        List<JobRecruitPaging> searchedRecruits = jobRecruitMapper.findAllWithConditions(condition, pageable, sort);
        Integer count = jobRecruitMapper.findAllWithConditionsCount(condition);

        return new JobRecruitListResponseDto(searchedRecruits, count);
    }


    public List<String> getAllUrls() {
        List<String> urls = jobRecruitMapper.findAllUrls();

        return urls;
    }

    public RecruitDetailDataDto getRecruitDetail(Long recruitId) {
        return jobRecruitDetailService.getDetails(recruitId);
    }

    public JobRecruitListResponseDto getRecruitsByCompany(String companyName, Pageable pageable) {
        List<JobRecruitPaging> recruits = jobRecruitMapper.findRecruitsByCompany(companyName, pageable);
        Integer count = jobRecruitMapper.findRecruitsByCompanyCount(companyName);

        return new JobRecruitListResponseDto(recruits, count);
    }
}
