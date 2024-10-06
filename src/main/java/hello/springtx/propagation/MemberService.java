package hello.springtx.propagation;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final LogRepository logRepository;

    @Transactional
    public void joinV1(String username) {
        Member member = new Member(username);
        Log logMessage = new Log(username);

        log.info("멤버 레포 호출 시작");
        memberRepository.save(member);
        log.info("멤버 레포 호출 종료");


        log.info("로그 레포 호출 시작");
        logRepository.save(logMessage);
        log.info("로그 레포 호출 종료");
    }


    @Transactional
    public void joinV2(String username) {
        Member member = new Member(username);
        Log logMessage = new Log(username);

        log.info("멤버 레포 호출 시작");
        memberRepository.save(member);
        log.info("멤버 레포 호출 종료");


        log.info("로그 레포 호출 시작");

        try {

            logRepository.save(logMessage);

        } catch(RuntimeException e) {
            log.info("로그 저장 실패");
            log.info("정상 흐름 반환");
        }

        log.info("로그 레포 호출 종료");
    }
}
