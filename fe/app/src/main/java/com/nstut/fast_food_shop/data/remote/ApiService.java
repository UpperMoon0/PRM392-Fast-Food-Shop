package com.nstut.fast_food_shop.data.remote;

import com.nstut.fast_food_shop.model.Cart;
import com.nstut.fast_food_shop.model.Category;
import com.nstut.fast_food_shop.model.DailyRevenue;
import com.nstut.fast_food_shop.model.Order;
import com.nstut.fast_food_shop.data.remote.request.ProductDTO;
import com.nstut.fast_food_shop.model.Product;
import com.nstut.fast_food_shop.model.User;
import com.nstut.fast_food_shop.data.remote.request.AddItemRequest;
import com.nstut.fast_food_shop.data.remote.request.LoginRequest;
import com.nstut.fast_food_shop.data.remote.request.UpdateItemRequest;
import com.nstut.fast_food_shop.data.remote.response.LoginResponse;
import com.nstut.fast_food_shop.data.remote.response.UserResponse;


import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    @GET("api/products")
    Call<List<Product>> searchProducts(@Query("keyword") String keyword, @Query("categoryId") String categoryId);

    @POST("api/auth/login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    @POST("api/auth/register")
    Call<User> register(@Body User user);

    @GET("api/users/{id}")
    Call<User> getUserById(@Path("id") String id);

    @GET("api/categories")
    Call<List<Category>> getAllCategories();

    @GET("api/categories/{id}")
    Call<Category> getCategoryById(@Path("id") String id);

    @POST("api/categories")
    Call<Category> createCategory(@Body Category category);

    @PUT("api/categories/{id}")
    Call<Category> updateCategory(@Path("id") String id, @Body Category category);

    @DELETE("api/categories/{id}")
    Call<Void> deleteCategory(@Path("id") String id);

    @GET("api/products")
    Call<List<Product>> getAllProducts();

    @GET("api/products/{id}")
    Call<Product> getProductById(@Path("id") String id);

    @POST("api/products")
    Call<Product> createProduct(@Body ProductDTO productDTO);

    @PUT("api/products/{id}")
    Call<Product> updateProduct(@Path("id") String id, @Body ProductDTO productDTO);

    @DELETE("api/products/{id}")
    Call<Void> deleteProduct(@Path("id") String id);

    @POST("api/orders")
    Call<Order> createOrder(@Body Order order);

    @GET("api/orders/user/{userId}")
    Call<List<Order>> getOrdersByUser(@Path("userId") String userId);

    @GET("api/orders")
    Call<List<Order>> getAllOrders();

    @GET("api/orders/daily-revenue")
    Call<List<DailyRevenue>> getDailyRevenue();

    @GET("api/cart/{userId}")
    Call<Cart> getCartByUserId(@Path("userId") String userId);

    @POST("api/cart/{userId}/items")
    Call<Cart> addItemToCart(@Path("userId") String userId, @Body AddItemRequest request);

    @DELETE("api/cart/{userId}/items/{cartItemId}")
    Call<Cart> removeItemFromCart(@Path("userId") String userId, @Path("cartItemId") String cartItemId);

    @PUT("api/cart/{userId}/items/{cartItemId}")
    Call<Cart> updateCartItem(@Path("userId") String userId, @Path("cartItemId") String cartItemId, @Body UpdateItemRequest request);

    @Multipart
    @POST("api/upload")
    Call<String> uploadImage(@Part MultipartBody.Part file);
}