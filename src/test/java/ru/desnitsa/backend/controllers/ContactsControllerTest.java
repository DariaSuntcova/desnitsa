package ru.desnitsa.backend.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.desnitsa.backend.dto.ContactsDto;
import ru.desnitsa.backend.entities.Contacts;
import ru.desnitsa.backend.services.ContactsService;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ContactsController.class)
public class ContactsControllerTest {
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockBean
//    private ContactsService contactsService;
//
//    @Test
//    public void testGetAllContacts() throws Exception {
//        Contacts contact1 = new Contacts();
//        contact1.setId(1L);
//        contact1.setPhoneNumber("123456789");
//        contact1.setAddress("Address 1");
//
//        Contacts contact2 = new Contacts();
//        contact2.setId(2L);
//        contact2.setPhoneNumber("987654321");
//        contact2.setAddress("Address 2");
//
//        List<Contacts> contactsList = Arrays.asList(contact1, contact2);
//
//        when(contactsService.findAll()).thenReturn(contactsList);
//
//        mockMvc.perform(MockMvcRequestBuilders.get("/contacts"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$[0].id").value(1))
//                .andExpect(jsonPath("$[0].phonenumber").value("123456789"))
//                .andExpect(jsonPath("$[0].address").value("Address 1"))
//                .andExpect(jsonPath("$[1].id").value(2))
//                .andExpect(jsonPath("$[1].phonenumber").value("987654321"))
//                .andExpect(jsonPath("$[1].address").value("Address 2"));
//
//        verify(contactsService, times(1)).findAll();
//    }
//
//    @Test
//    public void testAddAndUpdateContact() throws Exception {
//        Long id = 1L;
//        ContactsDto contactsDto = new ContactsDto();
//        contactsDto.setPhoneNumber("123456789");
//        contactsDto.setAddress("Test Address");
//
//        Contacts contact = new Contacts();
//        contact.setId(id);
//        contact.setPhoneNumber(contactsDto.getPhoneNumber());
//        contact.setAddress(contactsDto.getAddress());
//
//        when(contactsService.saveContact(contactsDto)).thenReturn(contact);
//
//        mockMvc.perform(MockMvcRequestBuilders.post("/contacts")
//                        .contentType("application/json")
//                        .content("{\"phonenumber\": \"123456789\", \"address\": \"Test Address\"}"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id").value(1))
//                .andExpect(jsonPath("$.phonenumber").value("123456789"))
//                .andExpect(jsonPath("$.address").value("Test Address"));
//
//        verify(contactsService, times(1)).saveContact(contactsDto);
//    }
//
//    @Test
//    public void testDeleteContact() throws Exception {
//        long id = 1L;
//
//        mockMvc.perform(MockMvcRequestBuilders.delete("/contacts/{id}", id))
//                .andExpect(status().isOk());
//
//        verify(contactsService, times(1)).deleteContactsById(id);
//    }
}
