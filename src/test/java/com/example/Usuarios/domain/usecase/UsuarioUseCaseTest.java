package com.example.Usuarios.domain.usecase;

import com.example.Usuarios.application.handler.IPasswordHandler;
import com.example.Usuarios.domain.model.Usuario;
import com.example.Usuarios.domain.spi.IUsuarioPersistencePort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("HU-1: crear la cuenta de un propietario")
class  UsuarioUseCaseTest {

    private static final Long ROL_PROPIETARIO = 2L;
    private static final String CLAVE_PLANA = "Secreta123";
    private static final String CLAVE_ENCRIPTADA = "$2a$10$hashDeBCryptSimulado";

    @Mock
    private IUsuarioPersistencePort usuarioPersistencePort;

    @Mock
    private IPasswordHandler passwordHandler;

    @InjectMocks
    private UsuarioUseCase usuarioUseCase;

    @Captor
    private ArgumentCaptor<Usuario> usuarioCaptor;


    private Usuario propietarioValido() {
        return Usuario.builder()
                .nombre("Ana")
                .apellido("Gomez")
                .numeroDocumento("1234567890")
                .celular("+573005698325")
                .fechaNacimiento(LocalDate.now().minusYears(30))
                .correo("ana.gomez@example.com")
                .clave(CLAVE_PLANA)
                .build();
    }

    private Usuario capturarPropietarioGuardado() {
        verify(usuarioPersistencePort).guardarPropietario(usuarioCaptor.capture());
        return usuarioCaptor.getValue();
    }

    @Nested
    @DisplayName("Criterio 1: los datos del propietario se entregan al puerto de persistencia")
    class Persistencia {

        @Test
        @DisplayName("conserva los demas campos obligatorios sin alterarlos")
        void conservaLosCamposObligatorios() {
            when(passwordHandler.encode(CLAVE_PLANA)).thenReturn(CLAVE_ENCRIPTADA);
            Usuario entrada = propietarioValido();

            usuarioUseCase.crearPropietario(entrada);

            Usuario guardado = capturarPropietarioGuardado();
            assertThat(guardado.getNombre()).isEqualTo("Ana");
            assertThat(guardado.getApellido()).isEqualTo("Gomez");
            assertThat(guardado.getNumeroDocumento()).isEqualTo("1234567890");
            assertThat(guardado.getCelular()).isEqualTo("+573005698325");
            assertThat(guardado.getCorreo()).isEqualTo("ana.gomez@example.com");
            assertThat(guardado.getFechaNacimiento()).isEqualTo(entrada.getFechaNacimiento());
        }
    }

    @Nested
    @DisplayName("Criterio 2: el documento de identidad debe ser unicamente numerico")
    class DocumentoDeIdentidad {

        @ParameterizedTest(name = "documento invalido: \"{0}\"")
        @ValueSource(strings = {"12345A", "ABCDEF", "123 456", "123-456", "123.456", "+123456", ""})
        @DisplayName("rechaza documentos que no son solo digitos")
        void rechazaDocumentoNoNumerico(String documento) {
            Usuario entrada = propietarioValido();
            entrada.setNumeroDocumento(documento);

            assertThatThrownBy(() -> usuarioUseCase.crearPropietario(entrada))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("documento");

            verifyNoInteractions(usuarioPersistencePort);
        }

        @ParameterizedTest(name = "documento valido: \"{0}\"")
        @ValueSource(strings = {"1", "1234567890", "00099988877"})
        @DisplayName("acepta documentos compuestos solo por digitos")
        void aceptaDocumentoNumerico(String documento) {
            when(passwordHandler.encode(CLAVE_PLANA)).thenReturn(CLAVE_ENCRIPTADA);
            Usuario entrada = propietarioValido();
            entrada.setNumeroDocumento(documento);

            usuarioUseCase.crearPropietario(entrada);

            assertThat(capturarPropietarioGuardado().getNumeroDocumento()).isEqualTo(documento);
        }
    }

    @Nested
    @DisplayName("Criterio 2: el celular admite el simbolo + y maximo 13 caracteres")
    class Celular {

        @ParameterizedTest(name = "celular valido: \"{0}\"")
        @ValueSource(strings = {"+573005698325", "573005698325", "3005698325", "+1", "1"})
        @DisplayName("acepta el ejemplo de la HU y celulares dentro del limite")
        void aceptaCelularValido(String celular) {
            when(passwordHandler.encode(CLAVE_PLANA)).thenReturn(CLAVE_ENCRIPTADA);
            Usuario entrada = propietarioValido();
            entrada.setCelular(celular);

            usuarioUseCase.crearPropietario(entrada);

            assertThat(capturarPropietarioGuardado().getCelular()).isEqualTo(celular);
        }

