package dev.rafaelcordeiro.logisticsroutingapp.core.util;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Pair<L, R> {
    private L left;
    private R right;

    public static <L, R> Pair<L, R> of(L left, R right) {
        var pair = new Pair<L, R>();
        pair.left = left;
        pair.right = right;
        return pair;
    }
}
