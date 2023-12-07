package pe.LaCasona.backend_casona.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.LaCasona.backend_casona.models.DTO.UserAdmDTO;
import pe.LaCasona.backend_casona.services.UserService;

import java.util.List;

@RestController
@RequestMapping("/admin")
@CrossOrigin("*")
public class AdminController {
    @Autowired
    private UserService userService;
    @GetMapping("/getAll")
    public List<UserAdmDTO> fetchAllUsers(){
        return userService.getAllUsers();
    }
    @PutMapping("update/{id}")
    public UserAdmDTO updateUser(@PathVariable String id, @RequestBody UserAdmDTO updatedUser) {
        return userService.updateUser(id, updatedUser);
    }
    @PostMapping("/save")
    public UserAdmDTO saveUser(@RequestBody UserAdmDTO user) {
        return userService.save(user);
    }
    @DeleteMapping("/delete/{id}")
    public void deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
    }
}
