package com.smartx.rag.application;

import org.springframework.stereotype.Service;

@Service
public class RagPipelineService {
    public String executePipeline(String query) {
        // 1. Retrieve from Knowledge Graph
        // 2. Assemble Prompt
        // 3. Call LLM
        return "RAG Response";
    }
}
