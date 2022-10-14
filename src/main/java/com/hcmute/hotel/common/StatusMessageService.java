package com.hcmute.hotel.common;

public class StatusMessageService {
    public enum StatusMessage{
        SENT(1), WATCHED(2);

        private int status;

        StatusMessage(int status) {
            this.status = status;
        }

        @Override
        public String toString() {
            return "StatusMessage{" +
                    "status=" + status +
                    '}';
        }
    }
}
