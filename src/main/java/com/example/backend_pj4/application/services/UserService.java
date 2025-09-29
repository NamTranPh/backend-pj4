// package com.example.backend_pj4.application.services;

// @Service
// public class UserService {
//     private final UserRepository userRepository;
//     private final RoleRepository roleRepository; // nếu cần kiểm tra role

//     public UserService(UserRepository userRepository, RoleRepository roleRepository) {
//         this.userRepository = userRepository;
//         this.roleRepository = roleRepository;
//     }

//     public User createUser(User user) {
//         // gán ID, createdAt, validate phone/email
//         if (userRepository.existsByPhone(user.getPhone())) {
//             throw new IllegalArgumentException("Phone already exists");
//         }
//         // set createdAt, isActive true mặc định...
//         return userRepository.save(user);
//     }

//     public User updateUser(String id, User partial) {
//         var existing = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
//         // cập nhật các field cần thiết (không update password ở đây)
//         existing.setName(partial.getName());
//         // ...
//         return userRepository.save(existing);
//     }

//     public void deleteUser(String id) {
//         if (!userRepository.existsById(id)) throw new RuntimeException("User not found");
//         userRepository.deleteById(id);
//     }

//     public User getById(String id) {
//         return userRepository.findById(id).orElseThrow(() -> new RuntimeException("Not found"));
//     }

//     public List<User> listAll() { return userRepository.findAll(); }
// }
