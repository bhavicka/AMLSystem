package com.tss.AmlSystem.dto.event;

public record UserRegisteredEvent(String email, String name, String tempPassword) {
}
