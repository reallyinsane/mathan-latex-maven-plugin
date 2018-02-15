package io.mathan.maven.it;

public class VerifierException extends Exception {

  public VerifierException(String message) {
    super(message);
  }

  public VerifierException(String message, Throwable cause) {
    super(message, cause);
  }
}
