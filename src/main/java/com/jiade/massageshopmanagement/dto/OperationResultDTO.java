package com.jiade.massageshopmanagement.dto;

public class OperationResultDTO {
    private int code;
    private String message;

    public OperationResultDTO() {}

    public OperationResultDTO(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public static OperationResultDTO success() {
        return new OperationResultDTO(200, "success");
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
