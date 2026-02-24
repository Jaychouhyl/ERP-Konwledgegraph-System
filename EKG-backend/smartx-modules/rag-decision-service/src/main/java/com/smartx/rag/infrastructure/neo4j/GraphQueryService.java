package com.smartx.rag.infrastructure.neo4j;

import org.springframework.stereotype.Service;

@Service
public class GraphQueryService {
    public Object executeCypher(String cypherQuery) {
        return "Graph Result";
    }
}
