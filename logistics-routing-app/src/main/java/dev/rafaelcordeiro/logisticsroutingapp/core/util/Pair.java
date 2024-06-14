package dev.rafaelcordeiro.logisticsroutingapp.core.util;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Pair<L, R> {
    private L left;
    private R right;
}
