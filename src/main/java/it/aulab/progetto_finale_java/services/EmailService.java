package it.aulab.progetto_finale_java.services;

public interface EmailService {
    void sendSimpleEmail(String to, String subject, String text);
}
