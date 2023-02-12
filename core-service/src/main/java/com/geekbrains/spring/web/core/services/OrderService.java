package com.geekbrains.spring.web.core.services;

import com.geekbrains.spring.web.api.dto.Cart;
import com.geekbrains.spring.web.core.repositories.OrdersRepository;
import com.geekbrains.spring.web.api.exceptions.ResourceNotFoundException;

import com.geekbrains.spring.web.core.dto.OrderDetailsDto;
import com.geekbrains.spring.web.core.entities.Order;
import com.geekbrains.spring.web.core.entities.OrderItem;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    // заглушка для запроса
    private String blank = "_EMPTY";

    private final OrdersRepository ordersRepository;
    private final ProductsService productsService;
    private final RestTemplate restTemplate;

    @Transactional
    public void createOrder(String username, OrderDetailsDto orderDetailsDto) {

        // подключаем заголовок
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("username", username);

        // запрашиваем текущую корзину у CartService
        ResponseEntity<Cart> response = restTemplate.exchange("http://localhost:5555/cart/api/v1/cart/" + blank,
                HttpMethod.GET, new HttpEntity<>(httpHeaders),
                Cart.class);
        Cart currentCart = response.getBody();


        Order order = new Order();
        order.setAddress(orderDetailsDto.getAddress());
        order.setPhone(orderDetailsDto.getPhone());
        order.setUsername(username);
        order.setTotalPrice(currentCart.getTotalPrice());
        List<OrderItem> items = currentCart.getItems().stream()
                .map(o -> {
                    OrderItem item = new OrderItem();
                    item.setOrder(order);
                    item.setQuantity(o.getQuantity());
                    item.setPricePerProduct(o.getPricePerProduct());
                    item.setPrice(o.getPrice());
                    item.setProduct(productsService.findById(o.getProductId()).orElseThrow(() -> new ResourceNotFoundException("Product not found")));
                    return item;
                }).collect(Collectors.toList());
        order.setItems(items);
        ordersRepository.save(order);


        ResponseEntity<String> r1 = restTemplate.exchange("http://localhost:5555/cart/api/v1/cart/" + blank + "/clear",
                HttpMethod.GET, new HttpEntity<>(httpHeaders),
                String.class);
//        System.out.println(r1);

    }

    public List<Order> findOrdersByUsername(String username) {
        return ordersRepository.findAllByUsername(username);
    }
}
