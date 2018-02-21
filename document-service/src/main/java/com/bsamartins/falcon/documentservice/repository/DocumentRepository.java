package com.bsamartins.falcon.documentservice.repository;

import com.bsamartins.falcon.documentservice.model.Document;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface DocumentRepository extends ReactiveCrudRepository<Document, String> {
}
