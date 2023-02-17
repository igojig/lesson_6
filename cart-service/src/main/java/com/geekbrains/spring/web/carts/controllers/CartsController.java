package com.geekbrains.spring.web.carts.controllers;

import com.geekbrains.spring.web.api.dto.ProductDto;
import com.geekbrains.spring.web.api.dto.StringResponse;

import com.geekbrains.spring.web.api.dto.Cart;
import com.geekbrains.spring.web.carts.services.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
public class CartsController {
    private final CartService cartService;

    @Autowired
    private final RestTemplate restTemplate;


//    @GetMapping("/prefix")
//    public String getPrefix(){
//        return cartService.getPrefix();
//    }

//    @GetMapping("/getForOrders/{username}")
//    public Cart getCurrentCart(@PathVariable String username){
//        return cartService.getCurrentCart(getCurrentCartUuid(username, null));
//    }


    @GetMapping("/{uuid}")
    public Cart getCart(@RequestHeader(required = false) String username, @PathVariable String uuid) {
        String cartUuid=getCurrentCartUuid(username, uuid);
        return cartService.getCurrentCart(cartUuid);}

    @GetMapping("/generate")
    public StringResponse getCart() {
        return new StringResponse(cartService.generateCartUuid());
    }

    @GetMapping("/{uuid}/add/{productId}")
    public void add(@RequestHeader(required = false) String username, @PathVariable String uuid, @PathVariable Long productId) {


        ProductDto productDto=restTemplate.getForObject("http://localhost:5555/core/api/v1/products/" + productId.toString(), ProductDto.class);

        cartService.addToCart(getCurrentCartUuid(username, uuid), productDto);
    }

    @GetMapping("/{uuid}/decrement/{productId}")
    public void decrement(@RequestHeader(required = false) String username, @PathVariable String uuid, @PathVariable Long productId) {
        cartService.decrementItem(getCurrentCartUuid(username, uuid), productId);
    }

    @GetMapping("/{uuid}/remove/{productId}")
    public void remove(@RequestHeader(required = false) String username, @PathVariable String uuid, @PathVariable Long productId) {
        cartService.removeItemFromCart(getCurrentCartUuid(username, uuid), productId);
    }

    @GetMapping("/{uuid}/clear")
    public void clear(@RequestHeader(required = false) String username, @PathVariable String uuid) {
        cartService.clearCart(getCurrentCartUuid(username, uuid));
    }

    @GetMapping("/{uuid}/merge")
    public void merge(@RequestHeader String username, @PathVariable String uuid) {
        cartService.merge(
                getCurrentCartUuid(username, null),
                getCurrentCartUuid(null, uuid)
        );
    }

    private String getCurrentCartUuid(String username, String uuid) {
        if (username != null) {
            return cartService.getCartUuidFromSuffix(username);
        }
        return cartService.getCartUuidFromSuffix(uuid);
    }
}
