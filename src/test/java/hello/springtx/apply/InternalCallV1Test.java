package hello.springtx.apply;


import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Slf4j
@SpringBootTest
public class InternalCallV1Test {


    @Autowired
    private CallService callService;

    @Test
    void internal() {
        log.info("callService= {}", callService.getClass());
        callService.internal();
    }

    @Test
    void external() {
        log.info("callService= {}", callService.getClass());
        callService.external();
    }

    @TestConfiguration
    static class Config {
        @Bean
        CallService callService() {
            return new CallService();
        }
    }

    static class CallService {
        public void external() {
            log.info("call external method");
            printTxInfo();
            internal();
        }

        @Transactional
        public void internal() {
            log.info("call internal method");
            printTxInfo();
        }

        private void printTxInfo() {
            boolean txActive = TransactionSynchronizationManager.isActualTransactionActive();
            log.info("txActive: {}", txActive);

            boolean readActive = TransactionSynchronizationManager.isCurrentTransactionReadOnly();

            log.info("readActive: {}", readActive);
        }
    }
}
