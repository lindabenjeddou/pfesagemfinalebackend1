package tn.esprit.PI.util;


import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NotFoundException extends RuntimeException {

    public NotFoundException() {
        log.info("error");
    }

    public NotFoundException(final String message) {
        log.error(message);
    }

}
