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
public class InternalCallV2Test {


    @Autowired
    private CallService callService;

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

        @Bean
        InternalCallService internalCallService() {
            return new InternalCallService();
        }
    }

    static class CallService {

        @Autowired
        private InternalCallService internalCallService;

        public void external() {
            log.info("call external method");
            printTxInfo();
            internalCallService.internal();
        }

        private void printTxInfo() {
            boolean txActive = TransactionSynchronizationManager.isActualTransactionActive();
            log.info("txActive: {}", txActive);
        }
    }

    static class InternalCallService {

        @Transactional
        public void internal() {
            log.info("call internal method");
            printTxInfo();
        }

        private void printTxInfo() {
            boolean txActive = TransactionSynchronizationManager.isActualTransactionActive();
            log.info("txActive: {}", txActive);
        }
    }
}
