package com.innowise.model.dto;

import java.io.Serializable;

public record ErrorDto(String message, int value) implements Serializable {

}
