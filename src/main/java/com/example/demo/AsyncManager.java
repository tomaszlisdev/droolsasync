package com.example.demo;

import org.kie.api.KieServices;
import org.kie.api.builder.*;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.ObjectFilter;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderError;
import org.kie.internal.builder.KnowledgeBuilderErrors;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class AsyncManager {

    @Async
    public void makeInSeparateThread(){
        Model m = new Model();
        m.setName("a");
        try {
            KieSession session = kieSession();
            session.insert(m);
            session.fireAllRules();
            System.out.printf("result "+findFacts(session, m.getClass()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        new A().call();
    }

    public KieSession kieSession() throws IOException {
        return kieContainer().newKieSession();
    }

    protected <T> Collection<T> findFacts(final KieSession session, final Class<T> factClass ) {
        ObjectFilter filter = object -> factClass.isInstance(object.getClass());
        return session.getObjects( filter ).stream().map(factClass::cast).collect(Collectors.toList());
    }

    public KieFileSystem kieFileSystem() throws IOException {
        KieFileSystem kieFileSystem = getKieServices().newKieFileSystem();
        Resource resource = ResourceFactory.newClassPathResource("test2.drl", "UTF-8");
        kieFileSystem.write(resource);
        return kieFileSystem;
    }

    private KieServices getKieServices() {
        return KieServices.Factory.get();
    }

    public KieContainer kieContainer() throws IOException {
        KieRepository kieRepository = getKieServices().getRepository();
/*

        kieRepository.addKieModule(new KieModule() {
            public ReleaseId getReleaseId() {
                return kieRepository.getDefaultReleaseId();
            }
        });
*/


      //  validateDrl();
        KieBuilder kieBuilder = getKieServices()
                .newKieBuilder(kieFileSystem())
                .buildAll();
        if(kieBuilder.getResults().hasMessages(new Message.Level[]{Message.Level.ERROR})){
            throw new IllegalArgumentException("Kie session loaded with errors" +  kieBuilder.getResults().getMessages());
        }
        KieModule module = kieBuilder.getKieModule();
        kieRepository.addKieModule(module);

        return getKieServices().newKieContainer(module.getReleaseId());
    }

    private void validateDrl() {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newClassPathResource( "test2.drl", getClass() ), ResourceType.DRL );
        KnowledgeBuilderErrors errors = kbuilder.getErrors();

        if( errors.size() > 0 )
        {
            for( KnowledgeBuilderError error : errors )
            {
                System.err.println( error );
            }
        }
    }

    private class A{
        void call(){
            try {
                new Thread().sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            throw new RuntimeException("Error");
        }
    }
}
