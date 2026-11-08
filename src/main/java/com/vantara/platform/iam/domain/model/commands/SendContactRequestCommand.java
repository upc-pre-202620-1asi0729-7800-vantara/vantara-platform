package com.vantara.platform.iam.domain.model.commands;

/**
 * Command to send the contact form ("send contact form").
 *
 * @param fullName name of the person who contacts Vantara
 * @param email    email address of the requester
 * @param message  message written in the form
 */
public record SendContactRequestCommand(String fullName, String email, String message) {
}
