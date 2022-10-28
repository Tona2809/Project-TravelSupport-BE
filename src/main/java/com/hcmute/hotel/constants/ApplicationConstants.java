package com.hcmute.hotel.constants;

public class ApplicationConstants {
    public static final String EMAIL_PATTERN =
            "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
    public static final String SUCCESSFUL = "SUCCESSFUL";
    public static final int SUCCESSFUL_CODE = 200;
    public static final String FAILED = "FAILED";
    public static final int FAILED_CODE = 400;
    // public static final String FORBIDDEN = "FORBIDDEN";
    // public static final String SHOW_MESSAGE = "SHOW_MESSAGE";
    // public static final String PERMISSION_DENY = "PERMISSION_DENY";
    public static final String BAD_REQUEST = "BAD_REQUEST";
    public static final int BAD_REQUEST_CODE = 400;
    public static final String BAD_REQUEST_MESSAGE = "This's a bad request"; // use in case have not exception
    // public static final String EXISTED_FIELD = "EXISTED_FIELD";
    // public static final String SESSION_EXPIRED = "SESSION_EXPIRED";
    public static final String NOT_FOUND = "NOT_FOUND";
    public static final int NOT_FOUND_CODE = 400;
    // public static final String AUTH_FAILED = "AUTH_FAILED";
    // public static final String IS_BLOCKED = "IS_BLOCKED";
    public static final String IS_DUPLICATED = "IS_DUPLICATED";
    public static final int DUPLICATED_CODE = 400;

//    public static final String SERVICE_NOT_FOUND = "SERVICE_NOT_FOUND";
//    public static final String CAR_NOT_FOUND = "CAR_NOT_FOUND";
//    public static final String CAR_SERVICE_NAME_IS_DUPLICATED = "CAR_SERVICE_NAME_IS_DUPLICATED";
//
//    public static final int PAGE_SIZE_OF_GET_ALL_CUSTOMER = 3;
//
//    public static final String GOODS_NOT_FOUND = "GOODS_NOT_FOUND";
//
//
//    public static final String IMAGE_DIRECTORY = "src\\main\\resources\\images";
}
