package com.mercado.altiplano.security;

import org.springframework.stereotype.Component;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Cifra y verifica contraseñas con PBKDF2 (viene incluido en Java, no necesita librerías extra).
 *
 * Cada contraseña lleva su propia "sal" aleatoria, así dos usuarios con la misma clave
 * no terminan con el mismo hash. Formato guardado:
 * pbkdf2_sha256$iteraciones$sal$hash
 */
@Component
public class PasswordHasher {

    private static final String ALGORITMO = "PBKDF2WithHmacSHA256";
    private static final String PREFIJO = "pbkdf2_sha256";
    private static final int ITERACIONES_POR_DEFECTO = 600_000;
    private static final int BYTES_SAL = 16;
    private static final int BITS_HASH = 256;

    private final SecureRandom azar = new SecureRandom();
    private final int iteraciones;

    public PasswordHasher() {
        this(ITERACIONES_POR_DEFECTO);
    }

    /** Se usa en las pruebas para que no tarden tanto. */
    public PasswordHasher(int iteraciones) {
        this.iteraciones = iteraciones;
    }

    public String cifrar(String passwordPlano) {
        byte[] sal = new byte[BYTES_SAL];
        azar.nextBytes(sal);
        byte[] hash = derivar(passwordPlano, sal, iteraciones, BITS_HASH);

        return String.join("$",
                PREFIJO,
                String.valueOf(iteraciones),
                Base64.getEncoder().encodeToString(sal),
                Base64.getEncoder().encodeToString(hash));
    }

    public boolean coincide(String passwordPlano, String almacenado) {
        if (passwordPlano == null || almacenado == null) {
            return false;
        }

        String[] partes = almacenado.split("\\$");
        if (partes.length != 4 || !PREFIJO.equals(partes[0])) {
            return false;
        }

        try {
            int iter = Integer.parseInt(partes[1]);
            byte[] sal = Base64.getDecoder().decode(partes[2]);
            byte[] esperado = Base64.getDecoder().decode(partes[3]);
            byte[] obtenido = derivar(passwordPlano, sal, iter, esperado.length * 8);

            // Comparación en tiempo constante para no dar pistas por la demora.
            return MessageDigest.isEqual(esperado, obtenido);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private static byte[] derivar(String password, byte[] sal, int iteraciones, int bits) {
        PBEKeySpec especificacion = new PBEKeySpec(password.toCharArray(), sal, iteraciones, bits);
        try {
            return SecretKeyFactory.getInstance(ALGORITMO)
                    .generateSecret(especificacion)
                    .getEncoded();
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("No se pudo cifrar la contraseña", e);
        } finally {
            especificacion.clearPassword();
        }
    }
}
