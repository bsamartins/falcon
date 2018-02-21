package com.bsamartins.falcon.documentservice.service;

import com.bsamartins.falcon.documentservice.model.Document;
import com.bsamartins.falcon.documentservice.repository.DocumentRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoSession;
import org.mockito.quality.Strictness;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.when;

public class DocumentServiceTest {

    @InjectMocks
    private DocumentService documentService = new DocumentService();

    @Mock
    private DocumentRepository documentRepository;

    private MockitoSession mockitoSession;

    @BeforeEach
    void setup() {
        // Manually initializing Mockito
        // as there is no support yet for Jupiter
        mockitoSession = Mockito.mockitoSession().initMocks(this)
                .strictness(Strictness.STRICT_STUBS)
                .startMocking();
    }

    @AfterEach
    void tearDown() {
        mockitoSession.finishMocking();
    }

    @Test
    public void testFindAll() {
        Document d1 = new Document(null);
        Document d2 = new Document(null);

        when(documentRepository.findAll()).thenReturn(Flux.just(d1, d2));

        StepVerifier.create(documentService.findAll())
                .expectNext(d1, d2)
                .verifyComplete();
    }

    @Test
    public void testSave() {
        Document d = new Document(null);

        when(documentRepository.save(d)).thenReturn(Mono.just(d));

        StepVerifier.create(this.documentService.save(d))
                .expectNext(d)
                .verifyComplete();
    }
}
