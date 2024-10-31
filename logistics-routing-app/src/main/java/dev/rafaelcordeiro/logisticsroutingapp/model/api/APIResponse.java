package dev.rafaelcordeiro.logisticsroutingapp.model.api;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class APIResponse<E> {
    private Boolean success;
    private String message;
    private List<String> messages;
    private E entity;
    private List<E> entities;
}
