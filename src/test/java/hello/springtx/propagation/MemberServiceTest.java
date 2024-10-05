package hello.springtx.propagation;

import lombok.extern.slf4j.Slf4j;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


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
    void joinV2() {
    }
}