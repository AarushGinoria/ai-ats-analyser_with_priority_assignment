package com.ai.project.service;

import com.ai.project.dto.gemini.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;


import java.util.ArrayList;
import java.util.List;
@Service
public class AIService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    @Value("${gemini.api.key}")
    private String apiKey;

    public AIService(RestTemplate restTemplate,ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper=objectMapper;
    }

    public ATSResponse analyzeResume(String resumeText, String jobDescription) throws Exception{

        String prompt = """
                Analyze the following resume against the given job description.

                Resume:
                %s

                Job Description:
                %s

                Return ONLY valid JSON in the following format:

                {
                  "atsScore": 0,
                  "missingSkills": [],
                  "suggestions": []
                }
                """.formatted(resumeText, jobDescription);
        Part part = new Part(prompt);

// Create List<Part>
        List<Part> parts = new ArrayList<>();
        parts.add(part);

// Create Content
        Content content = new Content(parts);

// Create List<Content>
        List<Content> contents = new ArrayList<>();
        contents.add(content);

// Create Gemini Request
        GeminiRequest request = new GeminiRequest(contents);
        String url =
                "https://generativelanguage.googleapis.com/v1beta/models/gemini-3-flash-preview:generateContent?key="
                        + apiKey;
        GeminiResponse response = restTemplate.postForObject(
                url,
                request,
                GeminiResponse.class
        );
        String aiJson =
                response
                        .getCandidates()
                        .get(0)
                        .getContent()
                        .getParts()
                        .get(0)
                        .getText();
        ATSResponse atsResponse =
                objectMapper.readValue(aiJson, ATSResponse.class);
        return atsResponse;
    }
}
