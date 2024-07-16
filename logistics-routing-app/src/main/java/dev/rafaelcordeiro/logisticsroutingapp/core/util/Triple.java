package dev.rafaelcordeiro.logisticsroutingapp.core.util;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Triple<F, S, T> {
    private F first;
    private S second;
    private T third;
}
