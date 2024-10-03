package hello.springtx.apply;


import lombok.extern.slf4j.Slf4j;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Slf4j
@SpringBootTest
public class TxBasicTest {

    @Autowired
    private BasicService basicService;

    @Test
    void proxyCheck() {
        log.info("aop class={}", basicService.getClass());
        boolean aopProxy = AopUtils.isAopProxy(basicService);

        assertThat(aopProxy).isTrue();
    }

    @Test
    void proxyCheck2() {
        basicService.tx();
        basicService.nonTx();
    }


    @TestConfiguration
    static class TxApplyBasicconfig {
        @Bean
        BasicService basicService() {
            return new BasicService();
        }
    }

    @Slf4j
    static class BasicService {

        //@Transactional
        @Transactional
        public void tx() {
            log.info("call tx");

            boolean txActive = TransactionSynchronizationManager.isActualTransactionActive();

            log.info("tx Active={}", txActive);
        }

        public void nonTx() {
            log.info("call tx");

            boolean txActive = TransactionSynchronizationManager.isActualTransactionActive();

            log.info("tx Active={}", txActive);
        }
    }
}
