package ru.desnitsa.backend.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

import java.util.List;
import java.util.Set;

@Table("contacts")
public record Contacts(
        @Id
        Long id,
        @MappedCollection(idColumn = "contacts_id")
        Set<PhoneNumber> phoneNumber,
        String address,
        String email,
        String telegram,
        String vkLink,
        String youtubeLink,
        String rutubeLink
) {
    public static Contacts of(Set<PhoneNumber> phoneNumber, String address,
                              String email, String telegram, String vkLink,
                              String youtubeLink, String rutubeLink) {
        return new Contacts(null, phoneNumber, address, email, telegram,
                vkLink, youtubeLink, rutubeLink);
    }
}
