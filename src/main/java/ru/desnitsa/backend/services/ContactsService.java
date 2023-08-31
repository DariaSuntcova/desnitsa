package ru.desnitsa.backend.services;

import org.springframework.stereotype.Service;
import ru.desnitsa.backend.dto.ContactsDto;
import ru.desnitsa.backend.entities.Contacts;
import ru.desnitsa.backend.exceptions.ContentNotFoundException;
import ru.desnitsa.backend.repositories.ContactsRepository;

@Service

public class ContactsService {
    private final ContactsRepository contactsRepository;

    public ContactsService(ContactsRepository contactsRepository) {
        this.contactsRepository = contactsRepository;
    }

    public Iterable<Contacts> findAll() {
        return contactsRepository.findAll();
    }

    public Contacts getContacts(Long id) {
        return contactsRepository.findById(id).orElseThrow(ContentNotFoundException::new);
    }
    public Contacts saveContact(ContactsDto contactsDto) {
        return contactsRepository.save(Contacts.of(
                contactsDto.phoneNumber(),
                contactsDto.address(),
                contactsDto.email(),
                contactsDto.telegram(),
                contactsDto.vkLink(),
                contactsDto.youtubeLink(),
                contactsDto.rutubeLink()));
    }

    public Contacts updateContact(Long id, ContactsDto contactsDto) {
        return contactsRepository.findById(id)
                .map(existingContacts -> new Contacts(
                        existingContacts.id(),
                        contactsDto.phoneNumber(),
                        contactsDto.email(),
                        contactsDto.address(),
                        contactsDto.telegram(),
                        contactsDto.vkLink(),
                        contactsDto.youtubeLink(),
                        contactsDto.rutubeLink()))
                .map(contactsRepository::save)
                .orElseThrow(ContentNotFoundException::new);
    }

    public void deleteContactsById(long id) {
        contactsRepository.deleteById(id);
    }
}
