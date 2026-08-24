package com.example.Usuarios.infrastructure.input.rest;

import com.example.Usuarios.infrastructure.configuration.security.JwtService;
import com.example.Usuarios.infrastructure.out.jpa.entity.RolEntity;
import com.example.Usuarios.infrastructure.out.jpa.entity.UsuarioEntity;
import com.example.Usuarios.infrastructure.out.jpa.repository.IUsuarioRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("HU-5: Autenticación y autorización")
class AuthRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private IUsuarioRepository usuarioRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private TransactionTemplate transactionTemplate;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @BeforeAll
    void setUp() {
        transactionTemplate.executeWithoutResult(status -> {
            entityManager.createNativeQuery("DELETE FROM usuarios").executeUpdate();
            entityManager.createNativeQuery("DELETE FROM roles").executeUpdate();

            entityManager.createNativeQuery(
                    "INSERT INTO roles (id, nombre, descripcion) VALUES (1, 'ADMIN', 'Administrador')"
            ).executeUpdate();
            entityManager.createNativeQuery(
                    "INSERT INTO roles (id, nombre, descripcion) VALUES (2, 'PROPIETARIO', 'Propietario')"
            ).executeUpdate();

            RolEntity rolAdmin = entityManager.find(RolEntity.class, 1L);
            RolEntity rolPropietario = entityManager.find(RolEntity.class, 2L);

            UsuarioEntity admin = new UsuarioEntity();
            admin.setNombre("Admin");
            admin.setApellido("Sistema");
            admin.setNumeroDocumento("1111111111");
            admin.setCelular("+573001111111");
            admin.setFechaNacimiento(LocalDate.of(1980, 1, 1));
            admin.setCorreo("admin@test.com");
            admin.setClave(encoder.encode("admin123"));
            admin.setRol(rolAdmin);
            entityManager.persist(admin);

            UsuarioEntity propietario = new UsuarioEntity();
            propietario.setNombre("Carlos");
            propietario.setApellido("Martinez");
            propietario.setNumeroDocumento("2222222222");
            propietario.setCelular("+573002222222");
            propietario.setFechaNacimiento(LocalDate.of(1985, 6, 15));
            propietario.setCorreo("propietario@test.com");
            propietario.setClave(encoder.encode("password123"));
            propietario.setRol(rolPropietario);
            entityManager.persist(propietario);
        });
    }

    @Nested
    @DisplayName("POST /auth/login")
    class Login {

        @Test
        @DisplayName("login exitoso retorna token JWT")
        void loginExitosoRetornaToken() throws Exception {
            String body = """
                    {"correo": "admin@test.com", "clave": "admin123"}
                    """;

            mockMvc.perform(post("/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.token").isNotEmpty());
        }

        @Test
        @DisplayName("login con correo inexistente retorna error")
        void loginCorreoInexistente() throws Exception {
            String body = """
                    {"correo": "noexiste@test.com", "clave": "password123"}
                    """;

            mockMvc.perform(post("/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").value("Credenciales inválidas"));
        }

        @Test
        @DisplayName("login con clave incorrecta retorna error")
        void loginClaveIncorrecta() throws Exception {
            String body = """
                    {"correo": "admin@test.com", "clave": "claveErronea"}
                    """;

            mockMvc.perform(post("/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").value("Credenciales inválidas"));
        }
    }

    @Nested
    @DisplayName("POST /auth/validate")
    class ValidateToken {

        @Test
        @DisplayName("token válido retorna datos del usuario")
        void tokenValidoRetornaDatos() throws Exception {
            String token = jwtService.generateToken("admin@test.com", "ADMIN", 1L);

            mockMvc.perform(post("/auth/validate")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.valid").value(true))
                    .andExpect(jsonPath("$.correo").value("admin@test.com"))
                    .andExpect(jsonPath("$.rol").value("ADMIN"));
        }

        @Test
        @DisplayName("token inválido retorna 401")
        void tokenInvalidoRetorna401() throws Exception {
            mockMvc.perform(post("/auth/validate")
                            .header("Authorization", "Bearer token.invalido.aqui"))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.valid").value(false));
        }
    }

    @Nested
    @DisplayName("Autorización por rol")
    class Autorizacion {

        @Test
        @DisplayName("admin puede crear propietario")
        void adminPuedeCrearPropietario() throws Exception {
            String token = jwtService.generateToken("admin@test.com", "ADMIN", 1L);

            String body = """
                    {
                        "nombre": "Nuevo",
                        "apellido": "Propietario",
                        "numeroDocumento": "9999999999",
                        "celular": "+573009999999",
                        "fechaNacimiento": "1990-05-15",
                        "correo": "nuevo.prop@mail.com",
                        "clave": "Password123"
                    }
                    """;

            mockMvc.perform(post("/usuarios/propietario")
                            .header("Authorization", "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isCreated());
        }

        @Test
        @DisplayName("propietario no puede crear propietario - retorna 403")
        void propietarioNoPuedeCrearPropietario() throws Exception {
            String token = jwtService.generateToken("propietario@test.com", "PROPIETARIO", 2L);

            String body = """
                    {
                        "nombre": "Otro",
                        "apellido": "Propietario",
                        "numeroDocumento": "8888888888",
                        "celular": "+573008888888",
                        "fechaNacimiento": "1990-05-15",
                        "correo": "otro.prop@mail.com",
                        "clave": "Password123"
                    }
                    """;

            mockMvc.perform(post("/usuarios/propietario")
                            .header("Authorization", "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("sin token no puede crear propietario - retorna 401")
        void sinTokenNoPuedeCrearPropietario() throws Exception {
            String body = """
                    {
                        "nombre": "Sin",
                        "apellido": "Auth",
                        "numeroDocumento": "7777777777",
                        "celular": "+573007777777",
                        "fechaNacimiento": "1990-05-15",
                        "correo": "sin.auth@mail.com",
                        "clave": "Password123"
                    }
                    """;

            mockMvc.perform(post("/usuarios/propietario")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("usuario autenticado puede consultar usuario por id")
        void usuarioAutenticadoPuedeConsultarPorId() throws Exception {
            String token = jwtService.generateToken("propietario@test.com", "PROPIETARIO", 2L);

            // Find the actual ID
            UsuarioEntity propietario = usuarioRepository.findByCorreo("propietario@test.com").orElseThrow();

            mockMvc.perform(get("/usuarios/" + propietario.getId())
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.correo").value("propietario@test.com"));
        }
    }
}
