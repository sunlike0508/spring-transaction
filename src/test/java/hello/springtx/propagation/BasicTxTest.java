package hello.springtx.propagation;


import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;

@Slf4j
@SpringBootTest
public class BasicTxTest {

    @Autowired
    PlatformTransactionManager transactionManager;

    @TestConfiguration
    static class Config {

        @Bean
        public PlatformTransactionManager transactionManager(DataSource dataSource) {
            return new DataSourceTransactionManager(dataSource);
        }
    }

    @Test
    void commit() {
        log.info("transaction start");
        TransactionStatus status = this.transactionManager.getTransaction(new DefaultTransactionAttribute());
        log.info("transaction commit start");
        transactionManager.commit(status);
        log.info("transaction commit end");
    }


    @Test
    void rollback() {
        log.info("transaction start");
        TransactionStatus status = this.transactionManager.getTransaction(new DefaultTransactionAttribute());
        log.info("transaction rollback start");
        transactionManager.rollback(status);
        log.info("transaction rollback end");
    }

    @Test
    void commit2() {
        log.info("transaction111 start");
        TransactionStatus status = this.transactionManager.getTransaction(new DefaultTransactionAttribute());
        log.info("transaction111 commit start");
        transactionManager.commit(status);

        log.info("transaction222 start");
        TransactionStatus status2 = this.transactionManager.getTransaction(new DefaultTransactionAttribute());
        log.info("transaction22 commit start");
        transactionManager.commit(status2);
    }
}




















