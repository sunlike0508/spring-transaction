package hello.springtx.propagation;


import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestReporter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
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

    @Test
    void commit3() {
        log.info("transaction111 start");
        TransactionStatus status = this.transactionManager.getTransaction(new DefaultTransactionAttribute());
        log.info("transaction111 commit start");
        transactionManager.commit(status);

        log.info("transaction222 start");
        TransactionStatus status2 = this.transactionManager.getTransaction(new DefaultTransactionAttribute());
        log.info("transaction22 rollback start");
        transactionManager.rollback(status2);
    }

    @Test
    void commit4() {
        log.info("외부 트랜잭션 시작");
        TransactionStatus outer = this.transactionManager.getTransaction(new DefaultTransactionAttribute());
        log.info("outer.inNew() = {}", outer.isNewTransaction());

        log.info("내부 트랜잭션 시작");
        TransactionStatus innter = this.transactionManager.getTransaction(new DefaultTransactionAttribute());
        log.info("innter.inNew() = {}", innter.isNewTransaction());
        log.info("내부 트랜잭션 커밋");
        transactionManager.commit(innter);

        log.info("외부 트랜잭션 커밋");
        transactionManager.commit(outer);
    }


    @Test
    void commit5() {
        log.info("외부 트랜잭션 시작");
        TransactionStatus outer = this.transactionManager.getTransaction(new DefaultTransactionAttribute());
        log.info("outer.inNew() = {}", outer.isNewTransaction());

        log.info("내부 트랜잭션 시작");
        TransactionStatus innter = this.transactionManager.getTransaction(new DefaultTransactionAttribute());
        log.info("innter.inNew() = {}", innter.isNewTransaction());
        log.info("내부 트랜잭션 커밋");
        transactionManager.commit(innter);

        log.info("외부 트랜잭션 로오올백");
        transactionManager.rollback(outer);
    }

    @Test
    void commit6() {
        log.info("외부 트랜잭션 시작");
        TransactionStatus outer = this.transactionManager.getTransaction(new DefaultTransactionAttribute());
        log.info("outer.inNew() = {}", outer.isNewTransaction());

        log.info("내부 트랜잭션 시작");
        TransactionStatus innter = this.transactionManager.getTransaction(new DefaultTransactionAttribute());
        log.info("innter.inNew() = {}", innter.isNewTransaction());
        log.info("내부 트랜잭션 롤백");
        transactionManager.rollback(innter);

        log.info("외부 트랜잭션 commit");
        transactionManager.commit(outer);
    }

    @Test
    void commit7() {
        log.info("외부 트랜잭션 시작");
        TransactionStatus outer = this.transactionManager.getTransaction(new DefaultTransactionAttribute());
        log.info("outer.inNew() = {}", outer.isNewTransaction());

        log.info("내부 트랜잭션 시작");
        DefaultTransactionAttribute defaultTransactionAttribute= new DefaultTransactionAttribute();
        defaultTransactionAttribute.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);

        TransactionStatus inner = transactionManager.getTransaction(defaultTransactionAttribute);
        log.info("inner.inNew() = {}", inner.isNewTransaction());

        log.info("내부 트랜잭션 롤백");
        transactionManager.rollback(inner);

        log.info("외부 트랜잭션 커밋");
        transactionManager.commit(outer);
    }


    @Test
    void commit8() {
        log.info("외부 트랜잭션 시작");
        TransactionStatus outer = this.transactionManager.getTransaction(new DefaultTransactionAttribute());
        log.info("outer.inNew() = {}", outer.isNewTransaction());

        log.info("내부 트랜잭션 시작");
        DefaultTransactionAttribute defaultTransactionAttribute= new DefaultTransactionAttribute();
        defaultTransactionAttribute.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);

        TransactionStatus inner = transactionManager.getTransaction(defaultTransactionAttribute);
        log.info("inner.inNew() = {}", inner.isNewTransaction());

        log.info("내부 트랜잭션 커밋");
        transactionManager.commit(inner);

        log.info("외부 트랜잭션 롤백");
        transactionManager.rollback(outer);
    }
}




















