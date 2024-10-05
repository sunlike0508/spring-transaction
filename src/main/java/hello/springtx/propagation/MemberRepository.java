package hello.springtx.propagation;


import java.util.Optional;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class MemberRepository {

    private final EntityManager em;


    @Transactional
    public void save(Member member) {
        log.info("save member : {}", member);
        em.persist(member);
    }

    public Optional<Member> findById(String username) {
        return em.createQuery("select m from Member m where m.username = :username", Member.class)
                .setParameter("username", username).getResultList().stream().findAny();
    }
}
