package com.example.command.dto;

public class ResultCommand {
    private String result;
    private String error;

    public String getError() {
        return error;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public void setError(String error) {
        this.error = error;
    }
}
