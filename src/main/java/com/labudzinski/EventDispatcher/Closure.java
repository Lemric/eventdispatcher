package com.labudzinski.EventDispatcher;

import com.fasterxml.uuid.Generators;

import java.util.UUID;

public interface Closure {
    public UUID key = Generators.randomBasedGenerator().generate();
}
