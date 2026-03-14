package com.revpay.transactionservice.client.UserClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "user-service", url = "http://localhost:8081")
public interface UserClient {

    @GetMapping("/api/users/by-email")
    UserLookupResponse getUserByEmail(@RequestParam("email") String email);

    class UserLookupResponse {
        private Long id;
        private Long userId;
        private String email;
        private String fullName;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }

        /** Returns the ID regardless of which field the user-service uses */
        public Long resolvedId() {
            if (id != null) return id;
            return userId;
        }
    }
}
