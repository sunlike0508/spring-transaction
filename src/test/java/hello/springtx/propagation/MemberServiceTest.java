package hello.springtx.propagation;

import java.rmi.UnexpectedException;
import lombok.extern.slf4j.Slf4j;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.UnexpectedRollbackException;


@Slf4j
@SpringBootTest
class MemberServiceTest {

    @Autowired
    MemberService memberService;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    LogRepository logRepository;

    @Test
    void joinV1() {

        memberService.joinV1("신선호");

        Member member = memberRepository.findById("신선호").orElseThrow();

        assertThat(member.getUsername()).isEqualTo("신선호");

        Log log = logRepository.findById("신선호").orElseThrow();

        assertThat(log.getMessage()).isEqualTo("신선호");
    }



    @Test
    void outerTxOff_fail() {

        assertThatThrownBy(() -> memberService.joinV1("로그예외")).isInstanceOf(RuntimeException.class);

        Member member = memberRepository.findById("로그예외").orElseThrow();

        assertThat(member.getUsername()).isEqualTo("로그예외");

        assertThat(logRepository.findById("신선호").isEmpty()).isTrue();
    }


    @Test
    void 싱글트랜잭션() {

        memberService.joinV1("신선호");

        Member member = memberRepository.findById("신선호").orElseThrow();

        assertThat(member.getUsername()).isEqualTo("신선호");

        Log log = logRepository.findById("신선호").orElseThrow();

        assertThat(log.getMessage()).isEqualTo("신선호");
    }

    @Test
    void 서비스_레포모두트랜잭션() {

        memberService.joinV1("신선호");

        Member member = memberRepository.findById("신선호").orElseThrow();

        assertThat(member.getUsername()).isEqualTo("신선호");

        Log log = logRepository.findById("신선호").orElseThrow();

        assertThat(log.getMessage()).isEqualTo("신선호");
    }

    @Test
    void 서비스_레포모두트랜잭션_오류() {

        assertThatThrownBy(() -> memberService.joinV1("로그예외")).isInstanceOf(RuntimeException.class);

        assertThat(memberRepository.findById("로그예외").isEmpty()).isTrue();

        assertThat(logRepository.findById("신선호").isEmpty()).isTrue();
    }


    @Test
    void 서비스_레포모두트랜잭션_오류_복구실패() {

        assertThatThrownBy(() -> memberService.joinV2("로그예외")).isInstanceOf(UnexpectedRollbackException.class);

        assertThat(memberRepository.findById("로그예외").isEmpty()).isTrue();

        assertThat(logRepository.findById("로그예외").isEmpty()).isTrue();

        //저장 예외 발생
        //Participating transaction failed - marking existing transaction as rollback-only
        //Setting JPA transaction on EntityManager [SessionImpl(2076486718<open>)] rollback-only
        //JDBC transaction marked for rollback-only (exception provided for stack trace)
    }


    @Test
    void 서비스_레포모두트랜잭션_오류_복구성공() {

        assertThatThrownBy(() -> memberService.joinV2("로그예외")).isInstanceOf(UnexpectedRollbackException.class);

        Member member = memberRepository.findById("로그예외").orElseThrow();

        assertThat(member.getUsername()).isEqualTo("로그예외");

        assertThat(logRepository.findById("로그예외").isEmpty()).isTrue();

        //Completing transaction for [hello.springtx.propagation.MemberService.joinV2]
        //Initiating transaction commit
        //Committing JPA transaction on EntityManager [SessionImpl(70952807<open>)]
        //On commit, transaction was marked for roll-back only, rolling back
    }

}