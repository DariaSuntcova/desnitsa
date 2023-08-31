package ru.desnitsa.backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.desnitsa.backend.dto.ContactsDto;
import ru.desnitsa.backend.entities.Contacts;
import ru.desnitsa.backend.services.ContactsService;

@RestController
@RequestMapping("/contacts")
public class ContactsController {
    private final ContactsService contactService;

    @Autowired
    public ContactsController(ContactsService contactsService) {
        this.contactService = contactsService;
    }

    @GetMapping
    public Iterable<Contacts> ContactAll() {
        return contactService.findAll();
    }

    @GetMapping("{id}")
    public Contacts getContacts(@PathVariable Long id) {
        return contactService.getContacts(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Contacts saveContact(@RequestBody ContactsDto contactsDto) {
        return contactService.saveContact(contactsDto);
    }

    @PutMapping("{id}")
    public Contacts updateContact(@PathVariable long id, @RequestBody ContactsDto contactsDto) {
        return contactService.updateContact(id, contactsDto);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteContact(@PathVariable long id) {
        contactService.deleteContactsById(id);
    }
}
