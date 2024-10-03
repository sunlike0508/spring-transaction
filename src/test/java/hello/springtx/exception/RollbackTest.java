package hello.springtx.exception;


import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@SpringBootTest
public class RollbackTest {

    @Autowired
    private RollbackService rollbackService;

    @Test
    void test() {
        rollbackService.runtimeException();
    }

    @Test
    void test2() throws RollbackService.MyException {
        rollbackService.checkedException();
    }

    @Test
    void test3() throws RollbackService.MyException {
        rollbackService.rollbackFor();
    }

    @TestConfiguration
    static class Config {
        @Bean
        RollbackService rollbackService() {
            return new RollbackService();
        }
    }
    
    static class RollbackService {

        @Transactional
        public void runtimeException() {
            log.info("call runtimeException");
            throw new RuntimeException();
        }
        
        @Transactional
        public void checkedException() throws MyException {
            log.info("call checkedException");
            throw new MyException();
        }

        @Transactional(rollbackFor = MyException.class)
        public void rollbackFor() throws MyException {
            log.info("call rollbackFor");
            throw new MyException();
        }


        private class MyException extends Exception {}
    }
}
