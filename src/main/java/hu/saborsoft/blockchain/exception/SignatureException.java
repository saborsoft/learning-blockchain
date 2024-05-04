package hu.saborsoft.blockchain.exception;

public class SignatureException extends RuntimeException {

    public SignatureException(String message, Throwable t) {
        super(message, t);
    }
}
