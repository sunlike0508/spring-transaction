package hello.springtx.order;

import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@Slf4j
@SpringBootTest
class OrderServiceTest {

    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderRepository orderRepository;

    @Test
    void complete() throws NotEnoughMoneyException {
        Order order = new Order();
        order.setUsername("정상");

        orderService.order(order);

        Order order1 = orderRepository.findById(order.getId()).orElseThrow();

        assertThat(order1.getPayStatus()).isEqualTo("완료");
    }

    @Test
    void runtime() {
        Order order = new Order();
        order.setUsername("예외");

        assertThatThrownBy(() -> orderService.order(order)).isInstanceOf(RuntimeException.class);

        Optional<Order> order1 = orderRepository.findById(order.getId());

        assertThat(order1).isEmpty();
    }

    @Test
    void 잔고부족() {
        Order order = new Order();
        order.setUsername("잔고부족");

        assertThatThrownBy(() -> orderService.order(order)).isInstanceOf(NotEnoughMoneyException.class);

        Optional<Order> order1 = orderRepository.findById(order.getId());

        assertThat(order1.get().getPayStatus()).isEqualTo("대기");
    }
}