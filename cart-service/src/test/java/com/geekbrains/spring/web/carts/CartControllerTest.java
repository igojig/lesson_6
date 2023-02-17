package com.geekbrains.spring.web.carts;

import com.geekbrains.spring.web.api.dto.Cart;
import com.geekbrains.spring.web.api.dto.OrderItemDto;
import com.geekbrains.spring.web.api.dto.StringResponse;
import com.geekbrains.spring.web.carts.services.CartService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.BDDMockito.given;
import static org.hamcrest.Matchers.hasSize;




@SpringBootTest
@AutoConfigureMockMvc
public class CartControllerTest {
    @Autowired
    private MockMvc mvc;

    @SpyBean
    CartService cartService;

//    @BeforeEach
//    public void initCart() {
//        cartService.clearCart("WEB_MARKET_test_user");
//
//    }



    @Test
    public void testGenerate() throws Exception{
        StringResponse stringResponse=new StringResponse("_XXX_");

        given(cartService.generateCartUuid()).willReturn(stringResponse.getValue());
        mvc
                .perform(get("/api/v1/cart/generate").contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").hasJsonPath())
                .andExpect(jsonPath("$.value", is("_XXX_")));


    }

    @Test
    public void testGetCart() throws Exception{

        OrderItemDto orderItemDto1=new OrderItemDto(1L, "Milk", 2, 50, 100);
        OrderItemDto orderItemDto2=new OrderItemDto(2L, "Bread", 3, 50, 150);

        List<OrderItemDto> list=new ArrayList<>();
        list.add(orderItemDto1);
        list.add(orderItemDto2);

        Cart cart=new Cart();
        cart.setItems(list);
        cart.setTotalPrice(250);

        given(cartService.getCurrentCart("SPRING_WEB_test_user")).willReturn(cart);
        mvc
                .perform(get("/api/v1/cart/_XXX_").contentType(MediaType.APPLICATION_JSON).header("username","test_user"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.totalPrice", is(cart.getTotalPrice())))
                .andExpect(jsonPath("$.items[1].price", is(cart.getItems().get(1).getPrice())));
    }

}
