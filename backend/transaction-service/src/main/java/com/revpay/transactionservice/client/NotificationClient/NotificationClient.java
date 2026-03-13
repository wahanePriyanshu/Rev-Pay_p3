package com.revpay.transactionservice.client.NotificationClient;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "notification-service", url = "http://localhost:8085")
public interface NotificationClient {

    @PostMapping("/api/notifications/internal/create")
    void createNotification(@RequestBody CreateNotificationRequest request);

    class CreateNotificationRequest {
        private Long userId;
        private String title;
        private String message;
        private String type;
        private String referenceType;
        private Long referenceId;

        public Long getUserId() {
            return userId;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getReferenceType() {
            return referenceType;
        }

        public void setReferenceType(String referenceType) {
            this.referenceType = referenceType;
        }

        public Long getReferenceId() {
            return referenceId;
        }

        public void setReferenceId(Long referenceId) {
            this.referenceId = referenceId;
        }
    }
}