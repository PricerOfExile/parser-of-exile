package poe.currency;

import jakarta.annotation.Nonnull;

public record Currency(@Nonnull String tradeId,
                       double chaosEquivalent) {
}
