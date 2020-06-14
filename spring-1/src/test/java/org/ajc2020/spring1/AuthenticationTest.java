package org.ajc2020.spring1;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.ajc2020.spring1.config.KIBeConfig;
import org.ajc2020.spring1.filter.AuthFilter;
import org.ajc2020.spring1.manager.SessionManager;
import org.ajc2020.spring1.model.Admin;
import org.ajc2020.spring1.model.PermissionLevel;
import org.ajc2020.spring1.model.Worker;
import org.ajc2020.spring1.service.AdminServiceImpl;
import org.ajc2020.spring1.service.WorkerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Base64;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuthenticationTest {

    @Autowired
    private AuthFilter authFilter;

    @Autowired
    private SessionManager sessionManager;

    @Autowired
    private KIBeConfig config;

    private MockMvc mockMvc;

    @MockBean
    WorkerServiceImpl workerService;

    @MockBean
    AdminServiceImpl adminService;

    private Worker worker1;
    private Worker worker2;

    private Admin admin1;
    private Admin admin2;

    private String baseEncode(String email, String password) {
        return Base64.getEncoder().encodeToString((email + ":" + password).getBytes());
    }

    @BeforeEach
    public void setUp() {
        worker1 = new Worker();
        worker1.setEmail("email1");
        worker1.setPassword("password1");
        worker2 = new Worker();
        worker2.setEmail("email2");
        worker2.setPassword("password2");
        admin1 = new Admin();
        admin1.setEmail("emailAdmin1");
        admin1.setPassword("adminPassword1");
        admin1.setSuperAdmin(false);
        admin2 = new Admin();
        admin2.setEmail("emailAdmin2");
        admin2.setPassword("adminPassword2");
        admin2.setSuperAdmin(true);

        mockMvc = MockMvcBuilders
                .standaloneSetup(new TestController())
                .addFilters(authFilter)
                .build();

        Mockito.when(workerService.findByEmail(worker1.getEmail())).thenReturn(Optional.of(worker1));
        Mockito.when(workerService.findByEmail(worker2.getEmail())).thenReturn(Optional.of(worker2));
        Mockito.when(adminService.findByEmail(admin1.getEmail())).thenReturn(Optional.of(admin1));
        Mockito.when(adminService.findByEmail(admin2.getEmail())).thenReturn(Optional.of(admin2));
    }

    @RestController
    private class TestController {
        @GetMapping("/me")
        public ResponseEntity<Object> returnMe() {
            try {
                return ResponseEntity.ok(sessionManager.getSession());
            } catch (NullPointerException ex) {
                // Spring security config won't allow unauthorized to access endpoints.
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No user set");
            }
        }

        @GetMapping("/worker")
        public ResponseEntity<Worker> returnWorkerMe() {
            try {
                assertTrue(sessionManager.isSessionWorker());
                assertEquals(PermissionLevel.WORKER, sessionManager.getPermission());
                return ResponseEntity.ok(sessionManager.getWorker());
            } catch (NullPointerException ex) {
                // Spring security config won't allow unauthorized to access endpoints.
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No user set");
            }
        }

        @GetMapping("/admin")
        public ResponseEntity<Admin> returnAdminMe() {
            try {
                assertTrue(sessionManager.isSessionAdmin());
                assertEquals(PermissionLevel.ADMIN, sessionManager.getPermission());
                return ResponseEntity.ok(sessionManager.getAdmin());
            } catch (NullPointerException ex) {
                // Spring security config won't allow unauthorized to access endpoints.
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No user set");
            }
        }

        @GetMapping("/super-admin")
        public ResponseEntity<Admin> returnSuperAdminMe() {
            try {
                assertTrue(sessionManager.isSessionAdmin());
                assertEquals(PermissionLevel.SUPER_ADMIN, sessionManager.getPermission());
                return ResponseEntity.ok(sessionManager.getAdmin());
            } catch (NullPointerException ex) {
                // Spring security config won't allow unauthorized to access endpoints.
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No user set");
            }
        }

        @GetMapping("/device")
        public ResponseEntity<Void> returnDeviceMe() {
            try {
                assertEquals(PermissionLevel.DEVICE, sessionManager.getPermission());
                return ResponseEntity.ok().build();
            } catch (NullPointerException ex) {
                // Spring security config won't allow unauthorized to access endpoints.
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No user set");
            }
        }
    }

    @Test
    public void normalFunction() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        String base64User1 = baseEncode(worker1.getEmail(), worker1.getPassword());
        MvcResult resultUser1 = mockMvc.perform(get("/me").header("Authorization", "Basic " + base64User1).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        assertEquals(mapper.writeValueAsString(worker1), resultUser1.getResponse().getContentAsString());
        mockMvc.perform(get("/worker").header("Authorization", "Basic " + base64User1).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful());

        String base64User2 = baseEncode(worker2.getEmail(), worker2.getPassword());
        MvcResult resultUser2 = mockMvc.perform(get("/me").header("Authorization", "Basic " + base64User2).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        assertEquals(mapper.writeValueAsString(worker2), resultUser2.getResponse().getContentAsString());
        mockMvc.perform(get("/worker").header("Authorization", "Basic " + base64User2).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful());

        String base64Admin1 = baseEncode(admin1.getEmail(), admin1.getPassword());
        MvcResult resultAdmin1 = mockMvc.perform(get("/me").header("Authorization", "Basic " + base64Admin1).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andDo(print())
                .andReturn();

        assertEquals(mapper.writeValueAsString(admin1), resultAdmin1.getResponse().getContentAsString());
        mockMvc.perform(get("/admin").header("Authorization", "Basic " + base64Admin1).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is2xxSuccessful());

        String base64Admin2 = baseEncode(admin2.getEmail(), admin2.getPassword());
        MvcResult resultAdmin2 = mockMvc.perform(get("/me").header("Authorization", "Basic " + base64Admin2).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andDo(print())
                .andReturn();

        assertEquals(mapper.writeValueAsString(admin2), resultAdmin2.getResponse().getContentAsString());
        mockMvc.perform(get("/super-admin").header("Authorization", "Basic " + base64Admin2).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is2xxSuccessful());

        mockMvc.perform(get("/device").header("Authorization", config.getDevice().getAuthorizationType() + " " + config.getDevice().getToken())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andDo(print());
    }

    @Test
    public void invalidUsernameAndPassword() throws Exception {
        String base64InvalidUser1 = baseEncode(worker1.getEmail(), worker1.getPassword() + "bad");
        mockMvc.perform(get("/me").header("Authorization", "Basic " + base64InvalidUser1).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

        String base64NoUsername = baseEncode("", "bad");
        mockMvc.perform(get("/me").header("Authorization", "Basic " + base64NoUsername).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

        String base64NoPassword = baseEncode("bady", "");
        mockMvc.perform(get("/me").header("Authorization", "Basic " + base64NoPassword).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

        String base64NoNothing = baseEncode("", "");
        mockMvc.perform(get("/me").header("Authorization", "Basic " + base64NoNothing).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

}
