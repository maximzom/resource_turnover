package com.agriculture.resource_turnover;

import com.agriculture.resource_turnover.models.Order;
import com.agriculture.resource_turnover.models.OrderStatus;
import com.agriculture.resource_turnover.models.Resource;
import com.agriculture.resource_turnover.repositories.OrderRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyDouble;

import java.util.List;

@DataJpaTest
@ActiveProfiles("test")
class OrderRepositoryTest {

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private TestEntityManager entityManager;

	@Test
	void shouldFindOrdersByStatus() {
		Resource resource = new Resource();
		entityManager.persist(resource);

		Order order = new Order();
		order.setResource(resource);
		order.setStatus(OrderStatus.PENDING_EXECUTION);
		entityManager.persist(order);

		List<Order> found = orderRepository.findByStatus(OrderStatus.PENDING_EXECUTION);

		assertThat(found).hasSize(1);
		assertThat(found.get(0).getStatus()).isEqualTo(OrderStatus.PENDING_EXECUTION);
	}
}