package com.example.jobservice.vo.jobrecruit;

import com.example.jobservice.vo.Category;
import com.example.jobservice.vo.Company;
import com.example.jobservice.vo.Stack;
import com.example.jobservice.vo.Type;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobRecruitPaging {

    private Long id;
    private String title;
    private String url;
    private String workExperience;
    private LocalDateTime createdAt;
    private String company;
    private String region;
    private String logoUrl;
    private String type;
    private String deadline;
    private List<String> categories = new ArrayList<>();
    private List<String> stacks = new ArrayList<>();
}
