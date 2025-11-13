package ro.ppoo.banking.dto;

public record ClientCreateRequest(
        String firstname,
        String lastname,
        String email,
        String phone,
        String cnp,
        boolean gdprAccepted
) {}