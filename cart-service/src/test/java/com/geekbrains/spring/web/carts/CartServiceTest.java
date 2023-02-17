package com.geekbrains.spring.web.carts;

import com.geekbrains.spring.web.api.dto.ProductDto;
import com.geekbrains.spring.web.carts.services.CartService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Optional;

@SpringBootTest
public class CartServiceTest {

    @Autowired
    CartService cartService;

    @BeforeEach
    public void initCart() {
        cartService.clearCart("test_cart");
        cartService.clearCart("test_cart_guest");
        cartService.clearCart("test_cart_user");
    }

    @Test
    public void addToCartTest(){
        ProductDto productDto=new ProductDto(1L, "test", 100);

        cartService.addToCart("test_cart", productDto);
        cartService.addToCart("test_cart", productDto);
        cartService.addToCart("test_cart", productDto);

        Assertions.assertEquals(1, cartService.getCurrentCart("test_cart").getItems().size());
        Assertions.assertEquals(300, cartService.getCurrentCart("test_cart").getTotalPrice());
    }


    @Test
    public void mergeCartTest(){
        ProductDto productDto_1=new ProductDto(1L, "test_1", 100);
        ProductDto productDto_2=new ProductDto(2L, "test_2", 200);
        ProductDto productDto_3=new ProductDto(3L, "test_3", 300);

        ProductDto productDto_4=new ProductDto(1L, "test_1", 100);
        ProductDto productDto_5=new ProductDto(2L, "test_2", 200);
        ProductDto productDto_6=new ProductDto(3L, "test_3", 300);

        cartService.addToCart("test_cart_guest", productDto_1);
        cartService.addToCart("test_cart_guest", productDto_2);
        cartService.addToCart("test_cart_guest", productDto_3);

        cartService.addToCart("test_cart_user", productDto_1);
        cartService.addToCart("test_cart_user", productDto_2);
        cartService.addToCart("test_cart_user", productDto_3);

        cartService.merge("test_cart_user", "test_cart_guest");
        Assertions.assertEquals(0, cartService.getCurrentCart("test_cart_guest").getItems().size());
        Assertions.assertEquals(0, cartService.getCurrentCart("test_cart_guest").getTotalPrice());

        Assertions.assertEquals(3, cartService.getCurrentCart("test_cart_user").getItems().size());
        Assertions.assertEquals(1200, cartService.getCurrentCart("test_cart_user").getTotalPrice());
    }

}
