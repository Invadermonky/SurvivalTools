package com.invadermonky.survivaltools.api.blocks;

public enum RedstoneMode {
    REQUIRED,
    INVERTED,
    IGNORED;

    public RedstoneMode next() {
        return RedstoneMode.values()[(this.ordinal() + 1) % values().length];
    }

    public RedstoneMode previous() {
        int length = values().length;
        return RedstoneMode.values()[(this.ordinal() + length - 1) % length];
    }

    @Override
    public String toString() {
        return this.name().toLowerCase();
    }
}
