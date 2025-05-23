package com.example.crabfood_api.controller;

import com.example.crabfood_api.dto.request.FoodRequest;
import com.example.crabfood_api.dto.request.UserRequest;
import com.example.crabfood_api.dto.response.FoodResponse;
import com.example.crabfood_api.dto.response.UserResponse;
import com.example.crabfood_api.service.user.IUserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final IUserService service;

    public UserController(IUserService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> findAll(){
        return new ResponseEntity<>(service.findAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> findById(@PathVariable Long id){
        return new ResponseEntity<>(service.findById(id), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> update(@PathVariable Long id, @RequestBody UserRequest request){
        UserResponse updatedFood = service.update(id, request);
        if (updatedFood == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(updatedFood, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        service.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