        @Test
        @DisplayName("el ejemplo de la HU +573005698325 mide exactamente 13 caracteres")
        void elEjemploDeLaHuMide13Caracteres() {
            assertThat("+573005698325").hasSize(13);
        }

        @ParameterizedTest(name = "celular invalido: \"{0}\"")
        @ValueSource(strings = {
                "+5730056983251",  // 14 caracteres, excede el limite
                "300569A325",      // contiene letras
                "300 569 8325",    // contiene espacios
                "300-569-8325",    // contiene guiones
                "57+3005698325",   // el + no esta al inicio
                ""                 // vacio
        })
        @DisplayName("rechaza celulares fuera de formato o de longitud")
        void rechazaCelularInvalido(String celular) {
            Usuario entrada = propietarioValido();
            entrada.setCelular(celular);

            assertThatThrownBy(() -> usuarioUseCase.crearPropietario(entrada))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("celular");

            verifyNoInteractions(usuarioPersistencePort);
        }

        @Test
        @DisplayName("COMPORTAMIENTO ACTUAL: 13 digitos sin + se rechazan aunque midan 13 caracteres")
        void treceDigitosSinMasSeRechazan() {
            // El regex es ^\+?\d{1,12}$, es decir maximo 12 digitos. Un numero de 13
            // digitos mide 13 caracteres y una lectura literal del criterio 2 lo
            // aceptaria. Esta prueba fija el comportamiento actual, no lo aprueba.
            Usuario entrada = propietarioValido();
            entrada.setCelular("3005698325123");

            assertThat("3005698325123").hasSize(13);
            assertThatThrownBy(() -> usuarioUseCase.crearPropietario(entrada))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("celular");
        }
    }

    @Nested
    @DisplayName("Criterio 2: el correo debe tener una estructura valida")
    class Correo {

        @ParameterizedTest(name = "correo valido: \"{0}\"")
        @ValueSource(strings = {
                "ana.gomez@example.com",
                "ana+etiqueta@example.com",
                "ana_gomez@sub.example.co",
                "ana-gomez@example.org"
        })
        @DisplayName("acepta correos con estructura valida")
        void aceptaCorreoValido(String correo) {
            when(passwordHandler.encode(CLAVE_PLANA)).thenReturn(CLAVE_ENCRIPTADA);
            Usuario entrada = propietarioValido();
            entrada.setCorreo(correo);

            usuarioUseCase.crearPropietario(entrada);

            assertThat(capturarPropietarioGuardado().getCorreo()).isEqualTo(correo);
        }

        @ParameterizedTest(name = "correo invalido: \"{0}\"")
        @ValueSource(strings = {
                "anagomez.example.com",  // sin arroba
                "ana@",                  // sin dominio
                "@example.com",          // sin parte local
                "ana gomez@example.com", // contiene espacio
                ""                       // vacio
        })
        @DisplayName("rechaza correos sin estructura valida")
        void rechazaCorreoInvalido(String correo) {
            Usuario entrada = propietarioValido();
            entrada.setCorreo(correo);

            assertThatThrownBy(() -> usuarioUseCase.crearPropietario(entrada))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("correo");

            verifyNoInteractions(usuarioPersistencePort);
        }

        @Test
        @DisplayName("COMPORTAMIENTO ACTUAL: un dominio sin punto (ana@example) se acepta")
        void dominioSinPuntoSeAcepta() {
            // El regex ^[A-Za-z0-9+_.-]+@(.+)$ solo exige un caracter despues del @,
            // por lo que no valida el TLD. Esta prueba fija el comportamiento actual.
            when(passwordHandler.encode(CLAVE_PLANA)).thenReturn(CLAVE_ENCRIPTADA);
            Usuario entrada = propietarioValido();
            entrada.setCorreo("ana@example");

            usuarioUseCase.crearPropietario(entrada);

            assertThat(capturarPropietarioGuardado().getCorreo()).isEqualTo("ana@example");
        }
    }

    @Nested
    @DisplayName("Criterio 3: el usuario queda con el rol propietario")
    class RolPropietario {

        @Test
        @DisplayName("asigna el rol propietario (id 2) ignorando cualquier rol recibido")
        void asignaRolPropietario() {
            when(passwordHandler.encode(CLAVE_PLANA)).thenReturn(CLAVE_ENCRIPTADA);
            Usuario entrada = propietarioValido();
            entrada.setRolId(99L);

            usuarioUseCase.crearPropietario(entrada);

            assertThat(capturarPropietarioGuardado().getRolId()).isEqualTo(ROL_PROPIETARIO);
        }
    }

