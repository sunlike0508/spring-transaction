package hello.springtx.apply;


import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@SpringBootTest
public class InitTxTest {


    @Autowired
    private Hello hello;

    @Test
    void go() {

    }

    @TestConfiguration
    static class Config {
        @Bean
        Hello hello(){
            return new Hello();
        }
    }

    @Slf4j
    static class Hello {
        @PostConstruct
        @Transactional
        public void init1() {
            boolean isActive = TransactionSynchronizationManager.isActualTransactionActive();

            log.info("hello active {}", isActive);
        }

        @EventListener(ApplicationReadyEvent.class)
        @Transactional
        protected void initV2() {
            log.info("hello active Event {}", TransactionSynchronizationManager.isActualTransactionActive());
        }
    }
}
