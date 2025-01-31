package br.com.fynncs.record;

import java.time.LocalDate;
import java.util.UUID;

public record Person(UUID userId, String nickname, String name, LocalDate birthday,
                     String gender, String nationality, String maritalStatus, String profession,
                     String education, String email) {
}
