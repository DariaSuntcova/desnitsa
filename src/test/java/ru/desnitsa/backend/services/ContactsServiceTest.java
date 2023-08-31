package ru.desnitsa.backend.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.desnitsa.backend.dto.ContactsDto;
import ru.desnitsa.backend.entities.Contacts;
import ru.desnitsa.backend.repositories.ContactsRepository;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

class ContactsServiceTest {
//    @Mock
//    private ContactsRepository contactsRepository;
//
//    @InjectMocks
//    private ContactsService contactsService;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    void testUpdateOrAddContact_ExistingContact() {
//
//        long id = 1L;
//        ContactsDto contactsDto = new ContactsDto();
//        contactsDto.setPhoneNumber("123456789");
//        contactsDto.setAddress("Test Address");
//        contactsDto.setTelegram("testtelegram");
//        contactsDto.setVkLink("testvklink");
//        contactsDto.setYoutubeLink("testyoutubelink");
//        contactsDto.setRutubeLink("testrutubelink");
//
//        Contacts existingContact = new Contacts();
//        existingContact.setId(id);
//
//        when(contactsRepository.findById(id)).thenReturn(Optional.of(existingContact));
//
//        Contacts result = contactsService.saveContact(contactsDto);
//
//        verify(contactsRepository, times(1)).findById(id);
//        verify(contactsRepository, times(1)).save(existingContact);
//
//        assertEquals(contactsDto.getPhoneNumber(), existingContact.getPhoneNumber());
//        assertEquals(contactsDto.getAddress(), existingContact.getAddress());
//        assertEquals(contactsDto.getTelegram(), existingContact.getTelegram());
//        assertEquals(contactsDto.getVkLink(), existingContact.getVkLink());
//        assertEquals(contactsDto.getYoutubeLink(), existingContact.getYoutubeLink());
//        assertEquals(contactsDto.getRutubeLink(), existingContact.getRutubeLink());
//        assertEquals(existingContact, result);
//    }
//
//    @Test
//    void testUpdateOrAddContact_NewContact() {
//
//        long id = 1L;
//        ContactsDto contactsDto = new ContactsDto();
//        contactsDto.setPhoneNumber("123456789");
//        contactsDto.setAddress("Test Address");
//        contactsDto.setTelegram("testtelegram");
//        contactsDto.setVkLink("testvklink");
//        contactsDto.setYoutubeLink("testyoutubelink");
//        contactsDto.setRutubeLink("testrutubelink");
//
//        when(contactsRepository.findById(id)).thenReturn(Optional.empty());
//
//        Contacts result = contactsService.saveContact(contactsDto);
//
//        verify(contactsRepository, times(1)).findById(id);
//        verify(contactsRepository, times(1)).save(any(Contacts.class));
//
//        assertEquals(id, result.getId());
//        assertEquals(contactsDto.getPhoneNumber(), result.getPhoneNumber());
//        assertEquals(contactsDto.getAddress(), result.getAddress());
//        assertEquals(contactsDto.getTelegram(), result.getTelegram());
//        assertEquals(contactsDto.getVkLink(), result.getVkLink());
//        assertEquals(contactsDto.getYoutubeLink(), result.getYoutubeLink());
//        assertEquals(contactsDto.getRutubeLink(), result.getRutubeLink());
//    }
//
//    @Test
//    void testDeleteContactsById() {
//        long id = 1L;
//        contactsService.deleteContactsById(id);
//        verify(contactsRepository, times(1)).deleteById(id);
//    }
}