    @Nested
    @DisplayName("Criterio 4: el usuario debe ser mayor de edad")
    class MayorDeEdad {

        @Test
        @DisplayName("acepta a quien cumple exactamente 18 anios hoy")
        void aceptaAlQueCumple18Hoy() {
            when(passwordHandler.encode(CLAVE_PLANA)).thenReturn(CLAVE_ENCRIPTADA);
            Usuario entrada = propietarioValido();
            entrada.setFechaNacimiento(LocalDate.now().minusYears(18));

            usuarioUseCase.crearPropietario(entrada);

            verify(usuarioPersistencePort).guardarPropietario(any(Usuario.class));
        }

        @Test
        @DisplayName("rechaza a quien cumple 18 anios maniana")
        void rechazaAlQueCumple18Maniana() {
            Usuario entrada = propietarioValido();
            entrada.setFechaNacimiento(LocalDate.now().minusYears(18).plusDays(1));

            assertThatThrownBy(() -> usuarioUseCase.crearPropietario(entrada))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("El usuario debe ser mayor de edad");

            verifyNoInteractions(usuarioPersistencePort);
        }

        @Test
        @DisplayName("rechaza a un menor de edad")
        void rechazaMenorDeEdad() {
            Usuario entrada = propietarioValido();
            entrada.setFechaNacimiento(LocalDate.now().minusYears(10));

            assertThatThrownBy(() -> usuarioUseCase.crearPropietario(entrada))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("El usuario debe ser mayor de edad");

            verifyNoInteractions(usuarioPersistencePort);
        }
    }

    @Nested
    @DisplayName("HU-6: crear la cuenta de un empleado")
    class CrearEmpleado {

        private Usuario empleadoValido() {
            return Usuario.builder()
                    .nombre("Pedro")
                    .apellido("Garcia")
                    .numeroDocumento("9876543210")
                    .celular("+573005551234")
                    .correo("pedro.garcia@example.com")
                    .clave(CLAVE_PLANA)
                    .build();
        }

        @Test
        @DisplayName("crea empleado con datos validos y asigna rol 3")
        void creaEmpleadoConDatosValidos() {
            when(passwordHandler.encode(CLAVE_PLANA)).thenReturn(CLAVE_ENCRIPTADA);
            Usuario entrada = empleadoValido();

            usuarioUseCase.crearEmpleado(entrada);

            verify(usuarioPersistencePort).guardarEmpleado(usuarioCaptor.capture());
            Usuario guardado = usuarioCaptor.getValue();
            assertThat(guardado.getRolId()).isEqualTo(3L);
            assertThat(guardado.getClave()).isEqualTo(CLAVE_ENCRIPTADA);
            assertThat(guardado.getNombre()).isEqualTo("Pedro");
            assertThat(guardado.getCorreo()).isEqualTo("pedro.garcia@example.com");
        }

        @Test
        @DisplayName("rechaza empleado con documento no numerico")
        void rechazaDocumentoNoNumerico() {
            Usuario entrada = empleadoValido();
            entrada.setNumeroDocumento("123ABC");

            assertThatThrownBy(() -> usuarioUseCase.crearEmpleado(entrada))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("documento");

            verifyNoInteractions(usuarioPersistencePort);
        }

        @Test
        @DisplayName("rechaza empleado con celular invalido")
        void rechazaCelularInvalido() {
            Usuario entrada = empleadoValido();
            entrada.setCelular("300-invalid");

            assertThatThrownBy(() -> usuarioUseCase.crearEmpleado(entrada))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("celular");

            verifyNoInteractions(usuarioPersistencePort);
        }

        @Test
        @DisplayName("rechaza empleado con correo invalido")
        void rechazaCorreoInvalido() {
            Usuario entrada = empleadoValido();
            entrada.setCorreo("correo-sin-arroba");

            assertThatThrownBy(() -> usuarioUseCase.crearEmpleado(entrada))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("correo");

            verifyNoInteractions(usuarioPersistencePort);
        }

        @Test
        @DisplayName("siempre asigna rol empleado (3) sin importar el rolId recibido")
        void siempreAsignaRolEmpleado() {
            when(passwordHandler.encode(CLAVE_PLANA)).thenReturn(CLAVE_ENCRIPTADA);
            Usuario entrada = empleadoValido();
            entrada.setRolId(99L);

            usuarioUseCase.crearEmpleado(entrada);

            verify(usuarioPersistencePort).guardarEmpleado(usuarioCaptor.capture());
            assertThat(usuarioCaptor.getValue().getRolId()).isEqualTo(3L);
        }
    }


}
