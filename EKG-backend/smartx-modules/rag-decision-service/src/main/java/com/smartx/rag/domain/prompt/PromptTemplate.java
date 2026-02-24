package com.smartx.rag.domain.prompt;

import lombok.Data;

@Data
public class PromptTemplate {
    private String id;
    private String templateContent;
    private String scenario;
}
